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
 * A data structure that encapsulates the parameters required to execute a
 * genetic algorithm.
 * These include stuff like the size of the population, or its elitism rate.
 * Note that nothing here is directly related to the type of individuals
 * manipulated by a GA, so that you can re-use these structures for
 * differently typed GAs.
 */
public class GAParameters {
  /**
   * The size of the population.
   */
  private int popSize;

  /**
   * The number of genes in a chromosome.
   */
  private int chromosomeSize;

  /**
   * The elitism rate in the population.
   */
  private double elitismRate;

  /**
   * The mutation rate for this population.
   */
  private double mutationRate;

  /**
   * Construct a GAParameters data structure.
   * @param popSize the size of the population.
   * @param chromosomeSize the number of genes in individuals' chromosomes.
   * @param elitismRate the rate of population elites that should be left be.
   * @param mutationRate the rate of mutation amongst individuals.
   */
  public GAParameters(int popSize, int chromosomeSize, double elitismRate,
      double mutationRate) {
    this.popSize = popSize;
    this.chromosomeSize = chromosomeSize;
    this.elitismRate = elitismRate;
    this.mutationRate = mutationRate;
  }

  /**
   * Get the size of the population.
   * @return the population size
   */
  public int getPopSize() {
    return popSize;
  }

  /**
   * Get the number of genes in a chromosome.
   * @return the chromosome size
   */
  public int getChromosomeSize() {
    return chromosomeSize;
  }

  /**
   * Get the elitism rate.
   * @return the elitism rate
   * @see DefaultGeneticAlgorithm for a discussion of what this is.
   */
  public double getElitismRate() {
    return elitismRate;
  }

  /**
   * Get the mutation rate.
   * @return the mutation rate
   * @see DefaultGeneticAlgorithm for a discussion of what this is.
   */
  public double getMutationRate() {
    return mutationRate;
  }
}
