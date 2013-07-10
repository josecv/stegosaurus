/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stegosaurus.stegutils;

/**
 * Provides utility methods used throughout.
 * 
 * @author joe
 */
public final class StegUtils {

  private StegUtils() {
  }

  /**
   * Interpret the byte array given as a little endian int composed of size
   * bytes. So {0xA3, 0x98} becomes 0x98A3.
   * 
   * @param bytes
   *            the byte array in question
   * @param size
   *            the number of bytes composing the int
   * @return the int worked out from the byte array
   */
  public static int intFromBytes(byte[] bytes, int size) {
    int retval = 0;
    for (int i = 0; i < size; i++) {
      retval += ((int) bytes[i]) << (i * 8);
    }
    return retval;
  }

  /**
   * Interpret the bit array given as a big endian int composed of size bits.
   * So, {1, 0, 0, 1}, of size 4, becomes the int 0b1001 or 5.
   * 
   * @param bits
   *            the bit array.
   * @param size
   *            the number of bits making up the int.
   * @return the int worked out from the bit array.
   * @see IntFromBytes for a little endian equivalent which deals with entire
   *      bytes.
   */
  public static int intFromBits(byte[] bits, int size) {
    int retval = 0;
    for (int i = 0; i < size; i++) {
      retval += ((int) bits[i]) << (size - i - 1);
    }
    return retval;
  }
}
