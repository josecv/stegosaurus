package com.stegosaurus.steganographers.genetic;

import java.util.List;
import java.util.Random;

/**
 * Selects individuals for reproduction out of a list of possible candidates.
 * @param <T> the kind of Individual to operate on.
 */
interface SelectionOperator<T extends Individual<T>> {
  /**
   * Select an individual for reproduction from the population pool given,
   * and remove it from the population.
   * Note that the population pool must be sorted from fittest to least fit
   * (i.e. from the individual with the smallest fitness value, to the one
   * with the largest fitness value).
   * @param population the population pool to select from.
   * @param random the random  number generator to use.
   * @return the selected individual.
   */
  Individual<T> select(List<Individual<T>> population, Random random);
}
