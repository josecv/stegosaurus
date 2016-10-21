package com.stegosaurus.steganographers.genetic;

import com.google.inject.Inject;

import com.stegosaurus.genetic.GAFactory;
import com.stegosaurus.steganographers.EmbedderFactory;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.pm1.PM1Embedder;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;

/**
 * Creates new GeneticPM1 embedders factory.
 */
public class GeneticPM1Factory implements EmbedderFactory {

  /**
   * The embedder factory.
   */
  private PM1EmbedderFactory embedderFactory;

  /**
   * The genetic algorithm factory.
   */
  private GAFactory gaFactory;

  /**
   * CTOR.
   * @param embedderFactory the factory for embedders
   * @param gaFactory the factory for genetic algorithms
   */
  @Inject
  public GeneticPM1Factory(PM1EmbedderFactory embedderFactory,
                           GAFactory gaFactory) {
    this.embedderFactory = embedderFactory;
    this.gaFactory = gaFactory;
  }

  /**
   * Build a new genetic embedder.
   */
  @Override
  public Embedder build() {
    return new GeneticPM1(embedderFactory, gaFactory);
  }
}
