package com.stegosaurus.stegostreams;

import java.io.InputStream;

/**
 * Produces, bit by bit, the byte array given. Operates in Big Endian, which
 * is to say the most significant bit of the 0th byte is returned, followed
 * by the second to most significant bit of the 0th byte, and so on.
 */
public class BitInputStream extends InputStream {

  /**
   * The byte array.
   */
  private byte[] in;
  /**
   * The next bit to read.
   */
  private int index;

  /**
   * Initialize the input stream with the input given.
   *
   * @param in the array of bytes whose bits we will return one by one.
   */
  public BitInputStream(byte[] in) {
    this.in = in.clone();
    index = 0;
  }

  /**
   * Get the next bit (0 or 1) from the byte array.
   *
   * @return the next bit.
   */
  @Override
  public int read() {
    int retval = (in[index/8] >> (7 - (index % 8))) & 1;
    index++;
    return retval;
  }

  /**
   * Skip any remaining bits in the current byte until a new one is reached.
   */
  public void skipToEndOfByte() {
    while(available() % 8 != 0) {
      read();
    }
  }

  /**
   * Get the number of available bits that can be read without blocking, or 0
   * if there is nothing left to read.
   *
   * @return the number of bits that can be read.
   */
  @Override
  public int available() {
    return (in.length * 8) - index;
  }
}
