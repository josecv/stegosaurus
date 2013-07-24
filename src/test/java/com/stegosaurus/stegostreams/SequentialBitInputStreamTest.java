package com.stegosaurus.stegostreams;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import com.stegosaurus.stegostreams.SequentialBitInputStream;

/**
 * Tests the sequential bit inputstream class.
 */
public class SequentialBitInputStreamTest {

  /**
   * Test the read method.
   */
  @Test
  public void testRead() {
    byte[] arg = { 0b01011101 };
    SequentialBitInputStream st = new SequentialBitInputStream(arg);
    byte[] expected = { 0, 1, 0, 1, 1, 1, 0, 1 };
    byte[] retval = new byte[8];
    try {
      for (int i = 0; i < retval.length; i++) {
        retval[i] = (byte) st.read();
      }
      st.close();
    } catch (IOException e) {
      fail("Unexpected exception: " + e);
    }
    assertTrue("Wrong return value from read",
        Arrays.equals(retval, expected));
  }

  /**
   * Test the skipToEndOfByte method.
   */
  @Test
  public void testSkipToEndOfByte() {
    byte[] arg = { 0b01001111, 0b00001101 };
    BitInputStream st = new SequentialBitInputStream(arg);
    for(int i = 0; i < 4; i++) {
      st.read();
    }
    st.skipToEndOfByte();
    try {
      st.close();
      for(int i = 0; i < 4; i++) {
        assertEquals("Failed to skip all the way to next byte", st.read(), 0);
      }
    } catch(IOException e) {
      fail("Unexpected exception: " + e);
    }
  }
}
