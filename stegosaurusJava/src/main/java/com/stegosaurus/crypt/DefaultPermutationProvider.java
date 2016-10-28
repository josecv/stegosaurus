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
                    Permutation retval = new Permutation(size, seed);
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
  public Permutation getPermutation(int size, long seed) {
    return permutations.getUnchecked(size).getUnchecked(seed);
  }
}
