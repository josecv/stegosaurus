package com.stegosaurus.stegostreams;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

import java.io.OutputStream;

/**
 * Converts bit-by-bit input to a byte array.
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
  private int i;

  public BitOutputStream() {
    super();
    data = new TByteArrayList();
  }

  /**
   * Write a new bit to the stream.
   * 
   * @param b the bit to write
   */
  @Override
  public void write(int b) {
    if(b != 0 && b != 1) {
      throw new IllegalArgumentException("Argument " + b + " not a bit");
    }
    if (i / 8 == data.size()) {
      data.add((byte) 0);
    }
    byte current = data.get(i / 8);
    current |= (byte) (b << 7 - (i % 8));
    data.set(i / 8, current);
    i++;
  }

  /**
   * Write an int, with the size given, to the stream, in big endian order.
   * @param val the int to write.
   * @param size how many bits should be written.
   */
  public void writeInt(int val, int size) {
    for (int j = size - 1; j >= 0; j--) {
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
    while((i % 8) != 0) {
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
}
