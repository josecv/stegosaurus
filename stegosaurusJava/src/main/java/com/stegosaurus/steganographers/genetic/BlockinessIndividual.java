/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stegosaurus.steganographers.genetic;

import java.util.Random;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.genetic.AbstractIndividual;
import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.pm1.PM1Embedder;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;

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
  private PM1EmbedderFactory embedderFactory;

  /**
   * The last stego image produced.
   */
  private JPEGImage stego = null;

  /**
   * The seed we'll use to reseed the permutation.
   */
  private short seed;

  /**
   * The embed request we're optimizing for.
   */
  private EmbedRequest request;

  /**
   * CTOR.
   * @param c the chromosome for this object.
   * @param request the EmbedRequest we want to optimize for.
   * @param seed the seed used to re-seed the embedding algorithm.
   * @param embedderFactory a factory to construct PM1Embedders.
   */
  public BlockinessIndividual(Chromosome c, EmbedRequest request,
                              short seed,
                              PM1EmbedderFactory embedderFactory) {
    super(c);
    this.seed = seed;
    this.embedderFactory = embedderFactory;
    this.request = request;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateImpl() {
    PM1Embedder embedder = embedderFactory.build(chromosome);
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
    /* We want to maximize this ratio; since the GeneticAlgorithm class
     * seeks to _minimize_ the fitness, and the ratio is guaranteed to be
     * between 0 and 1, we can just do this. */
    return 1.0 - (stego.calculateReciprocalROB());
  }
}
