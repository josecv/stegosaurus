/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    if (count / Byte.SIZE == data.size()) {
      data.add((byte) 0);
    }
    byte current = data.get(count / Byte.SIZE);
    current |= (byte) (b << 7 - (count % Byte.SIZE));
    data.set(count / Byte.SIZE, current);
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
    while((count % Byte.SIZE) != 0) {
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
