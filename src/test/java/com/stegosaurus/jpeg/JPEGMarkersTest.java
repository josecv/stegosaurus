package com.stegosaurus.jpeg;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test the JPEGMarkers class.
 */
public class JPEGMarkersTest {
  /**
   * Test the isRSTMarker method.
   */
  @Test
  public void testIsRSTMarker() {
    byte[] input = { (byte) 0xD0, (byte) 0xDA, 0x39, (byte) 0xD4 };
    boolean[] expected = { true, false, false, true };
    for(int i = 0; i < input.length; i++) {
      byte b = input[i];
      assertEquals("isRSTMarker failed on input " + b,
        expected[i], JPEGMarkers.isRSTMarker(b));
    }
  }
}
