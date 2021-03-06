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
 * An individual in a genetic algorithm.
 * <p>Possesses a single chromosome, is capable of crossing over with
 * another individual and being mutated.
 * Also has a fitness function that may be computed.</p>
 * <p>Note that in this case, we do not produce more individuals when crossing
 * over. Instead, the parents become the children after the crossover
 * operation.</p>
 * <p>It may not be immediately possible to derive an individual's fitness
 * from the chromosome; as such, the general workflow should be to construct
 * the individual, call the simulate() method, and then calculate its
 * fitness.</p>
 *
 * <p>The idea with the template parameter here is that it should be set to the
 * actual implementing class. Thus, if class A implements individual, it
 * should be declared as "A implements Individual &gt;A&lt;". This is to
 * ensure that individuals of one type can only be crossed over with those
 * of that same type.
 * This should allow declaration of some variables as Individual&gt;?&lt;
 * provided crossover isn't called on them.</p>
 *
 * <p>It is possible to compare an individual to another individual of the
 * same type. For such a comparison, their fitness values are compared. Thus,
 * if individual A is fitter than individual B, A is less than B.
 * NOTE: This ordering is not necessarily consistent with the equals()
 * method.</p>
 */
public interface Individual<T extends Individual<T>>
  extends Comparable<Individual<T>> {
  /**
   * Run the simulation corresponding to this individual.
   * Evidently, the precise nature of this is incredibly variable, but it
   * should generally be assumed to be an expensive operation.
   * @return this same individual.
   */
  Individual<T> simulate();

  /**
   * Calculate and return the fitness of this individual.
   * Should be a double between 0 and 1, where 0 is perfect and 1 is useless.
   * It is <em>strongly</em> recommended that implementing classes cache this
   * value and only recalculate it when strictly necesssary.
   * @return the fitness.
   */
  double calculateFitness();

  /**
   * Cross this individual over with the one given.
   * @param other the individual to cross over with.
   */
  void crossover(Individual<T> other);

  /**
   * Get this individual's chromosome.
   * @return the chromosome.
   */
  Chromosome getChromosome();

  /**
   * Randomly mutate this individual's chromosome, according to a given rate.
   * Thus, if the rate is 1/3, a third of this individual's genes will
   * be altered.
   * @param rate the rate of mutation.
   * @return this same individual.
   */
  Individual<T> mutate(double rate);
}
