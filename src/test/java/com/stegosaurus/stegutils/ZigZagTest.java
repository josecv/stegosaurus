package com.stegosaurus.stegutils;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

/**
 * Tests the ZigZag class.
 */
public class ZigZagTest {
  /**
   * Test input to use, in regular order. 64 meaningless random numbers.
   */
  private static final int[] INPUT = {
    10, 92, 96, 36, 49, 44, 21, 11,
    47, 56, 67, 26, 13, 54,  8,  9,
    48, 17, 37, 69, 24, 55, 36, 44,
     0, 74, 16, 61, 99, 77, 12, 30,
    58, 74, 97, 45, 92, 39, 19, 83,
    95, 32, 42, 13, 83, 26,  0, 42,
    74, 22, 86, 96, 47, 38, 27,  2,
    88, 12, 61,  1, 62, 63, 97,  1
  };

  /**
   * The input, in zig zag order.
   */
  private static final int[] ZIG_ZAGGED = {
    10, 92, 47, 48, 56, 96, 36, 67,
    17,  0, 58, 74, 37, 26, 49, 44,
    13, 69, 16, 74, 95, 74, 32, 97,
    61, 24, 54, 21, 11,  8, 55, 99,
    45, 42, 22, 88, 12, 86, 13, 92,
    77, 36,  9, 44, 12, 39, 83, 96,
    61,  1, 47, 26, 19, 30, 83,  0,
    38, 62, 63, 27, 42,  2, 97,  1
  };

  /**
   * Test the sequentialToZigZag method.
   */
  @Test
  public void testSequentialToZigZag() {
    int[] result = ZigZag.sequentialToZigZag(INPUT);
    assertArrayEquals("To zig zag order failure.", ZIG_ZAGGED, result);
  }

  /**
   * Test the zigZagToSequential mehtod.
   */
  @Test
  public void testZigZagToSequential() {
    int[] result = ZigZag.zigZagToSequential(ZIG_ZAGGED);
    assertArrayEquals("To sequential order failure.", INPUT, result);
  }
}
