package com.stegosaurus.jpeg;

import gnu.trove.list.TByteList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.NumUtils;
import com.stegosaurus.stegutils.ZigZag;

public class JPEGDecompressor extends JPEGProcessor {
  /**
   * Simply invokes the corresponding parent constructor.
   * @param image an inputstream containing the JPEG image to decompress.
   */
  public JPEGDecompressor(InputStream image) {
    super(image);
  }

  /**
   * Invokes the corresponding parent constructor.
   * @param data the data for the JPEG image.
   */
  public JPEGDecompressor(byte[] data) {
    super(data);
  }

  /**
   * Extend some additional bits read from the stream and their magnitude
   * into the actual value encoded.
   * @param additional the bits read from the stream.
   * @param magnitude the number of bits read from the stream.
   */
  private static int extend(int additional, int magnitude) {
    int vt = 1 << (magnitude - 1);
    if(additional < vt) {
      return additional + (-1 << magnitude) + 1;
    } else {
      return additional;
    }
  }

  /**
   * Decode the next dc coefficient in the input stream given.
   * @param decoder the huffman decoder to use
   * @param in the bit input stream containing scan data
   * @param lastDc the last dc coefficient decoded for this component.
   */
  private int decodeDC(HuffmanDecoder decoder, BitInputStream in, int lastDC)
      throws IOException {
    int magnitude = decoder.decodeNext(in);
    byte[] additional = new byte[magnitude];
    in.read(additional);
    int diff = extend(NumUtils.intFromBits(additional), magnitude);
    return diff + lastDC;
  }

  /**
   * Decode 63 AC components and place them in the output array given,
   * starting at index 1, to account for a DC component being present.
   * It is assumed that the output array has been initialized to 0 prior
   * to the operation.
   * @param decoder the huffman decoder to use for the components
   * @param in the input stream where the data is to be found
   * @param out the byte array where the data is to be stored
   */
  private void decodeACs(HuffmanDecoder decoder, BitInputStream in,
                          int[] out) throws IOException {
    int i = 1;
    final int total = 64;
    while(i < total) {
      byte symbol1 = decoder.decodeNext(in);
      int magnitude = (symbol1 & 0x0F);
      int zeroRun = (symbol1 & 0xF0) >> 4;
      if(magnitude != 0) {
        i += zeroRun;
        byte[] extra = new byte[magnitude];
        in.read(extra);
        out[i] = extend(NumUtils.intFromBits(extra), magnitude);
        i++;
      } else {
        if(zeroRun == 0x0F) {
          i += 16;
        } else if (zeroRun == 0) {
          i = total;
        }
      }
    }
  }

  /**
   * Decompress the data given and place the corresponding bytes in the output
   * list provided.
   * @param scan the scan we're working with.
   * @param input the data to decompress.
   * @param output the list into which the data will be placed.
   */
  private void decompress(Scan scan, byte[] input, TByteList output)
      throws IOException {
    if(input[0] == (byte) 0xFF && JPEGConstants.isRSTMarker(input[1])) {
      output.add(input, 0, 2);
      /* TODO Waste of good time. There must be a better way */
      input = ArrayUtils.subarray(input, 2, input.length);
    }
    input = unescape(input);
    /* TODO Figure out a better length to put in here */
    TIntList accumulated = new TIntArrayList(input.length);
    BitInputStream in = new BitInputStream(input);
    int[] lastDCs = new int[scan.getScanComponents()];
    int mcus = scan.getNumberOfMCUsPerIteration();
    /* We'll iterate over each data unit */
    for(int mcu = 0; mcu < mcus; mcu++) {
      for(byte cmp = 0; cmp < scan.getScanComponents(); cmp++) {
        for(byte hor = 0; hor < scan.getSubsampling()[cmp][0]; hor++) {
          for(byte vert = 0; vert < scan.getSubsampling()[cmp][1]; vert++) {
            /* The way this table stuff works is that the first four bits of
             * the table data contain the table id for the dc values. We
             * then fetch the huffman table with that id and with class 0.
             * The next four bits contain the table id for the ac values, so
             * we fetch the table with that id and with class 1.
             */
            int tableId = scan.getTableId(cmp + 1);
            int dcTable = (tableId & 0xF0) >> 4;
            int acTable = (tableId & 0x0F) | 0x10;
            int dc = decodeDC(scan.getDecoder(dcTable), in, lastDCs[cmp]);
            lastDCs[cmp] = dc;
            int[] coeffs = new int[64];
            coeffs[0] = dc;
            decodeACs(scan.getDecoder(acTable), in, coeffs);
            accumulated.addAll(ZigZag.zigZagToSequential(coeffs));
          }
        }
      }
    }
    byte[] asBytes = NumUtils.byteArrayFromIntArray(accumulated.toArray());
    output.addAll(escape(asBytes));
    in.close();
  }

  /**
   * Decompress the scan given.
   * @param scan the scan
   * @return the decompressed scan
   */
  @Override
  protected Scan process(Scan scan) {
    /* Every coefficient is going to become an int in here */
    TByteList output = new TByteArrayList(scan.getCoefficientCount() * 4);
    try {
      for(byte[] piece : scan) {
        decompress(scan, piece, output);
      }
    } catch(IOException ioe) {
      /* BitInputStreams are not declared as throwing on read, so we shouldn't
       * end here. In the event that we do, in fact, wind up getting an
       * exception (from some future modification), it would be from invalid
       * user input, so we can throw it back out: it's not really our fault.
       */
      throw new IllegalArgumentException("Scan data caused exception", ioe);
    }
    scan.setData(output.toArray());
    return scan;
  }
}
