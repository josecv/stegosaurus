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
package com.stegosaurus.genetic;

/**
 * A Dummy individual, useful for testing;
 * its fitness returns 1 / n, where n is the number of times it's been
 * calculated.
 */
public class DummyIndividual extends AbstractIndividual<DummyIndividual> {

  /**
   * The number of times the fitness has been calculated.
   */
  private double calcCount = 0.0;

  /**
   * CTOR.
   * @param c the chromosome.
   */
  public DummyIndividual(Chromosome c) {
    super(c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateImpl() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double calculateFitnessImpl() {
    calcCount++;
    return 1 / calcCount;
  }
}
