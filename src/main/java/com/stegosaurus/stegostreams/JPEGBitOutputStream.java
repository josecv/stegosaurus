package com.stegosaurus.stegostreams;

import gnu.trove.list.TByteList;

/**
 * Converts bit-by-bit input to a byte array, writing 0x00 bytes after every
 * 0xFF byte is written.
 */
public class JPEGBitOutputStream extends BitOutputStream {
  /**
   * Write a new bit to the stream.
   * @param b the bit to write.
   */
  @Override
  public void write(int b) {
    super.write(b);
    TByteList data = getData();
    int count = getCount();
    if ((count / 8 == data.size()) && 
        (data.size() > 0) &&
        (data.get(data.size() - 1) == (byte) 0xFF)) {
      this.writeInt(0, 8);
    }
  }
}
