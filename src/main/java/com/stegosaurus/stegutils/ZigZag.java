package com.stegosaurus.stegutils;

/**
 * Deals with zig zag ordering of matrices.
 */
public final class ZigZag {
  /**
   * Private CTOR.
   */
  private ZigZag() { }

  /**
   * The values used to go from a sequentially ordered matrix to its zig-zag
   * ordered equivalent. The indices are those in the matrix, and their
   * corresponding values are the indices in the zig-zag ordered equivalent.
   */
  private static final int[] sequentialToZigZag = {
     0,  1,  5,  6, 14, 15, 27, 28,
     2,  4,  7, 13, 16, 26, 29, 42,
     3,  8, 12, 17, 25, 30, 41, 43,
     9, 11, 18, 24, 31, 40, 44, 53,
    10, 19, 23, 32, 39, 45, 52, 54,
    20, 22, 33, 38, 46, 51, 55, 60,
    21, 34, 37, 47, 50, 56, 59, 61,
    35, 36, 48, 49, 57, 58, 62, 63
  };

  /**
   * The values used to go from a zig-zag ordered run to its sequentially
   * ordered equivalent. The indices are those in the zig-zag run, and their
   * corresponding values are the indices in the sequential matrix equivalent.
   */
  private static final int[] zigZagToSequential = {
     0,  1,  8, 16,  9,  2,  3, 10,
    17, 24, 32, 25, 18, 11,  4,  5,
    12, 19, 26, 33, 40, 48, 41, 34,
    27, 20, 13,  6,  7, 14, 21, 28,
    35, 42, 49, 56, 57, 50, 43, 36,
    29, 22, 15, 23, 30, 37, 44, 51,
    58, 59, 52, 45, 38, 31, 39, 46,
    53, 60, 61, 54, 47, 55, 62, 63
  };

  /**
   * Ensure that the given array is 64 ints long. If not, throw.
   * @param array the array to validate.
   * @throws IllegalArgumentException if its length is not 64.
   */
  private static void validateLength(int[] array) {
    if(array.length != 64) {
      throw new IllegalArgumentException("Input array is not 64 elements long");
    }
  }

  /**
   * Using the guide given, return the input, reordered. Does not change
   * the input array.
   * @param input the array to reorder
   * @param guide an array going from the indices in the input to their
   *    corresponding indices in the output.
   */
  private static int[] reorder(int[] input, int[] guide) {
    validateLength(input);
    int[] retval = new int[input.length];
    for(int i = 0; i < input.length; i++) {
      retval[guide[i]] = input[i];
    }
    return retval;
  }

  /**
   * Given a sequentially ordered matrix of 64 elements, re-order its elements
   * in a zig-zag fashion and return them.
   * @param sequentialOrder the matrix to re-order.
   * @return the zig zag ordered values.
   */
  public static int[] toZigZagOrder(int[] sequentialOrder) {
    return reorder(sequentialOrder, sequentialToZigZag);
  }

  /**
   * Given a zig-zag ordered run of 64 elements, re-order them as a square
   * matrix and return it.
   * @return the sequentially ordered values.
   */
  public static int[] toSequentialOrder(int[] zigZagOrder) {
    return reorder(zigZagOrder, zigZagToSequential);
  }
}
