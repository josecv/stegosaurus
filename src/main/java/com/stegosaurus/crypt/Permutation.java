package com.stegosaurus.crypt;

import gnu.trove.iterator.TIntIterator;

import java.util.Random;

/**
 * Produces a random permutation of numbers.
 */
public class Permutation implements TIntIterator {
  /**
   * The size.
   */
  private int size;
  /**
   * The random number generator we use.
   */
  private Random random;

  /**
   * The actual permutation.
   */
  private int[] permutation = null;

  /**
   * The current index.
   */
  private int index = 0;

  /**
   * Construct a new permutation. You should call init after constructing this.
   * @param size the number of elements in the permutations.
   * @param random a random number generator.
   */
  public Permutation(int size, Random random) {
    this.size = size;
    this.random = random;
  }


  /**
   * Initialize the permutation. This is a somewhat expensive operation, so
   * it's separate from the constructor.
   * Note that it can be called as many times as desired should you, for
   * example, wish to reseed the random number generator.
   */
  public void init() {
    /* No need to allocate a new array if we already have one: the
     * permutation algorithm isn't dependent on an array's contents, so
     * we're fine.
     */
    if(permutation == null) {
      permutation = new int[size];
    }
    /* This stuff is just your basic Knuth permutation. */
    for(int i = 0; i < size; i++) {
      int j = random.nextInt(i + 1);
      permutation[i] = permutation[j];
      permutation[j] = i;
    }
    index = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int next() {
    if(permutation == null) {
      throw new IllegalStateException("Permutation has not been initialized");
    }
    int retval = permutation[index];
    index++;
    return retval;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove from permutation");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasNext() {
    return index < size;
  }
}
