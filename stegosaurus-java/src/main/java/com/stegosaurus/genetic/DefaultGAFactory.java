package com.stegosaurus.genetic;

import java.util.Random;

/**
 * Constructs instances of the default GeneticAlgorithm implementation.
 */
public class DefaultGAFactory implements GAFactory {
  /**
   * {@inheritDoc}
   */
  @Override
	public <C extends Individual<C>> GeneticAlgorithm<C> build(
        IndividualFactory<C> factory,
        SelectionOperator<C> selection, Random random,
        GAParameters params) {
    return new GeneticAlgorithm<C>(factory, selection, random, params);
  }
}
