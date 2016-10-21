package com.stegosaurus.genetic;

import java.util.Random;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Builds Parallel genetic algorithms.
 */
class ParallelGAFactory implements GAFactory {
    /**
     * The executor service to provide to children instances.
     */
    private ListeningExecutorService service;

    /**
     * Construct a factory.
     * @param service the executor service to provide to built instances.
     */
    @Inject
    public ParallelGAFactory(ListeningExecutorService service) {
      this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Individual<C>> GeneticAlgorithm<C> build(
        IndividualFactory<C> factory,
        SelectionOperator<C> selection,
        Random random,
        GAParameters params) {
      return new ParallelGA<C>(factory, selection, random, params, service);
    }
  }
