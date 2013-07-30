package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * Tests the JPEGBitInputStream class.
 */
public class JPEGBitInputStreamTest {
  /**
   * Test the read method.
   */
  @Test
  public void testRead() {
    byte[] input = {0x1A, 0x2B, (byte) 0xFF, 0, (byte) 0x8F};
    byte[] expected = { 0, 0, 0, 1,  1, 0, 1, 0,
                        0, 0, 1, 0,  1, 0, 1, 1,
                        1, 1, 1, 1,  1, 1, 1, 1,
                        1, 0, 0, 0,  1, 1, 1, 1 };
    InputStream stream = new JPEGBitInputStream(input);
    try {
      byte[] result = new byte[expected.length];
      for(int i = 0; i < result.length; i++) {
        result[i] = (byte) stream.read();
      }
      stream.close();
      assertArrayEquals("Read not working as expected", expected, result);
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
