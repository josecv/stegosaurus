package com.stegosaurus.genetic;

import java.util.List;
import java.util.Random;

/**
 * Uses linear rank selection to select pairs of Individuals to cross over
 * from a population pool.
 * It makes use of a gradient factor: the larger the factor, the larger
 * the probability that the fittest element will be selected.
 */
public class RankSelection<T extends Individual<T>>
  implements SelectionOperator<T> {

  /**
   * Construct a selection operator.
   * @param factor the factor for the selection.
   */
  public RankSelection(double factor) {
    this.factor = factor;
  }

  /**
   * The factor.
   */
  private final double factor;

  /**
   * {@inheritDoc}
   */
  @Override
  public int select(List<? extends Individual<T>> population, Random random) {
    int n = population.size();
    if(n == 1) {
      return 0;
    }
    double nm = 2 / (factor + 1);
    double np = (2 * factor) / (factor + 1);
    while(true) {
      /* The higher ranking members are very likely to be selected, whereas
       * the lower ranking are not. To steer clear of any biases that might
       * be introduced by this, we loop through the population in reverse.
       */
      for(int i = n - 1; i >= 0; i--) {
        double rank = n - i;
        double p = (1 / (double) n) * (nm + (np - nm) * ((rank - 1) / (n - 1)));
        double next = random.nextDouble();
        if(next < p) {
          return i;
        }
      }
    }
  }
}
