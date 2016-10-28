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

import java.util.Random;

/**
 * Builds GeneticAlgorithms.
 */
public interface GAFactory {

	/**
	 * Build a new GeneticAlgorithm instance.
	 * 
	 * @param factory the IndividualFactory that'll build Individuals.
	 * @param selection the Selection operator to use for crossover selection.
	 * @param random the random number generator to use.
	 * @param popSize the size of the population.
	 * @param chromosomeSize the number of genes in individuals' chromosomes.
	 * @param elitismRate the rate of population elites that should be left be.
	 * @param mutationRate the rate of mutation amongst individuals.
	 * @see DefaultGeneticAlgorithm
	 */
	<C extends Individual<C>> GeneticAlgorithm<C> build(
			IndividualFactory<C> factory, SelectionOperator<C> selection,
			Random random, GAParameters params);

}
