package com.stegosaurus.crypt;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.Random;

import com.google.inject.Singleton;

/**
 * Provides Permutation objects for given seeds and given lengths.
 * Permutations being immutable, this object actually keeps a reference to any
 * previously requested instances and returns them again on subsequent
 * requests
 */
@Singleton
public class DefaultPermutationProvider implements PermutationProvider {
  /**
   * The random number generators used to generate the permutations.
   * We don't want any crazy stuff going on here, and we want
   * the permutations to be deterministic, so these are thread local.
   */
  private ThreadLocal<Random> random =
    new ThreadLocal<Random>() {
      protected Random initialValue() {
        return new Random();
      }
  };

  /**
   * The actual permutations to return, as a map mapping from permutation
   * size to permutation seed to the actual permutation.
   * Note that this particular object isn't inherently thread safe at all;
   * instead, the getPermutation method is synchronized to hopefully prevent
   * any sillyness.
   */
  private final TIntObjectMap<TLongObjectMap<Permutation>> permutations =
    new TIntObjectHashMap<>();


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized Permutation getPermutation(int size, long seed) {
    TLongObjectMap<Permutation> withLength = permutations.get(size);
    if(withLength == null) {
      withLength = new TLongObjectHashMap<>();
      permutations.put(size, withLength);
    }
    Permutation retval = withLength.get(seed);
    if(retval == null) {
      Random r = random.get();
      r.setSeed(seed);
      retval = new Permutation(size, r);
      retval.init();
      withLength.put(seed, retval);
    }
    return retval;
  }
}
