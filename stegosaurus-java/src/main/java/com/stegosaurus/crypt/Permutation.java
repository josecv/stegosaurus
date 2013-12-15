package com.stegosaurus.crypt;

import java.util.Random;

/**
 * Produces a (pseudo) random permutation of numbers.
 * These are immutable objects, so that for any given random seed, permutation
 * size and prng, two permutations will be identical.
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
   * Initialize the permutation.
   * This is a somewhat expensive operation, so it's separate from the
   * constructor. You should only call this operation once.
   */
  public void init() {
    if(permutation != null) {
      throw new IllegalStateException("Permutation has been initialized");
    }
    permutation = new int[size];
    /* This stuff is just your basic Knuth permutation. */
    for(int i = 0; i < size; i++) {
      int j = random.nextInt(i + 1);
      if(j != i) {
        permutation[i] = permutation[j];
      }
      permutation[j] = i;
    }
  }

  /**
   * Get the element at the index given.
   * @param index the index.
   * @return the element located at the index given.
   * @throws IllegalStateException if init() has not been called.
   */
  public int get(int index) {
    if(permutation == null) {
      throw new IllegalStateException("init() has not been called");
    }
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
