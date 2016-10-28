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
