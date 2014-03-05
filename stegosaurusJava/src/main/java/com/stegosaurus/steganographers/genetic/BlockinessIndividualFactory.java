package com.stegosaurus.steganographers.genetic;

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
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BlockinessIndividual build(Chromosome c) {
    /* We have to hand out a copy of the image to the individual, so as to
     * be able to parallelize some operations. Happily, because the JPEGImage
     * has not been manipulated in any significant way, writeNew is a pretty
     * fast operation here.
     */
    EmbedRequest r = new EmbedRequest(request);
    return new BlockinessIndividual(c, r, seed, embedderFactory);
  }
}
