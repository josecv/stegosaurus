package com.stegosaurus.stegutils;

import static org.junit.Assert.*;

import java.nio.ByteOrder;

import org.junit.Test;

public class NumUtilsTest {

  /**
   * Test the intFromBits method, in its default big endian ordering.
   */
  @Test
  public void testIntFromBits() {
    byte[] arr = {0, 1, 0, 1};
    int expected = 0b0101;
    int result = NumUtils.intFromBits(arr, ByteOrder.BIG_ENDIAN);
    assertEquals("Failure from intFromBits big endian", expected, result);
    result = NumUtils.intFromBits(arr);
    assertEquals("intFromBits not defaulting to big endian", expected, result);
    expected = 0b101;
    result = NumUtils.intFromBits(arr, ByteOrder.BIG_ENDIAN, 1, 3);
    assertEquals("Failure from intFromBits with subarray, big endian",
      expected, result);
  }

  /**
   * Test the intFromBits method in its little endian variant.
   */
  @Test
  public void testIntFromBitsLE() {
    byte[] arr = {0, 1, 0, 1};
    int expected = 0b1010;
    int result = NumUtils.intFromBits(arr, ByteOrder.LITTLE_ENDIAN);
    assertEquals("Failure from intFromBits little endian", expected, result);
    expected = 0b10;
    result = NumUtils.intFromBits(arr, ByteOrder.LITTLE_ENDIAN, 0, 2);
    assertEquals("Failure from intFromBits with subarray, little endian",
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
   * Test the intFromBytes method when only a subset of the input array is to
   * be taken into account.
   */
  @Test
  public void testIntFromBytesSubarray() {
    byte[] input = {0x05, 0x17, 0x2A, 0x22, 0x42};
    int start = 0;
    int end = 2;
    int expected = 0x0517;
    int result = NumUtils.intFromBytes(input, ByteOrder.BIG_ENDIAN, start,
      end);
    assertEquals("intFromBytes subarray big endian failure", expected, result);
    result = NumUtils.intFromBytes(input, start, end);
    assertEquals("intFromBytes subarray not defaulting to big endian",
      expected, result);
    start = 1;
    end = 4;
    expected = 0x222A17;
    result = NumUtils.intFromBytes(input, ByteOrder.LITTLE_ENDIAN, start, end);
    assertEquals("intFromBytes subarray little endian failure", expected,
      result);
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

  /**
   * Test the byteArrayFromInt method with both little and big endian
   * orderings.
   */
  @Test
  public void testByteArrayFromInt() {
    int n = 0xDEADBEEF;
    byte[] expectedBE = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF };
    byte[] result = NumUtils.byteArrayFromInt(n);
    assertArrayEquals("Failure from byteArrayFromInt, big endian",
      expectedBE, result);
    byte[] expectedLE = { (byte) 0xEF, (byte) 0xBE, (byte) 0xAD, (byte) 0xDE };
    result = NumUtils.byteArrayFromInt(n, ByteOrder.LITTLE_ENDIAN);
    assertArrayEquals("Failure from byteArrayFromInt, little endian",
      expectedLE, result);
  }

  /**
   * Test the intArrayFromByteArray method in both big and little endian
   * ordering.
   */
  @Test
  public void testIntArrayFromByteArray() {
    byte[] input = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF,
                    (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
    int[] bigEndian = { 0xDEADBEEF, 0xCAFEBABE };
    int[] result = NumUtils.intArrayFromByteArray(input, ByteOrder.BIG_ENDIAN);
    assertArrayEquals("Failure from intArrayFromByteArray big endian",
      bigEndian, result);
    result = NumUtils.intArrayFromByteArray(input);
    assertArrayEquals("intArrayFromByteArray not defaulting to big endian",
      bigEndian, result);
    int[] littleEndian = { 0xEFBEADDE, 0xBEBAFECA };
    result = NumUtils.intArrayFromByteArray(input, ByteOrder.LITTLE_ENDIAN);
    assertArrayEquals("Failure from intArrayFromByteArray little endian",
      littleEndian, result);
  }

  /**
   * Test the byteArrayFromBits method in both its big and little endian
   * versions.
   */
  @Test
  public void testByteArrayFromBits() {
    byte[] input = { 0, 1, 0, 1,   1, 1, 0, 1,
                     0, 0, 1, 0,   1, 1, 0, 1 };
    byte[] expectedBE = { 0b01011101, 0b00101101 };
    byte[] expectedLE = { (byte) 0b10111010, (byte) 0b10110100 };
    byte[] result = NumUtils.byteArrayFromBits(input, ByteOrder.BIG_ENDIAN);
    assertArrayEquals("Failure from byteArrayFromBits big endian", expectedBE,
      result);
    result = NumUtils.byteArrayFromBits(input, ByteOrder.LITTLE_ENDIAN);
    assertArrayEquals("Failure from byteArrayFromBits little endian",
      expectedLE, result);
  }

  /**
   * Test the byteArrayFromBits method in both its big and little endian
   * versions, when the input is a subset of the array given.
   */
  @Test
  public void testByteArrayFromBitsSubarray() {
    byte[] input = { 0, 1, 0, 1,   1, 1, 0, 1,
                     0, 0, 1, 0,   1, 1, 0, 1 };
    byte[] expectedBE = { 0b01011101, 0b00101 };
    byte[] result = NumUtils.byteArrayFromBits(input, ByteOrder.BIG_ENDIAN,
      0, 13);
    assertArrayEquals("byteArrayFromBits big endian with subarray failed",
      expectedBE, result);
    byte[] expectedLE = { 0b011 };
    result = NumUtils.byteArrayFromBits(input, ByteOrder.LITTLE_ENDIAN, 4, 3);
    assertArrayEquals("byteArrayFromBits little endian with subarray failed",
      expectedLE, result);
  }

  /**
   * Test the placeInLSB method.
   */
  @Test
  public void testPlaceInLSB() {
    int[] covers = { 0xFF, 0xE3, 0x92, 0x2C };
    int[] bits = { 0, 1, 1, 0 };
    int[] expected = { 0xFE, 0xE3, 0x93, 0x2C };
    for(int i = 0; i < covers.length; i++) {
      int result = NumUtils.placeInLSB(covers[i], bits[i]);
      assertEquals("Place in LSB failure", expected[i], result);
    }
  }
}
