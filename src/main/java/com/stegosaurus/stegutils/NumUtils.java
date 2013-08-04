package com.stegosaurus.stegutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Provides utility methods for dealing with numerical systems. This includes
 * transforming bytes and/or bits into ints, whether in big or little endian
 * ordering, and other similar operations.
 */
public final class NumUtils {

  /**
   * Private CTOR.
   */
  private NumUtils() {
  }

  /**
   * Interpret the byte array given as an int. Use big endian ordering by
   * default, so {0xA3, 0x98} becomes 0xA398.
   * 
   * @param bytes the byte array in question
   * @return the int worked out from the byte array
   */
  public static int intFromBytes(byte[] bytes) {
    return intFromBytes(bytes, ByteOrder.BIG_ENDIAN);
  }

  /**
   * Interpret the byte array given as an int, with the ordering given.
   * @param bytes the byte array in question
   * @param order the ordering to use, ie either BIG_ENDIAN or LITTLE_ENDIAN
   * @return the int worked out from the byte array
   */
  public static int intFromBytes(byte[] bytes, ByteOrder order) {
    if(bytes.length > 4) {
      throw new IllegalArgumentException("The byte array given is too long");
    }
    /* We have to be sure to pad the thing properly, or the buffer will scream
     * at us. What we do is pad it with 0s that will have no importance
     * whatsoever (ie they are "to the left" of the resulting number). In
     * little endian order, then, these go after the important data, while in
     * big endian order they go before.
     */
    if(order == ByteOrder.LITTLE_ENDIAN) {
      bytes = ArrayUtils.addAll(bytes, new byte[4 - bytes.length]);
    } else {
      bytes = ArrayUtils.addAll(new byte[4 - bytes.length], bytes);
    }
    ByteBuffer wrapped = ByteBuffer.wrap(bytes);
    wrapped.order(order);
    return wrapped.getInt();
  }

  /**
   * Interpret the byte array given as an int, in big endian ordering, taking
   * only into account the bytes between the start index and the end index.
   * @param bytes the byte array in question
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   */
  public static int intFromBytes(byte[] bytes, int start, int end) {
    return intFromBytes(bytes, ByteOrder.BIG_ENDIAN, start, end);
  }

  /**
   * Interpret the byte array given as an int, in the ordering given, taking
   * only into account the bytes between the start index and the end index.
   * @param bytes the byte array in question
   * @param order the ordering to use
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   */
  public static int intFromBytes(byte[] bytes, ByteOrder order, int start,
                                 int end) {
    return intFromBytes(ArrayUtils.subarray(bytes, start, end), order);
  }

  /**
   * Interpret the bit array given as a big endian int composed of size bits.
   * So, {1, 0, 0, 1}, of size 4, becomes the int 0b1001 or 5.
   * 
   * @param bits the bit array.
   * @return the int worked out from the bit array.
   */
  public static int intFromBits(byte[] bits) {
    return intFromBits(bits, ByteOrder.BIG_ENDIAN);
  }

  /**
   * Given a bit array, interpret it as an int, using the byte order given.
   * 
   * @param bits the bit array
   * @param order the order to use, either little or big endian
   * @return the int worked out from the bit array.
   */
  public static int intFromBits(byte[] bits, ByteOrder order) {
    return intFromBits(bits, order, 0, bits.length);
  }

  /**
   * Given a bit array, interpret the subset of it given as an int, using
   * big endian ordering.
   * 
   * @param bits the bit array
   * @param start the index to start looking at
   * @param len how many bits to use.
   * @return the int worked out from the bit array.
   */
  public static int intFromBits(byte[] bits, int start, int len) {
    return intFromBits(bits, ByteOrder.BIG_ENDIAN, start, len);
  }

  /**
   * Given a bit array, interpret the subset of it given as an int, using
   * the byte order given.
   * 
   * @param bits the bit array
   * @param order the order to use, either little or big endian.
   * @param start the index to start looking at
   * @param len how many bits to use.
   * @return the int worked out from the bit array.
   */
  public static int intFromBits(byte[] bits, ByteOrder order, int start,
                                int len) {
    int retval = 0;
    for (int i = 0; i < len; i++) {
      int index = i + start;
      if(order == ByteOrder.BIG_ENDIAN) {
        retval += bits[index] << (len - i - 1);
      } else {
        retval += bits[index] << i;
      }
    }
    return retval;
  }

