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
    if (i / 8 == data.size()) {
      data.add((byte) 0);
    }
    byte current = data.get(i / 8);
    current |= (byte) (b << 7 - (i % 8));
    data.set(i / 8, current);
    i++;
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
