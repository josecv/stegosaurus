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
   * The parameters for the algorithm.
   */
  private GeneticPM1Parameters params;

  /**
   * CTOR.
   * @param embedderFactory the factory for embedders
   * @param gaFactory the factory for genetic algorithms
   */
  @Inject
  public GeneticPM1Factory(PM1EmbedderFactory embedderFactory,
      GAFactory gaFactory, GeneticPM1Parameters params) {
    this.embedderFactory = embedderFactory;
    this.gaFactory = gaFactory;
    this.params = params;
  }

  /**
   * Build a new genetic embedder.
   */
  @Override
  public Embedder build() {
    return new GeneticPM1(embedderFactory, gaFactory, params);
  }
}
