package com.stegosaurus.genetic;

import com.google.inject.AbstractModule;

import com.stegosaurus.genetic.GAFactory;
import com.stegosaurus.genetic.ParallelGAFactory;


/**
 * A module for genetic algorithm-related classes.
 */
public class GeneticModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GAFactory.class).to(ParallelGAFactory.class);
  }
}
