package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import java.util.BitSet;
import java.util.Random;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.crypt.Permutation;

/**
 * Given a coefficient accessor and a permutation for it, will walk through
 * the permutation and execute a callback for every value that is not a DC
 * coefficient and not a 0 or a 1.
 * The permuter keeps track of already accessed indices and ensures they're not
 * repeated, so that it is possible to walk more than one permutation of the
 * same image.
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
   * Given a coefficient accessor, build a Permutation of its indices,
   * suitable to be used in an ImagePermuter instance.
   * It is returned uninitialized.
   * @param r the random number generator for the permutation.
   * @param acc the accessor.
   * @return a permutation of the indices in it.
   */
  public static Permutation buildPermutation(Random r,
      CoefficientAccessor acc) {
    return new Permutation(acc.getLength(), r);
  }

  /**
   * CTOR.
   * @param acc the coefficient accessor to use.
   * @param p the permutation of its indices.
   */
  public ImagePermuter(CoefficientAccessor acc, Permutation p) {
    accessor = acc;
    permutation = p;
    locked = new BitSet(acc.getLength());
  }

  /**
   * Walk the permuted image, running the procedure given on every good
   * coefficient found (ie every non zero, non one, non DC, coefficient).
   * The procedure's first argument is the index, and its second argument is
   * the value.
   * @param proc the procedure to run.
   */
  public void walk(TIntIntProcedure proc) {
    boolean go = true;
    for(int i = 0; i < permutation.getSize() && go; i++) {
      int index = permutation.get(i);
      /* getCoefficient is a native call, so we want to avoid it if possible,
       * which is why we ensure the coefficient is not DC before going any
       * further */
      if(!locked.get(index) && accessor.isDC(index) == 0) {
        int value = accessor.getCoefficient(index);
        if(value != 0) {
          locked.set(index);
          go = proc.execute(index, value);
        }
      }
    }
  }
}
