package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class BitOutputStreamTest {

  /**
   * Test the write methods by writing some bits.
   */
  @Test
  public void testWrite() {
    BitOutputStream s = new BitOutputStream();
    s.write(0);
    s.write(0);
    s.write(1);
    s.write(1);
    s.write(0);
    s.write(0);
    s.write(0);
    s.write(1);
    s.write(0);
    s.write(1);
    s.write(1);
    byte[] data = { 0b00110001, 0b01100000 };
    assertArrayEquals("Write failure", data, s.data());
    s.close();
  }

  /**
   * Test the writeToEndOfByte method.
   */
  @Test
  public void testWriteToEndOfByte() {
    BitOutputStream s = new BitOutputStream();
    s.write(0);
    s.write(1);
    s.write(0);
    s.write(1);
    s.write(0);
    s.writeToEndOfByte(1);
    byte[] data = { 0b01010111 };
    assertArrayEquals("Write to end of byte failure", data, s.data());
    s.close();
  }

  /**
   * Test the writeInt method.
   */
  @Test
  public void testWriteInt() {

  }
}
