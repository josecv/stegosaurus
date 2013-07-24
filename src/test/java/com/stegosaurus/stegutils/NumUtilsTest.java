package com.stegosaurus.stegutils;

import static org.junit.Assert.*;

import java.nio.ByteOrder;

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

  /**
   * Test the intFromBytes method with the default big endian ordering.
   */
  @Test
  public void testIntFromBytes() {
    byte[] input = {(byte) 0xA3, (byte) 0x98};
    int expected = 0xA398;
    int result = NumUtils.intFromBytes(input);
    assertEquals("intFromBytes big endian failure", expected, result);
  }

  /**
   * Test the intFromBytes method when a full four byte long array is given.
   */
  @Test
  public void testIntFromBytesFourBytes() {
    byte[] input = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
    int expected = 0xDEADBEEF;
    int result = NumUtils.intFromBytes(input);
    assertEquals("intFromBytes full array big endian failure", expected,
      result);
    expected = 0xEFBEADDE;
    result = NumUtils.intFromBytes(input, ByteOrder.LITTLE_ENDIAN);
    assertEquals("intFromBytes full array little endian failure", expected,
      result);
  }

  /**
   * Test the intFromBytes method with little endian ordering.
   */
  @Test
  public void testIntFromBytesLE() {
    byte[] input = {(byte) 0xA3, (byte) 0x98};
    int expected = 0x98A3;
    int result = NumUtils.intFromBytes(input, ByteOrder.LITTLE_ENDIAN);
    assertEquals("intFromBytes Little Endian failure", expected, result);
  }

  /**
   * Test the byteArrayFromIntArray method with the default big endian
   * ordering
   */
  @Test
  public void testByteArrayFromIntArray() {
    int[] arr = {0xDEADBEEF, 0xCAFEBABE};
    byte[] expected = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF,
                        (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
    assertArrayEquals("Faliure from byteArrayFromIntArray, big endian",
      expected, NumUtils.byteArrayFromIntArray(arr, ByteOrder.BIG_ENDIAN));
    assertArrayEquals("byteArrayFromIntArray not defaulting to big endian",
      expected, NumUtils.byteArrayFromIntArray(arr));
  }

  /**
   * Test the byteArrayFromIntArray method with little endian ordering
   */
  @Test
  public void testByteArrayFromIntArrayLE() {
    int[] arr = {0xDEADBEEF, 0xCAFEBABE};
    byte[] expected = {(byte) 0xEF, (byte) 0xBE, (byte) 0xAD, (byte) 0xDE,
                       (byte) 0xBE, (byte) 0xBA, (byte) 0xFE, (byte) 0xCA};
    assertArrayEquals("Failure from byteArrayFromIntArray, little endian",
      expected, NumUtils.byteArrayFromIntArray(arr, ByteOrder.LITTLE_ENDIAN));
  }
}
