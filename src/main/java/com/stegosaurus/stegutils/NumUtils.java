package com.stegosaurus.stegutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Provides utility methods for dealing with numerical systems. This includes
 * transforming bytes and/or bits into ints, whether in big or little endian
 * ordering, and other similar operations.
 */
public final class NumUtils {

  private NumUtils() {
  }

  /**
   * Interpret the byte array given as a little endian int composed of size
   * bytes. So {0xA3, 0x98} becomes 0x98A3.
   * 
   * @param bytes the byte array in question
   * @return the int worked out from the byte array
   */
  public static int intFromBytesLE(byte[] bytes) {
    if(bytes.length > 4) {
      throw new IllegalArgumentException("The byte array given is too long");
    }
    bytes = ArrayUtils.addAll(bytes, new byte[4 - bytes.length]);
    ByteBuffer wrapped = ByteBuffer.wrap(bytes);
    wrapped.order(ByteOrder.LITTLE_ENDIAN);
    return wrapped.getInt();
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
  public static int intFromBitsBE(byte[] bits, int size) {
    int retval = 0;
    int len = bits.length < size ? bits.length : size;
    for (int i = 0; i < len; i++) {
      retval += ((int) bits[i]) << (size - i - 1);
    }
    return retval;
  }
}
