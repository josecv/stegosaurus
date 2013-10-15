package com.stegosaurus.steganographers.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Any genetic algorithm that can be used for some sort of steganography.
 * The specific algorithm implemented by this class uses elitism, which is to
 * say that the fittest members of the population are not crossed over with
 * other members, and do not suffer mutation in any given generation.
 * Note that no non-elite individuals will be allowed to survive; i.e.
 * the crossover rate for non-elite individuals is 1.0.
 *
 * The elitism rate is the rate of elites to general population, where the
 * elites are those members that will not be crossed over or mutated. Thus,
 * if the elitism rate is of 1/3, the best third of the population will not
 * produce offspring and will not be genetically altered.
 *
 * @param <T> the Individual implementation for this particular algorithm.
 */
public class GeneticAlgorithm<T extends Individual<T>> {
  /**
   * The population size.
   */
  private final int popSize;

  /**
   * The elitism rate.
   */
  private final double elitismRate;

  /**
   * The mutation rate.
   */
  private final double mutationRate;

  /**
   * The number of genes in any given individual's chromosome.
   */
  private final int chromosomeSize;

  /**
   * The random number generator.
   */
  private Random random;

  /**
   * The population for the algorithm.
   */
  private List<Individual<T>> population;

  /**
   * The IndividualFactory we'll use to build invdividuals.
   */
  private IndividualFactory<T> factory;

  /**
   * The selection operator we'll use.
   */
  private SelectionOperator<T> selection;

  /**
   * Construct a new genetic algorithm instance.
   * @param factory the IndividualFactory that'll build Individuals.
   * @param selection the Selection operator to use for crossover selection.
   * @param random the random number generator to use.
   * @param popSize the size of the population.
   * @param chromosomeSize the number of genes in individuals' chromosomes.
   * @param elitismRate the rate of population elites that should be left be.
   * @param mutationRate the rate of mutation amongst individuals.
   */
  public GeneticAlgorithm(IndividualFactory<T> factory,
                          SelectionOperator<T> selection, Random random,
                          int popSize, int chromosomeSize, double elitismRate,
                          double mutationRate) {
    if(popSize % 2 != 0) {
      throw new IllegalArgumentException("Population size must be even");
    }
    this.factory = factory;
    this.selection = selection;
    this.random = random;
    this.popSize = popSize;
    this.elitismRate = elitismRate;
    this.mutationRate = mutationRate;
    this.population = new ArrayList<>(popSize);
    this.chromosomeSize = chromosomeSize;
  }

  /**
   * Initialize this object, by constructing a bunch of individuals with
   * corresponding random chromosomes.
   */
  public void init() {
    for(int i = 0; i < popSize; i++) {
      Chromosome c = new Chromosome(chromosomeSize, random);
      c.randomize();
      population.add(factory.build(c));
    }
  }

  /**
   * Run a generation of this algorithm: run the simulation, and sort
   * by fitness value.
   */
  private void runGeneration() {
    for(Individual<T> individual : population) {
      individual.simulate();
    }
    Collections.sort(population);
  }

  /**
   * Generate the next generation, unless current is 0, in which case we
   * have no information whatsoever to do so, and so nothing is done.
   * @param current the index of the current generation (from 0).
   */
  private void nextGeneration(int current) {
    if(current == 0) {
      return;
    }
    int elites = (int) Math.floor(popSize * elitismRate);
    /* We need to have an even amount of non-elites, for obvious reproductive
     * reasons.
     * In addition, the population size is guaranteed to be even.
     * Thus, if we have an odd amount of elites, we have an odd amount of
     * non-elites.
     */
    if(elites % 2 != 0) {
      elites--;
    }
    /* XXX This looks like it's slow as all hells. */
    List<Individual<T>> nonElites =
      new ArrayList<>(population.subList(elites, population.size()));
    population = new ArrayList<>(population.subList(0, elites));
    while(nonElites.size() > 0) {
      Individual<T> first = selection.select(nonElites, random);
      Individual<T> second = selection.select(nonElites, random);
      first.crossover(second);
      first.mutate(mutationRate);
      second.mutate(mutationRate);
      population.add(first);
      population.add(second);
    }
  }

  /**
   * Run this algorithm, until one of the individuals' fitness is less
   * than a given threshold.
   * @param threshold the threshold.
   * @return the fittest individual.
   */
  public Individual<T> runWithThreshold(double threshold) {
    double best;
    int i = 0;
    do {
      nextGeneration(i);
      runGeneration();
      best = population.get(0).calculateFitness();
      i++;
    } while(best > threshold);
    return population.get(0);
  }

  /**
   * Run this algorithm for a fixed number of generations; return the fittest
   * individual produced.
   * @param n the number of generations.
   * @return the fittest individual.
   */
  public Individual<T> runNGenerations(int n) {
    for(int i = 0; i < n; i++) {
      nextGeneration(i);
      runGeneration();
    }
    return population.get(0);
  }
}