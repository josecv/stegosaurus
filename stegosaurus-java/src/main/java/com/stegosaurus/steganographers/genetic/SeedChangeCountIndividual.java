package com.stegosaurus.steganographers.genetic;

import com.stegosaurus.genetic.AbstractIndividual;
import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.PM1Embedder;

/**
 * Uses a chromosome as a seed for a PM1Embedding on a JPEG image, and uses
 * the number of changes required for the embedding to determine fitness.
 * Its fitness function is the number of changes required for embedding
 * over the message length, so that it is always between 0 and 1, and the
 * more changes are required, the less fit the individual is.
 */
public class SeedChangeCountIndividual extends
  AbstractIndividual<SeedChangeCountIndividual> {

  /**
   * The embed request to service.
   */
  private EmbedRequest request;

  /**
   * The message we want to embed.
   */
  private byte[] message;

  /**
   * The embedder to use.
   */
  private PM1Embedder embedder;

  /**
   * The number of changes required for embedding (as of the last simulation).
   */
  private int changes;

  /**
   * CTOR.
   * Note that it receives (among other things) a PM1Embedder. This may use
   * any PM1Sequence, since the number of changes required to embed an
   * image is completely independent of the PM sequence used to embed that
   * image.
   * @param c the chromosome for this individual. Will be used as a seed.
   * @param message the message we're interested in embedding.
   * @param key the key to use for embedding.
   * @param img the jpeg image we'll be embedding into.
   * @param embedder the PM1Embedder to use.
   */
  public SeedChangeCountIndividual(Chromosome c, EmbedRequest request,
                                   PM1Embedder embedder) {
    super(c);
    this.request = request;
    this.message = request.getMessage();
    this.embedder = embedder;
    changes = message.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateImpl() {
    changes = embedder.fakeEmbed(request, chromosome.asShort());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double calculateFitnessImpl() {
    return ((double) changes) / ((message.length + Short.SIZE) * 8);
  }
}
