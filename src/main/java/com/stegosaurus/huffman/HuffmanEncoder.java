package com.stegosaurus.huffman;

import java.io.IOException;
import java.util.Map;

import com.stegosaurus.stegostreams.BitOutputStream;
import com.stegosaurus.stegostreams.SequentialBitOutputStream;
import com.stegosaurus.huffman.HuffmanCode;

/**
 * Performs encoding of arbitrary bytes into bit streams according to a specific
 * code.
 * 
 * @author joe
 * 
 */
public class HuffmanEncoder {
  /**
   * The inner representation of the code we're using.
   */
  protected Map<Byte, HuffmanCode> code;

  /**
   * Initialize an encoder to match the decoder given.
   * 
   * @param decoder
   *            a decoder which can decode what this encoder encodes.
   */
  public HuffmanEncoder(HuffmanDecoder decoder) {
    code = decoder.root.asMap();
  }

  /**
   * Encode the given bytes.
   * 
   * @param input
   *            the bytes to encode
   * @return the encoded bytes.
   * @throws IOException
   *             on read error.
   */
  public byte[] encode(byte[] input) throws IOException {
    BitOutputStream os = new SequentialBitOutputStream();
    for (byte b : input) {
      HuffmanCode hc = code.get(b);
      int c = hc.code;
      int len = hc.length;
      for (int i = len - 1; i >= 0; i--) {
        os.write((c >> i) & 1);
      }
      System.out.println();
    }
    byte[] retval = os.data();
    os.close();
    return retval;
  }
}
