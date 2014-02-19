package com.stegosaurus.genetic;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Any genetic algorithm: attempts to find the optimal solution to a problem
 * using a rip-off of natural selection.
 * <p>
 * Makes use of some specific parameters, importantly the elitism and mutation
 * rates, explained below.
 * </p>
 * <p>
 * The specific algorithm implemented by this class uses elitism, which is to
 * say that the fittest members of the population are not crossed over with
 * other members, and do not suffer mutation in any given generation. Note that
 * no non-elite individuals will be allowed to survive; i.e. the crossover rate
 * for non-elite individuals is 1.0.
 * </p>
 * 
 * <p>
 * The elitism rate is the rate of elites to general population, where the
 * elites are those members that will not be crossed over or mutated. Thus, if
 * the elitism rate is of 1/3, the best third of the population will not produce
 * offspring and will not be genetically altered.
 * </p>
 * 
 * <p>
 * The mutation rate is the rate of mutant genes to chromosome size, for any
 * particular individual, at the end of any given generation. It can also be
 * conceptualized as the probability that any given gene in a chromosome will be
 * mutated.
 * </p>
 * 
 * @param <T> the <em>Individual</em> type used in this particular algorithm.
 */
public abstract class GeneticAlgorithm<T extends Individual<T>> {

  /**
   * The population size.
   */
  protected final int popSize;

  /**
   * The elitism rate.
   */
  protected final double elitismRate;

  /**
   * The mutation rate.
   */
  protected final double mutationRate;
  /**
   * The number of genes in any given individual's chromosome.
   */
  protected final int chromosomeSize;

  /**
   * The number of individuals that will be classed as elites each generation.
   */
  protected final int elites;

  /**
   * The random number generator.
   */
  protected Random random;
  /**
   * The IndividualFactory we'll use to build invdividuals.
   */
  protected IndividualFactory<T> factory;

  /**
   * The selection operator we'll use.
   */
  protected SelectionOperator<T> selection;

  /**
   * Construct a new Genetic Algorithm.
   * @param factory the IndividualFactory that'll build Individuals.
   * @param selection the Selection operator to use for crossover selection.
   * @param random the random number generator to use.
   * @param params the GAParameters for this algorithm.
   * @see GAParameters
   */
  public GeneticAlgorithm(IndividualFactory<T> factory,
      SelectionOperator<T> selection, Random random, GAParameters params) {
    this.popSize = params.getPopSize();
    this.elitismRate = params.getElitismRate();
    this.mutationRate = params.getMutationRate();
    this.chromosomeSize = params.getChromosomeSize();
    this.factory = factory;
    this.selection = selection;
    this.random = random;
    int elitesTmp = (int) Math.floor(popSize * elitismRate);
    /*
     * We need to have an even amount of non-elites, for obvious
     * reproductive reasons. In addition, the population size is guaranteed
     * to be even. Thus, if we have an odd amount of elites, we have an odd
     * amount of non-elites, and we need to correct that.
     */
    if (elitesTmp % 2 != 0) {
      elitesTmp--;
    }
    this.elites = elitesTmp;
    if (popSize % 2 != 0) {
      throw new IllegalArgumentException("Population size must be even");
    }
  }

  /**
   * Run this algorithm, until one of the individuals' fitness is less than a
   * given threshold.
   *
   * @param threshold the threshold.
   * @return the fittest individual.
   */
  public Individual<T> runWithThreshold(double threshold) {
    double best;
    List<? extends Individual<T>> population = getPopulation();
    int i = 0;
    do {
      nextGeneration(i);
      runGeneration();
      best = population.get(0).calculateFitness();
      i++;
    } while (best > threshold);
    return population.get(0);
  }

  /**
   * Run this algorithm for a fixed number of generations; return the fittest
   * individual produced.
   *
   * @param n the number of generations.
   * @return the fittest individual.
   */
  public Individual<T> runNGenerations(int n) {
    for (int i = 0; i < n; i++) {
      nextGeneration(i);
      runGeneration();
    }
    return getPopulation().get(0);
  }

  /**
   * Construct a new individual, with a randomized chromosome.
   * @return the new individual.
   */
  protected Individual<T> buildIndividual() {
    Chromosome c = new Chromosome(chromosomeSize, random);
    c.randomize();
    return factory.build(c);
  }

  /**
   * Run any tasks that must be executed before a generation can be simulated
   * and sorted.
   *
   * @param population the population.
   */
  protected abstract void
  prepareGeneration(List<? extends Individual<T>> population);

  /**
   * Run a simulation on the individual given.
   * @param individual the index of the individual to run the simulation on.
   */
  protected abstract void simulateIndividual(int individual);

  /**
   * Sort the population given by fitness value. This implies actually
   * calculating the fitness for the entire population, unless it has already
   * been obtained, something this class does not otherwise do explicitly.
   *
   * @param pop the population to sort.
   */
  protected abstract void sortPopulation(List<? extends Individual<T>> pop);

  /**
   * Get the population for this genetic algorithm.
   *
   * @return the population
   */
  protected abstract List<? extends Individual<T>> getPopulation();

  /**
   * Initialize this object, by constructing a bunch of individuals with
   * corresponding random chromosomes.
   */
  public abstract void init();

  /**
   * Run a generation of this algorithm: run the simulation, and sort by
   * fitness value.
   */
  private void runGeneration() {
    List<? extends Individual<T>> population = getPopulation();
    prepareGeneration(population);
    for(int i = 0; i < population.size(); i++) {
      simulateIndividual(i);
    }
    sortPopulation(population);
  }

  /**
   * Generate the next generation, unless current is 0, in which case we have
   * no information whatsoever to do so, and so nothing is done.
   * 
   * @param current the index of the current generation (from 0).
   */
  private void nextGeneration(int current) {
    if (current == 0) {
      return;
    }
    List<? extends Individual<T>> population = getPopulation();
    int i = elites;
    final int popSize = population.size();
    while(i < popSize) {
      int firstIndex = selection.select(population.subList(i,
        population.size()), random) + i;
      Collections.swap(population, i, firstIndex);
      int secondIndex =
        selection.select(population.subList(i + 1, population.size()),
                         random) + i + 1;
      Collections.swap(population, i + 1, secondIndex);
      population.get(i).crossover(population.get(i + 1));
      population.get(i).mutate(mutationRate);
      population.get(i + 1).mutate(mutationRate);
      i += 2;
    }
  }
}
