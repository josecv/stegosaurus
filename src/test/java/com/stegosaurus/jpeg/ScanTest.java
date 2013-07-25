package com.stegosaurus.jpeg;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * Tests the Scan class.
 */
public class ScanTest {

  /**
   * Test data to use.
   */
  private static byte[] DATA = {
    (byte) 0xF6, 0x18, (byte) 0xD8, 0x37, (byte) 0xDD, 0x7E,
    (byte) 0x9D, (byte) 0xF3, (byte) 0x9C, (byte) 0xD5, (byte) 0xA0,
    (byte) 0xE0, 0x1E, (byte) 0xFF, 0x00, 0x77, 0x66, 0x2A, 0x23,
    (byte) 0xD2, 0x40, 0x39, 0x20, (byte) 0xE4, 0x76, 0x15,
    (byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xD0, (byte) 0x8D, 0x75, 0x34,
    0x0F, 0x0E, (byte) 0xDE, (byte) 0xEA, 0x7B, 0x72, (byte) 0xF0, 0x46, 0x59,
    0x37, 0x29, 0x23, 0x71, (byte) 0xE0, 0x67, 0x1C, (byte) 0xFF, 0x00,
    0x67, 0x1D, (byte) 0xA8, (byte) 0xBE, (byte) 0x83, (byte) 0xB1,
    (byte) 0xF3, 0x1E, (byte) 0xFF, (byte) 0xD1, 0x5D, 0x5F, 0x53,
    (byte) 0x9A, 0x56, (byte) 0xBA, (byte) 0xBF, (byte) 0xBC, 0x76,
    0x76, (byte) 0xC9, 0x06, 0x63, (byte) 0xB4, 0x75, (byte) 0xE8,
    (byte) 0xA3, (byte) 0x81, (byte) 0xCF
  };

  /**
   * The same data, but split into different arrays by RST marker.
   */
  private static byte[][] DATA_SPLIT = {
    {
      (byte) 0xF6, 0x18, (byte) 0xD8, 0x37, (byte) 0xDD, 0x7E,
      (byte) 0x9D, (byte) 0xF3, (byte) 0x9C, (byte) 0xD5, (byte) 0xA0,
      (byte) 0xE0, 0x1E, (byte) 0xFF, 0x00, 0x77, 0x66, 0x2A, 0x23,
      (byte) 0xD2, 0x40, 0x39, 0x20, (byte) 0xE4, 0x76, 0x15,
      (byte) 0xFF, 0x00
    },
    {
      (byte) 0xFF, (byte) 0xD0, (byte) 0x8D, 0x75, 0x34,
      0x0F, 0x0E, (byte) 0xDE, (byte) 0xEA, 0x7B, 0x72, (byte) 0xF0, 0x46,
      0x59, 0x37, 0x29, 0x23, 0x71, (byte) 0xE0, 0x67, 0x1C, (byte) 0xFF,
      0x00, 0x67, 0x1D, (byte) 0xA8, (byte) 0xBE, (byte) 0x83, (byte) 0xB1,
      (byte) 0xF3, 0x1E
    },
    {
      (byte) 0xFF, (byte) 0xD1, 0x5D, 0x5F, 0x53,
      (byte) 0x9A, 0x56, (byte) 0xBA, (byte) 0xBF, (byte) 0xBC, 0x76,
      0x76, (byte) 0xC9, 0x06, 0x63, (byte) 0xB4, 0x75, (byte) 0xE8,
      (byte) 0xA3, (byte) 0x81, (byte) 0xCF
    },
  };

  /**
   * Tests iteration over the scan.
   */
  @Test
  public void testIteration() {
    int i = 0;
    Scan scan = new Scan();
    scan.setData(DATA);
    for(byte[] data : scan) {
      assertArrayEquals("Iteration data is not as expected", DATA_SPLIT[i],
        data);
      i++;
    }
  }

  /**
   * Tests iteration over the scan when there are no restart markers within
   * it.
   */
  @Test
  public void testIterationNoRSTs() {
    Scan scan = new Scan();
    /* We reuse DATA_SPLIT[0] just so we don't have to come up with any more
     * test data.
     */
    scan.setData(DATA_SPLIT[0]);
    for(byte[] data : scan) {
      assertArrayEquals("Iteration data is not as expected", DATA_SPLIT[0],
        data);
    }
  }
}
