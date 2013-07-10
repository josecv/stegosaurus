package com.stegosaurus.huffman;

/**
 * A representation of a single code, being the byte to be encoded, the code
 * that stands for it, and the length thereof.
 * 
 * @author joe
 * 
 */
public class HuffmanCode {
  /**
   * The byte that is to be represented by a code.
   */
  public byte target;

  /**
   * The code that represents the byte.
   */
  public int code;

  /**
   * How many bits are required to represent the byte.
   */
  public int length;

  /**
   * Construct a new Huffman code with the values given.
   * 
   * @param target
   *            the byte that is to be represented by a code
   * @param code
   *            the code that represents the byte
   * @param length
   *            how many bits are required to represent the byte
   */
  public HuffmanCode(byte target, int code, int length) {
    this.target = target;
    this.code = code;
    this.length = length;
  }

  /**
   * Overloaded constructor allowing target to be passed as an int. It is then
   * casted, because Java's compiler isn't smart enough to tell that the
   * number 2 is indeed a byte.
   * 
   * @param target
   *            the byte that is to be represented by a code
   * @param code
   *            the code that represents the byte
   * @param length
   *            how many bits are required to represent the byte
   */
  public HuffmanCode(int target, int code, int length) {
    this((byte) target, code, length);
  }

  /**
   * Return whether this huffman code equals the object given. A huffman code
   * equals another if their targets, codes and lengths are the same.
   * 
   * @return true if o is equal to this code.
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof HuffmanCode) {
      HuffmanCode h = (HuffmanCode) o;
      return h.target == target && h.length == length && h.code == code;
    }
    return false;
  }

  /**
   * Return a unique hash code for this particular huffman code. The hash code
   * returned is a function of this code's target, code and length.
   *
   * @return a hash code
   */
  @Override
  public int hashCode() {
    return target + (length * 31) + (code * 31 * 31);
  }

  /**
   * Get a string representation of this huffman code.
   * 
   * @return a string representation of the huffman code.
   */
  @Override
  public String toString() {
    return Integer.toBinaryString(code) + ": " + target + " (" + length
        + ")";
  }
}
