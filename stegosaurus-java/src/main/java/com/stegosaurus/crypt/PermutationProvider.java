package com.stegosaurus.crypt;

/**
 * Builds and returns permutations of given sizes and using given random
 * seeds.
 * Java's own java.util.Random objects should be used to generate these
 * permutations.
 */
public interface PermutationProvider {
  /**
   * Get a permutation of the given size and seed.
   * @param size the number of elements in the desired permutation.
   * @param seed the seed that should be used to generate it.
   * @return the permutation.
   */
  Permutation getPermutation(int size, long seed);
}
