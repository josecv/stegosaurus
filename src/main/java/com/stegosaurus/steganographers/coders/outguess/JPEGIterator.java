package com.stegosaurus.steganographers.coders.outguess;

import gnu.trove.iterator.TIntIterator;

/**
 * Iterates over the coefficients in a JPEG image.
 */
public interface JPEGIterator extends TIntIterator {
  /**
   * Reseed this iterator with the seed given.
   * @param seed the seed to use.
   * @param messageLen the length of the message to embed, in bytes.
   */
  void reseed(long seed, int messageLen);

  /**
   * Get the next index that may be used for a status bit.
   * @return the next index.
   * @throws IllegalStateException if invoked after getting a non-status index.
   */
  int nextStatus();

  /**
   * Skip the current index, returning the one immediately after it.
   * @return the index after the current one.
   */
  int skipIndex();
}
