package com.stegosaurus.crypt;

import java.util.Random;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
   * The actual permutations to return, as a loading cache mapping from
   * permutation size, to the caches mapping from permutation seed to
   * the permutation itself.
   * This way, we can get rid of entire permutation sizes that haven't
   * seen use in a while.
   */
  private final LoadingCache<Integer, LoadingCache<Long, Permutation>>
    permutations;

  /**
   * CTOR.
   */
  public DefaultPermutationProvider() {
    /* TODO Every last thing about this is awful. */
    /* TODO Make this nicer on the eye... */
    /* TODO Externalize the damn parameters */
    permutations = CacheBuilder.newBuilder()
      .maximumSize(5)
      .build(
        new CacheLoader<Integer, LoadingCache<Long, Permutation>>() {
          public LoadingCache<Long, Permutation> load(final Integer size) {
            return CacheBuilder.newBuilder()
              .maximumSize(50)
              .build(
                new CacheLoader<Long, Permutation>() {
                  public Permutation load(Long seed) {
                    Random r = random.get();
                    r.setSeed(seed);
                    Permutation retval = new Permutation(size, r);
                    retval.init();
                    return retval;
                  }
                });
          }
        });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized Permutation getPermutation(int size, long seed) {
    return permutations.getUnchecked(size).getUnchecked(seed);
  }
}
