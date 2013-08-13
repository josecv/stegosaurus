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

  /**
   * Write the nth restart marker in this image.
   * @param n the number corresponding to the restart marker to write.
   */
  public void writeRestart(int n) {
    /* We have to manually write this stuff via the parent write(), to prevent
     * a 0x00 from getting in.
     */
    for(int i = 0; i < 8; i++) {
      super.write(1);
    }
    super.writeInt(0xD0 | (n % 8), 8);
  }
}
