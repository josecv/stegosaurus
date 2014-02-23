package com.stegosaurus.stegostreams;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.primitives.Bytes;

/**
 * Produces, bit by bit, the byte array given. Operates in Big Endian, which
 * is to say the most significant bit of the 0th byte is returned, followed
 * by the second to most significant bit of the 0th byte, and so on.
 * TODO Some bounds checking!
 */
public class BitInputStream extends InputStream {

  /**
   * The byte array.
   */
  private byte[] data;

  /**
   * The next bit to read.
   */
  private int index;

  /**
   * Initialize the input stream with the input given.
   *
   * @param in the array of bytes whose bits we will return one by one.
   * @param additional further byte arrays to return.
   */
  public BitInputStream(byte[] in, byte... additional) {
    reset(in, additional);
  }

  /**
   * Reset the input stream, to handle a different byte array.
   * @param in the array of bytes whose bits we'll return
   * @param additional further byte arrays to return.
   */
  public final void reset(byte[] in, byte... additional) {
    this.data = Bytes.concat(in, additional);
    reset();
  }

  /**
   * Reset the input stream to handle the same byte array it is currently
   * handling.
   */
  public final void reset() {
    index = 0;
  }

  /**
   * Get the next bit (0 or 1) from the byte array.
   * @return the next bit, or -1 if there are no more bits to read.
   */
  @Override
  public int read() {
    if(index / Byte.SIZE >= data.length) {
      return -1;
    }
    int retval = (data[index/Byte.SIZE] >> (7 - (index % Byte.SIZE))) & 1;
    index++;
    return retval;
  }

  /**
   * Try to read n bits from the byte array and place them in b, where n is
   * the length of b.
   * @param b the output array.
   * @return the number of bits actually read.
   */
  @Override
  public int read(byte[] b) {
    try {
      return super.read(b);
    } catch(IOException e) {
      /* XXX */
      throw new RuntimeException(e);
    }
  }

  /**
   * Skip any remaining bits in the current byte until a new one is reached.
   */
  public void skipToEndOfByte() {
    skip(Byte.SIZE - (index % Byte.SIZE));
  }

  /**
   * Skip n bytes from this stream, if possible.
   * @param n the number of bytes to skip over.
   * @return the actual number of bytes skipped.
   */
  @Override
  public long skip(long n) {
    if(n < 0) {
      return 0;
    }
    index += n;
    return n;
  }

  /**
   * Get the number of available bits that can be read without blocking, or 0
   * if there is nothing left to read.
   *
   * @return the number of bits that can be read.
   */
  @Override
  public int available() {
    return (data.length * Byte.SIZE) - index;
  }

  /**
   * Get the index of the next bit to read.
   * @return the index
   */
  protected int getIndex() {
    return index;
  }

  /**
   * Close the input stream; has no effect.
   */
  public void close() { }

  /**
   * Get the byte array that we're returning bits from.
   * Note that it is not copied, but returned as-is.
   * @return the array
   */
  protected byte[] getData() {
    return data;
  }
}
