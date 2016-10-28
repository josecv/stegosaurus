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

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.IndividualFactory;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.pm1.PM1Embedder;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;

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
  private PM1EmbedderFactory embedderFactory;

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
                                     PM1EmbedderFactory embedderFactory) {
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
