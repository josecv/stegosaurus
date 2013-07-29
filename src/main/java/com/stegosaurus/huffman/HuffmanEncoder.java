package com.stegosaurus.huffman;

import java.io.IOException;
import java.util.Map;

import com.stegosaurus.stegostreams.BitOutputStream;

/**
 * Performs encoding of arbitrary bytes into bit streams according to a specific
 * code.
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
   * Encode the given bytes, placing them in the output stream given as
   * we go.
   * @param input the bytes to encode
   * @param output the output stream where they should be placed
   */
  public void encode(byte[] input, BitOutputStream os) {
    for (byte b : input) {
      HuffmanCode hc = code.get(b);
      os.writeInt(hc.code, hc.length);
    }
  }

  /**
   * Encode a single byte, and place the resulting code into the output
   * stream given.
   * @param input the byte to encode
   * @param output the output stream to place it in.
   */
  public void encode(byte input, BitOutputStream os) {
    byte[] in = { input };
    encode(in, os);
  }

  /**
   * Encode a single byte.
   * @param input the byte to encode
   * @return the encoded byte
   * @throws IOException
   */
  public byte[] encode(byte input) throws IOException {
    byte[] in = { input };
    return encode(in);
  }

  /**
   * Encode the given bytes.
   * 
   * @param input the bytes to encode
   * @return the encoded bytes.
   * @throws IOException on read error.
   * TODO This shouldn't really throw...
   */
  public byte[] encode(byte[] input) throws IOException {
    BitOutputStream os = new BitOutputStream();
    encode(input, os);
    byte[] retval = os.data();
    os.close();
    return retval;
  }
}
