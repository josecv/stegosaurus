package com.stegosaurus.stegostreams;

/**
 * Operates like a bit input stream, but skips any 0x00 bytes immediately
 * after 0xFF bytes.
 * Note that the available method is not guaranteed to be accurate, since
 * information any skipped 0x00 bytes will only become available as the stream
 * is read.
 */
public class JPEGBitInputStream extends BitInputStream {
  /**
   * Construct a new JPEG Bit input stream that will return the bits from
   * the byte array given.
   * @param buf the byte array to work with.
   */
  public JPEGBitInputStream(byte[] buf) {
    super(buf);
  }

  /**
   * Read the next bit.
   * @return the next bit in this stream.
   */
  @Override
  public int read() {
    int index = getIndex();
    byte[] data = getData();
    int dataIndex = index / 8;
    if(index % 8 == 0 &&
       index > 0 &&
       data[dataIndex - 1] == (byte) 0xFF &&
       data[dataIndex] == 0) {
      skip(8);
    }
    return super.read();
  }
}
