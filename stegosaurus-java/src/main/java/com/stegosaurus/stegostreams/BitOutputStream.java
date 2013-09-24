package com.stegosaurus.stegostreams;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Converts bit-by-bit input to a byte array; close() is a no-op.
 * Operates in Big Endian (ie sequentially)
 */
public class BitOutputStream extends OutputStream {
  /**
   * The byte array.
   */
  private TByteList data;

  /**
   * How many bits have been written.
   */
  private int count;

  /**
   * Construct a new BitOutputStream.
   */
  public BitOutputStream() {
    super();
    data = new TByteArrayList();
    count = 0;
  }

  /**
   * Write a new bit to the stream.
   * @param b the bit to write
   */
  @Override
  public void write(int b) {
    if(b != 0 && b != 1) {
      throw new IllegalArgumentException("Argument " + b + " not a bit");
    }
    if (count / 8 == data.size()) {
      data.add((byte) 0);
    }
    byte current = data.get(count / 8);
    current |= (byte) (b << 7 - (count % 8));
    data.set(count / 8, current);
    count++;
  }

  /**
   * Write a bit array to this output stream.
   * @param input the bit array.
   */
  @Override
  public void write(byte[] input) {
    try {
      super.write(input);
    } catch(IOException ioe) {
      throw new IllegalStateException("Received unexpected IOException", ioe);
    }
  }

  /**
   * Write an int, with the size given, to the stream, in big endian order.
   * @param val the int to write.
   * @param size how many bits should be written, counting from the lsb.
   */
  public void writeInt(int val, int size) {
    for(int j = size - 1; j >= 0; j--) {
      write((val >> j) & 1);
    }
  }

  /**
   * Write the bit given over and over until a byte boundary is reached.
   * That is, until the total number of bits that have been written to this
   * stream is a product of 8.
   * @param b the int to write
   */
  public void writeToEndOfByte(int b) {
    while((count % 8) != 0) {
      write(b);
    }
  }

  /**
   * Return the data as a Byte array.
   * 
   * @return the collected data.
   */
  public byte[] data() {
    return data.toArray();
  }

  /**
   * Reset the output stream, allowing the user to write to it again.
   */
  public void reset() {
    count = 0;
    data.clear();
  }

  /**
   * Close the bit output stream; has no effect.
   */
  @Override
  public void close() { }

  /**
   * Get the number of bits that have been written.
   * @return the count.
   */
  protected int getCount() {
    return count;
  }

  /**
   * Get the list containing the written data. The list itself is returned,
   * not a copy.
   * @return the list with the data.
   */
  protected TByteList getData() {
    return data;
  }
}
