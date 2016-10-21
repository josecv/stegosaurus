package com.stegosaurus.steganographers.genetic;

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.IndividualFactory;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.pm1.PM1Embedder;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;
import com.stegosaurus.steganographers.utils.DummyPMSequence;

/**
 * Builds SeedChangeCountIndividual instances.
 */
public class SeedChangeCountIndividualFactory
  implements IndividualFactory<SeedChangeCountIndividual> {

  /**
   * The embed request we're servicing.
   */
  private EmbedRequest request;

  /**
   * The embedder.
   */
  private PM1Embedder embedder;

  /**
   * Construct a factory to build individuals used in the optimization of
   * seeds for a given EmbedRequest.
   * @param request the embed request to optimize for.
   * @param factory an embedder factory.
   * @see SeedChangeCountIndividual for info on the PM1Embedders built here.
   */
  public SeedChangeCountIndividualFactory(EmbedRequest request,
                                          PM1EmbedderFactory factory) {
    this.embedder = factory.build(new DummyPMSequence());
    this.request = request;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SeedChangeCountIndividual build(Chromosome c) {
    EmbedRequest r = new EmbedRequest(request);
    return new SeedChangeCountIndividual(c, r, embedder);
  }
}
