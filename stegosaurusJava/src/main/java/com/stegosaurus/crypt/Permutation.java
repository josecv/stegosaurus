/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
   * The seed used to generate this permutation.
   */
  private long seed;

  /**
   * The actual permutation.
   */
  private int[] permutation = null;

  /**
   * Construct a new permutation. You should call init after constructing this.
   * @param size the number of elements in the permutations.
   * @param seed the seed that will be used to generate this permutation.
   */
  public Permutation(int size, long seed) {
    this.size = size;
    this.seed = seed;
  }


  /**
   * Initialize the permutation.
   * This is a somewhat expensive operation, so it's separate from the
   * constructor. You should only call this operation once.
   */
  public void init() {
    Random random = new Random(seed);
    if(permutation != null) {
      throw new IllegalStateException("Permutation has been initialized");
    }
    permutation = new int[size];
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
