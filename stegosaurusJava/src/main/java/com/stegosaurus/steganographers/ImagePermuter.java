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
package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import java.nio.charset.Charset;
import java.util.BitSet;

import com.google.common.hash.HashFunction;
import com.google.inject.Inject;
import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.cppIntArray;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.crypt.PermutationProvider;

/**
 * Given a coefficient accessor and a permutation for it, will walk through
 * the permutation and execute a callback for every value that is not a DC
 * coefficient and not a 0.
 * <p>The permuter keeps track of already accessed indices and ensures they're
 * not repeated, so that it is possible to walk more than one permutation of
 * the same image.</p>
 * <p>Note however that it will start walking every permutation from the
 * very start.</p>
 */
public class ImagePermuter {

  /**
   * The permutation in use.
   */
  private Permutation permutation;

  /**
   * The coefficient accessor we're permuting.
   */
  private CoefficientAccessor accessor;

  /**
   * The coefficients that have already been visited.
   */
  private BitSet locked;

  /**
   * The array of usable coefficients, as given to us by our accessor.
   */
  private cppIntArray usables;

  /**
   * The permutation provider to acquire the permutation objects that will
   * underlie this object.
   */
  private PermutationProvider permutationProvider;

  /**
   * The number of coefficients we're permuting.
   */
  private int length;

  /**
   * CTOR.
   * @param acc the coefficient accessor to use.
   * @param seed the seed to use for the permutation.
   * @param p the permutation of its indices.
   */
  protected ImagePermuter(CoefficientAccessor acc, long seed,
                          PermutationProvider permutationProvider) {
    accessor = acc;
    this.permutationProvider = permutationProvider;
    length = acc.getUsableCoefficientCount();
    this.permutation = permutationProvider.getPermutation(length, seed);
    locked = new BitSet(permutation.getSize());
    usables = cppIntArray.frompointer(acc.getUsableCoefficients());
  }

  /**
   * Change the permutation in use by this object; this does NOT reset the
   * object: visited indices will remain visited.
   * @param seed the seed for the permutation to change to.
   */
  public void setSeed(long seed) {
    permutation = permutationProvider.getPermutation(length, seed);
  }

  /**
   * Reset this permuter, thus allowing it to re-visit any previously visited
   * indices.
   */
  public void reset() {
    locked.clear();
  }

  /**
   * Walk the permuted image, running the procedure given on every good
   * coefficient found (ie every non zero, non DC, coefficient).
   * Note that the permutation will be walked from its start, regardless
   * of whether this method has already been invoked.
   * The procedure's first argument is the index, and its second argument is
   * the value.
   * @param proc the procedure to run.
   */
  public void walk(TIntIntProcedure proc) {
    boolean go = true;
    final int size = permutation.getSize();
    for(int i = 0; i < size && go; i++) {
      int index = permutation.get(i);
      /* getCoefficient is a native call, so we want to avoid it if possible,
       * which is why we ensure the coefficient is not DC before going any
       * further */
      if(!locked.get(index)) {
        int trueIndex = usables.getitem(index);
        int value = accessor.getCoefficient(trueIndex);
        locked.set(index);
        go = proc.execute(trueIndex, value);
      }
    }
  }

  /**
   * A factory capable of building ImagePermuter instances.
   * Will inject some required objects into the built instances.
   */
  public static class Factory {
    /**
     * The permutation provider that will be injected to built instances.
     */
    private PermutationProvider provider;

    /**
     * The hash function to use to hash keys into seeds.
     */
    private HashFunction hashFunction;

    /**
     * The charset to encode keys with.
     */
    private Charset charset;

    /**
     * Build a new ImagePermuter Factory; should only be invoked via Guice.
     * @param provider the permutation provider to use for built permuters.
     * @param hashFunction the hash function to hash String keys to seeds.
     * @param charset the charset to encode keys with.
     */
    @Inject
    public Factory(PermutationProvider provider, HashFunction hashFunction,
                   Charset charset) {
      this.provider = provider;
      this.hashFunction = hashFunction;
      this.charset = charset;
    }

    /**
     * Build a new ImagePermuter to permute the CoefficientAccessor given.
     * @param accessor the accessor whose coefficients will be permuted.
     * @param seed the seed to generate the actual permutation.
     */
    public ImagePermuter build(CoefficientAccessor acc, long seed) {
      return new ImagePermuter(acc, seed, provider);
    }

    /**
     * Build a new ImagePermuter to permute the CoefficientAccessor given.
     * Serves as a shortcut to the other build method, and will somehow
     * produce an integer valued seed from the key.
     * @param accessor the accessor whose coefficients will be permuted.
     * @param key the key, as a String, to generate the actual permutation.
     */
    public ImagePermuter build(CoefficientAccessor acc, String key) {
      return build(acc, hashFunction.hashString(key, charset).asLong());
    }
  }
}
