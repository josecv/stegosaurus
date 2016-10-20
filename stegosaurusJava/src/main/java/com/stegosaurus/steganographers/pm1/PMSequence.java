package com.stegosaurus.steganographers.pm1;

/**
 * A plus-minus sequence to use for embedding.
 */
public interface PMSequence {
  /**
   * Get whether we should increment or decrement the value that will contain
   * the bit at the index given.
   * The index itself should correspond to an index in the message, but the
   * value to be modified is a value in the cover.
   * @param index the index
   * @return true if we should increment the value, false otherwise.
   */
  boolean atIndex(int index);
}
