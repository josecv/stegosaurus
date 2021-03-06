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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A sensible default for a genetic algorithm; operates in a straightforward,
 * single-threaded manner.
 */
public class DefaultGeneticAlgorithm<T extends Individual<T>> extends
    GeneticAlgorithm<T> {
  /**
   * The population for the algorithm.
   */
  private List<Individual<T>> population;

  /**
   * Construct a new genetic algorithm instance.
   * 
   * @param factory the IndividualFactory that'll build Individuals.
   * @param selection the Selection operator to use for crossover selection.
   * @param random the random number generator to use.
   * @param params the GAParameters for this algorithm.
   * @see GAParameters
   */
  public DefaultGeneticAlgorithm(IndividualFactory<T> factory,
      SelectionOperator<T> selection, Random random, GAParameters params) {
    super(factory, selection, random, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init() {
    this.population = buildEmptyPopulation(popSize);
    for (int i = 0; i < popSize; i++) {
      population.add(buildIndividual());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<Individual<T>> getPopulation() {
    return population;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void sortPopulation(List<? extends Individual<T>> pop) {
    Collections.sort(pop);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateIndividual(int individual) {
    population.get(individual).simulate();
  }

  /**
   * {@inheritDoc}.
   * This particular variant does not do anything.
   */
  @Override
  protected void prepareGeneration(List<? extends Individual<T>> population) {

  }

  /**
   * Build a list of the size given, to be used for population; this method
   * should _only_ build the list: not actually populate it.
   * 
   * @param size the size of the eventual population
   * @return the empty list.
   */
  protected List<Individual<T>> buildEmptyPopulation(int size) {
    return new ArrayList<>(size);
  }
}
