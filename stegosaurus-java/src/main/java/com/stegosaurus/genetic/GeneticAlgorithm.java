package com.stegosaurus.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GeneticAlgorithm<T extends Individual<T>> {

  /**
   * The population size.
   */
  protected final int popSize;

  /**
   * Run any tasks that must be executed before a generation can be simulated
   * and sorted.
   * 
   * @param population the population.
   */
  protected abstract void prepareGeneration(List<Individual<T>> population);

  /**
   * Run a simulation on the individual given.
   * 
   * @param individual the individual to run the simulation on.
   */
  protected abstract void simulateIndividual(Individual<T> individual);

  /**
   * Sort the population given by fitness value. This implies actually
   * calculating the fitness for the entire population, unless it has already
   * been obtained, something this class does not otherwise do explicitly.
   * 
   * @param pop the population to sort.
   */
  protected abstract void sortPopulation(List<Individual<T>> pop);

  /**
   * Get the population for this genetic algorithm.
   * 
   * @return the population
   */
  protected abstract List<Individual<T>> getPopulation();

  /**
   * Initialize this object, by constructing a bunch of individuals with
   * corresponding random chromosomes.
   */
  public abstract void init();

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

  public GeneticAlgorithm(IndividualFactory<T> factory,
      SelectionOperator<T> selection, Random random, GAParameters params) {
    this.popSize = params.getPopSize();
    this.elitismRate = params.getElitismRate();
    this.mutationRate = params.getMutationRate();
    this.chromosomeSize = params.getChromosomeSize();
    this.factory = factory;
    this.selection = selection;
    this.random = random;
    if (popSize % 2 != 0) {
      throw new IllegalArgumentException("Population size must be even");
    }
  }

  /**
   * Run this algorithm, until one of the individuals' fitness is less than a
   * given threshold.
   * 
   * @param threshold
   *            the threshold.
   * @return the fittest individual.
   */
  public Individual<T> runWithThreshold(double threshold) {
    double best;
    List<Individual<T>> population = getPopulation();
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
   * @param n
   *            the number of generations.
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
   * Run a generation of this algorithm: run the simulation, and sort by
   * fitness value.
   */
  private void runGeneration() {
    List<Individual<T>> population = getPopulation();
    prepareGeneration(population);
    for (Individual<T> individual : population) {
      simulateIndividual(individual);
    }
    sortPopulation(population);
  }

  /**
   * Generate the next generation, unless current is 0, in which case we have
   * no information whatsoever to do so, and so nothing is done.
   * 
   * @param current
   *            the index of the current generation (from 0).
   */
  private void nextGeneration(int current) {
    if (current == 0) {
      return;
    }
    List<Individual<T>> population = getPopulation();
    int elites = (int) Math.floor(popSize * elitismRate);
    /*
     * We need to have an even amount of non-elites, for obvious
     * reproductive reasons. In addition, the population size is guaranteed
     * to be even. Thus, if we have an odd amount of elites, we have an odd
     * amount of non-elites.
     */
    if (elites % 2 != 0) {
      elites--;
    }
    /* XXX This looks like it's slow as all hells. */
    List<Individual<T>> nonElites = new ArrayList<>(population.subList(
        elites, population.size()));
    population = new ArrayList<>(population.subList(0, elites));
    while (nonElites.size() > 0) {
      Individual<T> first = selection.select(nonElites, random);
      Individual<T> second = selection.select(nonElites, random);
      first.crossover(second);
      first.mutate(mutationRate);
      second.mutate(mutationRate);
      population.add(first);
      population.add(second);
    }
  }

}
