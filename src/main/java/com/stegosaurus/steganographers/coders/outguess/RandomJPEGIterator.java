package com.stegosaurus.steganographers.coders.outguess;

import java.util.Random;

/**
 * Uses a pseudo random number generator to iterate over the coefficients
 * in a JPEG image.
 */
public class RandomJPEGIterator implements JPEGIterator {

  /**
   * The pseudo random number generator in use here.
   */
  private Random prng;

  /**
   * The length of the message, in bits.
   */
  private int messageLen;

  /**
   * The length of the cover medium, in bytes.
   */
  private int coverLen;

  /**
   * The first index that may be used to embed a message bit.
   */
  private int coverIndex;

  /**
   * The upper bound on the interval for random numbers.
   */
  private int interval;

  /**
   * Construct a new RandomJPEGIterator, using the seed and parameters given.
   * @param seed the seed to use.
   * @param messageLen the length of the message to embed, in bytes.
   * @param coverLen the number of bytes to use inside the cover medium.
   * @param coverIndex the first index that may be used for embedding.
   */
  public RandomJPEGIterator(long seed, int messageLen, int coverLen,
                            int coverIndex) {
    prng = new Random();
    reseed(seed, messageLen, coverLen, coverIndex);
  }

  /**
   * Return whether this iterator has another element to return.
   * @return whether there is at least one more element in this iterator.
   */
  @Override
  public boolean hasNext() {
    return getInterval() > 1;
  }

  /**
   * Get the upper bound on the interval for random numbers.
   * @return the upper bound.
   */
  private int getInterval() {
    int coverBits = (coverLen - coverIndex);
    return (2 * coverBits) / messageLen;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int skipIndex() {
    coverIndex++;
    return coverIndex;
  }

  /**
   * Return the next element in this iterator.
   * @return the next element.
   */
  @Override
  public int next() {
    /* We recalculate the interval every 8 bits. */
    if(messageLen % 8 == 0) {
      interval = getInterval();
      coverIndex += prng.nextInt(interval - 1) + 1;
    }
    messageLen--;
    coverIndex++;
    return coverIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reseed(long seed, int messageLen, int coverLen, int coverIndex) {
    prng.setSeed(seed);
    this.messageLen = messageLen * 8;
    this.coverLen = coverLen;
    this.coverIndex = coverIndex;
  }

  /**
   * Attempt to remove an element; this is not supported by this
   * implementation.
   * @throws UnsupportedOperationException invariably.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Can't remove from jpeg iterator");
  }
}
