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
  private static int extend(int additional, byte magnitude) {
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
    byte magnitude = decoder.decodeNext(in);
    byte[] additional = new byte[magnitude];
    in.read(additional);
    int diff = extend(NumUtils.intFromBits(additional), magnitude);
    return diff + lastDC;
  }

  /**
   * Decode 63 AC components and place them in the output array given,
   * starting at index 1, to account for a DC component being present.
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
      byte magnitude = (byte) (symbol1 & (byte) 0xF0);
      byte zeroRun = (byte) (symbol1 * 0x0F);
      if(magnitude == 0) {
        if(zeroRun == 0x0F) {
          /* We've encountered a run of 16 zeroes. Emplace them, and increment
           * our counter */
          for(int j = 0; j < 16; j++, i++) {
            out[i] = 0;
          }
        } else if (zeroRun == 0) {
          /* We've encountered the end of block. Push i to 63 and write any
           * remaining 0s */
          for(; i < total; i++) {
            out[i] = 0;
          }
        }
      } else {
        /* First emplace the preceding run of zeroes */
        for(int j = 0; j < zeroRun; j++, i++) {
          out[i] = 0;
        }
        byte[] extra = new byte[magnitude];
        out[i] = extend(NumUtils.intFromBits(extra), magnitude);
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
    /* TODO Figure out a better length to put in here */
    TIntList accumulated = new TIntArrayList(input.length);
    BitInputStream in = new BitInputStream(input);
    int[] lastDCs = new int[scan.getScanComponents()];
    int mcus = scan.getNumberOfMCUsPerIteration();
    for(int mcu = 0; mcu < mcus; mcu++) {
      for(byte cmp = 0; cmp < scan.getScanComponents(); cmp++) {
        for(byte hor = 0; hor < scan.getSubsampling()[cmp][0]; hor++) {
          for(byte vert = 0; vert < scan.getSubsampling()[cmp][1]; vert++) {
            int dcTable = (scan.getTableId(cmp) & 0xF0) >> 4;
            int acTable = (scan.getTableId(cmp) & 0x0F) | 0x10;
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
    TByteList data = new TByteArrayList(scan.getCoefficientCount() * 4);
    try {
      for(byte[] piece : scan) {
        decompress(scan, piece, data);
      }
    } catch(IOException ioe) {
      /* BitInputStreams are not declared as throwing on read, so we shouldn't
       * end here. In the event that we do, in fact, wind up getting an
       * exception (from some future modification), it would be from invalid
       * user input, so we can throw it back out: it's not really our fault.
       */
      throw new IllegalArgumentException("Scan data caused exception", ioe);
    }
    scan.setData(data.toArray());
    return scan;
  }
}
