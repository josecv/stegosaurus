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
	 * @see GeneticAlgorithm
	 */
	<C extends Individual<C>> GeneticAlgorithm<C> build(
			IndividualFactory<C> factory, SelectionOperator<C> selection,
			Random random, GAParameters params);

}
