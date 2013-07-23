package com.stegosaurus.stegutils;

import static org.junit.Assert.*;

import java.nio.ByteOrder;

import org.junit.Test;

public class NumUtilsTest {

  /**
   * Test the intFromBytesLE method.
   */
  @Test
  public void testIntFromBytesLE() {
    byte[] input = {(byte) 0xA3, (byte) 0x98};
    int expected = 0x98A3;
    int result = NumUtils.intFromBytesLE(input);
    assertEquals("intFromBytes Little Endian failure", expected, result);
  }

  /**
   * Test the intFromBitsBE method.
   */
  @Test
  public void testIntFromBitsBE() {
    byte[] arr = {1, 0, 0, 1};
    int expected = 0b1001;
    int result = NumUtils.intFromBitsBE(arr, 4);
    assertEquals("Failure from intFromBits big endian", expected, result);
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
    assertEquals("Failure from intFromBits big endian, with padded result",
      expected, result);
  }

  @Test
  public void testByteArrayFromIntArray() {
    int[] arr = {0xDEADBEEF, 0xCAFEBABE};
    byte[] bigEndian = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF,
                        (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
    assertArrayEquals("Faliure from byteArrayFromIntArray, big endian",
      bigEndian, NumUtils.byteArrayFromIntArray(arr, ByteOrder.BIG_ENDIAN));
    assertArrayEquals("byteArrayFromIntArray not defaulting to big endian",
      bigEndian, NumUtils.byteArrayFromIntArray(arr));
  }
}
