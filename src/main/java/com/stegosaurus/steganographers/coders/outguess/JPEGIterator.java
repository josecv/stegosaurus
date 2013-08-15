package com.stegosaurus.steganographers.coders.outguess;

import gnu.trove.iterator.TIntIterator;

/**
 * Iterates over the coefficients in a JPEG image.
 */
public interface JPEGIterator extends TIntIterator {
  /**
   * Reseed this iterator with the seed given. Functionally equivalent to
   * resetting it.
   * @param seed the seed to use.
   * @param messageLen the length of the message to embed, in bytes.
   * @param coverLen the length of the cover image, in bytes.
   * @param coverIndex the first index available for embedding in the image.
   */
  void reseed(long seed, int messageLen, int coverLen, int coverIndex);

  /**
   * Skip the current index, returning the one immediately after it.
   * @return the index after the current one.
   */
  int skipIndex();
}
