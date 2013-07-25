package com.stegosaurus.jpeg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.NumUtils;

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
   * Decode 63 AC components and place them in the output list given.
   * @param decoder the huffman decoder to use for the components
   * @param in the input stream where the data is to be found
   * @param out the list to place the decoded components into
   */
  private void decodeACs(HuffmanDecoder decoder, BitInputStream in,
    List<Integer> out) throws IOException {
    int i = 0;
    while(i < 63) {
      byte symbol1 = decoder.decodeNext(in);
      byte magnitude = (byte) (symbol1 & (byte) 0xF0);
      byte zeroRun = (byte) (symbol1 * 0x0F);
      if(magnitude == 0) {
        if(zeroRun == 0x0F) {
          /* We've encountered a run of 16 zeroes. Emplace them, and increment
           * our counter */
          for(int j = 0; j < 16; j++, i++) {
            out.add(0);
          }
        } else if (zeroRun == 0) {
          /* We've encountered the end of block. Push i to 63 and write any
           * remaining 0s */
          for(; i < 63; i++) {
            out.add(0);
          }
        }
      } else {
        /* First emplace the preceding run of zeroes */
        for(int j = 0; j < zeroRun; j++, i++) {
          out.add(0);
        }
        byte[] extra = new byte[magnitude];
        out.add(extend(NumUtils.intFromBits(extra), magnitude));
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
  private void decompress(Scan scan, byte[] input, List<Integer> output)
      throws IOException {
    /* TODO Waste of good time. There must be a better way */
    if(input[0] == (byte) 0xFF && JPEGConstants.isRSTMarker(input[1])) {
      input = ArrayUtils.subarray(input, 2, input.length);
    }
    int[] lastDCs = new int[scan.getScanComponents()];
    BitInputStream in = new BitInputStream(input);
    while(in.available() > 0) {
      for(byte cmp = 0; cmp < scan.getScanComponents(); cmp++) {
        for(byte hor = 0; hor < scan.getSubsampling()[cmp][0]; hor++) {
          for(byte vert = 0; vert < scan.getSubsampling()[cmp][1]; vert++) {
            int dcTable = (scan.getTableId(cmp) & 0xF0) >> 4;
            int acTable = (scan.getTableId(cmp) & 0x0F) | 0x10;
            int dc = decodeDC(scan.getDecoder(dcTable), in, lastDCs[cmp]);
            lastDCs[cmp] = dc;
            output.add(dc);
            decodeACs(scan.getDecoder(acTable), in, output);
          }
        }
      }
    }
    in.close();
  }

  /**
   * Decompress the scan given.
   * @param scan the scan
   * @return the decompressed scan
   */
  @Override
  protected Scan process(Scan scan) {
    /* JPEG does a ton of different stuff to its data, so it's hard to
     * estimate how many bytes we're going to get in the end. At the same time,
     * because we're using an ArrayList, it's desirable to at least
     * ballpark it. For now, we're assuming that the data is three times as
     * large as its compressed counterpart. An unlikely assertion, but a decent
     * starting point.
     */
    List<Integer> data = new ArrayList<Integer>(scan.getData().length * 3);
    try {
      for(byte[] piece : scan) {
        decompress(scan, piece, data);
      }
    } catch(IOException ioe) {
      /* XXX */
      throw new RuntimeException(ioe);
    }
    /* XXX This statement kinda sucks. */
    scan.setData(NumUtils.
      byteArrayFromIntArray(ArrayUtils.
        toPrimitive(data.toArray(new Integer[data.size()]))));
    return scan;
  }
}
