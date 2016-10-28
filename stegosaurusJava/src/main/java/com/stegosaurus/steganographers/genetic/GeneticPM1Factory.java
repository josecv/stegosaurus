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
