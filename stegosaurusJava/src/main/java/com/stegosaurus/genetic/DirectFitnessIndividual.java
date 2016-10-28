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
 * A more sophisticated dummy individual whose fitness is a floating point
 * representation of its chromosome.
 * Useful for complex tests that need to be able to differentiate between
 * good and bad solutions in a meaningful way.
 * Its Chromosomes should be 64 genes long, so as to make them easily
 * convertible to doubles.
 * Note however that the fitness is not necessarily equal to the
 * chromosome's asDouble() value, given that the former must be between
 * 0 and 1, and the latter has no such constraint.
 * A value of NaN or infinity for the chromosome is considered unfit as
 * hell, and so produces a fitness of 1.0.
 * The precise algorithm for the fitness function is:
 * <code>
 *    a = abs(chromosome.asDouble())
 *    if a is NaN or a is Infinity:
 *      return 1.0
 *    if a &lt;= 1
 *      return a
 *    else
 *      log = floor(log10(a))
 *      return (a / (10 ^ log)) / 10
 * </code>
 * This algorithm lumps the majority of values around .1 to .9, but allows for
 * some very tiny values.
 */
public class DirectFitnessIndividual
  extends AbstractIndividual<DirectFitnessIndividual> {

  /**
   * CTOR.
   * @param chromosome the chromosome for this individual.
   */
  public DirectFitnessIndividual(Chromosome chromosome) {
    super(chromosome);
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
    double retval = Math.abs(chromosome.asDouble());
    if(Double.isNaN(retval) || Double.isInfinite(retval)) {
      return 1.0;
    }
    if(retval <= 1) {
      return retval;
    }
    int log = (int) Math.floor(Math.log10(retval));
    return (retval / Math.pow(10, log)) / 10;
  }
}
