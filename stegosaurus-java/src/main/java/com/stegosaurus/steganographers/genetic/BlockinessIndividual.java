package com.stegosaurus.steganographers.genetic;

import java.util.Random;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.PM1Embedder;

/**
 * An individual to be used in an algorithm to optimize the blockiness ratio
 * for a PM Embedding.
 * The chromosome in use is the PM Sequence to use for embedding.
 * Its simulation attempts to embed a message into a JPEG image.
 * Finally, the fitness function is the ratio between the blockiness of the
 * stego image, and the blockiness of an estimated image (the stego image
 * having been cropped by 4 pixels from the top and left).
 */
public class BlockinessIndividual
  extends AbstractIndividual<BlockinessIndividual> {

  /**
   * The embedder factory.
   */
  private PM1Embedder.Factory embedderFactory;

  /**
   * The last stego image produced.
   */
  private JPEGImage stego = null;

  /**
   * The seed we'll use to reseed the permutation.
   */
  private short seed;

  /**
   * A pseudo random number generator.
   */
  private Random random;

  /**
   * The embed request we're optimizing for.
   */
  private EmbedRequest request;

  /**
   * CTOR.
   * @param c the chromosome for this object.
   * @param request the EmbedRequest we want to optimize for.
   * @param random a prng.
   * @param seed the seed used to re-seed the embedding algorithm.
   * @param embedderFactory a factory to construct PM1Embedders.
   */
  public BlockinessIndividual(Chromosome c, EmbedRequest request,
                              Random random, short seed,
                              PM1Embedder.Factory embedderFactory) {
    super(c);
    this.random = random;
    this.seed = seed;
    this.embedderFactory = embedderFactory;
    this.request = request;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateImpl() {
    PM1Embedder embedder = embedderFactory.build(random, chromosome);
    /* Can't hurt to make sure the stego image has been deleted. */
    /* TODO Do we _really_ need to, though? */
    if(stego != null) {
      stego.delete();
    }
    stego = embedder.embed(request, seed);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double calculateFitnessImpl() {
    JPEGImage expected = stego.doCrop(4, 4);
    double expectedBlockiness = 0.0, stegoBlockiness = 0.0;
    for(int c = 0; c < stego.getComponentCount(); c++) {
      expectedBlockiness += expected.getComponent(c).calculateBlockiness();
      stegoBlockiness += stego.getComponent(c).calculateBlockiness();
    }
    /* We want to maximize this ratio; since the GeneticAlgorithm class
     * seeks to _minimize_ the fitness, and the ratio is guaranteed to be
     * between 0 and 1, we can just do this. */
    return 1.0 - (expectedBlockiness / stegoBlockiness);
  }
}
