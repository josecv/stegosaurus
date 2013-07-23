package com.stegosaurus.stegutils;

import static org.junit.Assert.*;

import org.junit.Test;

public class NumUtilsTest {

  /**
   * Test the intFromBitsBE method.
   */
  @Test
  public void testIntFromBitsBE() {
    byte[] arr = {1, 0, 0, 1};
    int expected = 0b1001;
    int result = NumUtils.intFromBitsBE(arr, 4);
    assertEquals("Expected " + expected + " got " + result,
      expected, result);
  }

  /**
   * Test the intFromBitsBE method when the size requested is larger than
   * the array itself, ie some padding is required.
   */
  @Test
  public void testIntFromBitsPaddedBE() {
    byte[] arr = {1, 1, 0, 1, 1};
    /* Let's request 4 extra bits */
    int expected = 0b110110000;
    int result = NumUtils.intFromBitsBE(arr, 9);
    assertEquals("Expected " + expected + " got " + result,
      expected, result);
  }
}
