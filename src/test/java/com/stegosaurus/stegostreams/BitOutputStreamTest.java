package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class BitOutputStreamTest {

  @Test
  /**
   * Test the write methods by writing some ints.
   */
  public void testWriteInt() {
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
    assertTrue("Wrong return value for data", Arrays.equals(data, s.data()));
    try {
      s.close();
    } catch (IOException io) {
      io.printStackTrace();
      fail("Unexpected exception " + io.getMessage());
    }
  }

}
