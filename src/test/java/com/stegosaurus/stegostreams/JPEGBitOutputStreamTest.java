package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;

import org.junit.Test;

/**
 * Tests the JPEGBitOutputStream class.
 */
public class JPEGBitOutputStreamTest {
  /**
   * Test the write method.
   */
  @Test
  public void testWrite() {
    BitOutputStream os = new JPEGBitOutputStream();
    os.write(0);
    os.write(1);
    os.write(0);
    os.write(1);
    os.write(0);
    os.write(1);
    os.write(0);
    os.write(1);
    os.write(1);
    os.write(1);
    os.write(1);
    os.write(1);
    os.write(1);
    os.write(1);
    os.write(1);
    os.write(1);
    os.writeInt(0xFF, 8);
    byte[] expected = { 0b01010101, (byte) 0xFF, 0, (byte) 0xFF, 0 };
    byte[] result = os.data();
    assertArrayEquals("Write failure", expected, result);
    try {
      os.close();
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