  /**
   * Given some bits, create a byte array out of them, using the byte order
   * given. Every byte is assumed to be 8 bits long.
   * @param bits the bits to use.
   * @param order the byte order to use.
   * @param start the offset to start fetching bits from.
   * @param len the number of bits to fetch.
   */
  public static byte[] byteArrayFromBits(byte[] bits, ByteOrder order,
    int start, int len) {
    int size = len % 8 == 0 ? (len / 8) : (len / 8) + 1;
    byte[] retval= new byte[size];
    int i;
    for(i = 0; i < (len / 8); i++) {
      int index = start + (i * 8);
      retval[i] = (byte) intFromBits(bits, order, index, 8);
    }
    if(len % 8 != 0) {
      retval[i] = (byte) intFromBits(bits, order, start + i * 8, len % 8);
    }
    return retval;
  }

  /**
   * Given some bits, create a byte array out of them, using Big Endian
   * ordering.
   * @param bits the bits to use.
   * @param start the offset to start fetching bits from.
   * @param len the number of bits to fetch.
   */
  public static byte[] byteArrayFromBits(byte[] bits, int start, int len) {
    return byteArrayFromBits(bits, ByteOrder.BIG_ENDIAN, start, len);
  }

  /**
   * Given some bits, create a byte array out of them, using the byte order
   * given.
   * @param bits the bits to use.
   * @param order the byte order to use.
   */
  public static byte[] byteArrayFromBits(byte[] bits, ByteOrder order) {
    return byteArrayFromBits(bits, order, 0, bits.length);
  }

  /**
   * Given some bits, create a byte array out of them, using big endian
   * ordering.
   * @param bits the bits to use.
   */
  public static byte[] byteArrayFromBits(byte[] bits) {
    return byteArrayFromBits(bits, 0, bits.length);
  }

  /**
   * Given an array of n ints return an equivalent array of n * 4 bytes.
   * By default this uses big endian order.
   * @param array the array of ints to transform
   * @return the corresponding byte array.
   */
  public static byte[] byteArrayFromIntArray(int[] array) {
    return byteArrayFromIntArray(array, ByteOrder.BIG_ENDIAN);
  }

  /**
   * Given an array of n ints return an equivalent array of n * 4 bytes.
   * @param array the array of ints to transform
   * @param order the ordering to use (big or little endian)
   * @return the corresponding byte array
   */
  public static byte[] byteArrayFromIntArray(int[] array, ByteOrder order) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(array.length * 4);
    byteBuffer.order(order);
    IntBuffer intBuffer = byteBuffer.asIntBuffer();
    intBuffer.put(array);
    return byteBuffer.array();
  }

  /**
   * Given an int, return an equivalent array of 4 bytes.
   * @param n the int to transform
   * @param order the ordering to use
   * @return the corresponding byte array
   */
  public static byte[] byteArrayFromInt(int n, ByteOrder order) {
    int[] arr = { n };
    return byteArrayFromIntArray(arr, order);
  }

  /**
   * Given an int, return an equivalent array of 4 bytes, using big endian
   * ordering.
   * @param n the int to transform
   * @return the corresponding byte array
   */
  public static byte[] byteArrayFromInt(int n) {
    return byteArrayFromInt(n, ByteOrder.BIG_ENDIAN);
  }

  /**
   * Given an array of n * 4 bytes, return an equivalent array of n ints. 
   * Big endian ordering is used.
   * @param array the array of bytes to transform
   * @return the corresponding int array
   */
  public static int[] intArrayFromByteArray(byte[] array) {
    return intArrayFromByteArray(array, ByteOrder.BIG_ENDIAN);
  }

  /**
   * Given an array of n * 4 bytes, return an equivalent array of n ints. 
   * @param array the array of bytes to transform
   * @param order the byte order to use
   * @return the corresponding int array
   */
  public static int[] intArrayFromByteArray(byte[] array, ByteOrder order) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(array).order(order);
    IntBuffer intBuffer = byteBuffer.asIntBuffer();
    int[] retval = new int[intBuffer.remaining()];
    intBuffer.get(retval);
    return retval;
  }

  /**
   * Place the bit given in the LSB of the cover int given.
   * @param cover the cover that will receive the int given.
   * @param bit the bit to place.
   * @todo does this really belong in this class?
   */
  public static int placeInLSB(int cover, int bit) {
    if(bit != 1 && bit != 0) {
      throw new IllegalArgumentException(bit + " not a bit, should be 0 or 1");
    }
    return (cover & (~1)) | bit;
  }
}
