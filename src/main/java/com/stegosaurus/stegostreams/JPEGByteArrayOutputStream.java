package com.stegosaurus.stegostreams;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * A byte array output stream that automatically writes a 0x00 byte whenever
 * a 0xFF byte is written.
 */
public class JPEGByteArrayOutputStream extends ByteArrayOutputStream {
  /**
   * Write the given byte to this byte array output stream. If it is a 0xFF
   * byte, a 0x00 byte is written automatically.
   * @param b the byte to write.
   */
  @Override
  public void write(int b) {
    super.write(b);
    if(b == (byte) 0xFF) {
      super.write(0);
    }
  }

  /**
   * Write the bytes to this byte array ouput stream. Any 0xFF bytes will be
   * written as well.
   * @param bytes the bytes to write.
   * @param off the offset to start writing from.
   * @param len the number of bytes to write.
   */
  @Override
  public void write(byte[] bytes, int off, int len) {
    /* This implementation takes a whole lot from the original implementation
     * inside the ByteArrayOutputStream class.
     */
    if((off < 0) || (off > bytes.length) || (len < 0) ||
       ((off + len) > bytes.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }
    /* Unfortunately, we'll have to iterate over the whole thing to make
     * sure we get all 0xFF bytes. However, we'll make sure that the array
     * is of a sufficient size before we do so. This way, we only have to
     * grow it once, at the most.
     */
    int newcount = count + len;
    if(newcount > buf.length) {
      buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
    }
    for(byte b : bytes) {
      write(b);
    }
  }
}
