package com.stegosaurus.steganographers.genetic;

import java.util.Random;

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.IndividualFactory;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.PM1Embedder;

/**
 * Builds BlockinessIndividuals for a specific image, message, key and seed.
 */
public class BlockinessIndividualFactory
  implements IndividualFactory<BlockinessIndividual> {

  /**
   * The pseudo-random number generator to use.
   */
  private Random prng;

  /**
   * The seed.
   */
  private short seed;

  /**
   * The embedder factory in use.
   */
  private PM1Embedder.Factory embedderFactory;

  /**
   * The request to optimize for.
   */
  private EmbedRequest request;

  /**
   * CTOR.
   * @param request the EmbedRequest we want to optimize for.
   * @param seed the seed used to re-seed the embedding algorithm.
   * @param embedderFactory a factory to construct PM1Embedders.
   */
  public BlockinessIndividualFactory(EmbedRequest request, short seed,
                                     PM1Embedder.Factory embedderFactory) {
    this.request = request;
    this.seed = seed;
    this.embedderFactory = embedderFactory;
    prng = new Random();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BlockinessIndividual build(Chromosome c) {
    return new BlockinessIndividual(c, request, prng, seed, embedderFactory);
  }
}
