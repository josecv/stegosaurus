package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the sequential bit inputstream class.
 */
public class BitInputStreamTest {

  /**
   * Test the read method.
   */
  @Test
  public void testRead() {
    byte[] arg = { 0x5D, 0x2A, 0x3F };
    BitInputStream st = new BitInputStream(arg);
    byte[] expected = { 0, 1, 0, 1,  1, 1, 0, 1,
                        0, 0, 1, 0,  1, 0, 1, 0,
                        0, 0, 1, 1,  1, 1, 1, 1
                      };
    byte[] retval = new byte[expected.length];
    for (int i = 0; i < retval.length; i++) {
      retval[i] = (byte) st.read();
    }
    st.close();
    assertArrayEquals("Wrong return value from read", expected, retval);
  }

  /**
   * Test the skipToEndOfByte method.
   */
  @Test
  public void testSkipToEndOfByte() {
    byte[] arg = { 0b01001111, 0b00001101 };
    BitInputStream st = new BitInputStream(arg);
    for(int i = 0; i < 4; i++) {
      st.read();
    }
    st.skipToEndOfByte();
    st.close();
    for(int i = 0; i < 4; i++) {
      assertEquals("Failed to skip all the way to next byte", st.read(), 0);
    }
  }

  /**
   * Test the skip method.
   */
  @Test
  public void testSkip() {
    byte[] arg = { 0x1A, 0x4B, 0x18 };
    byte[] expected = { 0, 0, 0, 1,  1, 0, 1, 0,
                        0, 1, 0, 0,
                                     1, 0, 0, 0};
    byte[] result = new byte[expected.length];
    BitInputStream stream = new BitInputStream(arg);
    int i;
    for(i = 0; i < 12; i ++) {
      result[i] = (byte) stream.read();
    }
    assertEquals("Incorrect number of bytes skipped", 8, stream.skip(8));
    for(; i < expected.length; i++) {
      result[i] = (byte) stream.read();
    }
    assertArrayEquals("Wrong result after skipping bytes", expected, result);
    stream.close();
  }
}
