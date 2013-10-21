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
   * @see GeneticAlgorithm for a discussion of what this is.
   */
  public double getElitismRate() {
    return elitismRate;
  }

  /**
   * Get the mutation rate.
   * @return the mutation rate
   * @see GeneticAlgorithm for a discussion of what this is.
   */
  public double getMutationRate() {
    return mutationRate;
  }
}
