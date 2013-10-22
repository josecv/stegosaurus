package com.stegosaurus.crypt;

import java.util.Random;

/**
 * Produces a random permutation of numbers.
 */
public class Permutation {
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
  }

  /**
   * Get the element at the index given.
   * @param index the index.
   * @return the element located at the index given.
   */
  public int get(int index) {
    return permutation[index];
  }

  /**
   * Get this permutation's size.
   * @return the permutation's size.
   */
  public int getSize() {
    return size;
  }
}
