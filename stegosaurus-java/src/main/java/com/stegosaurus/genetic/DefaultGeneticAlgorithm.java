package com.stegosaurus.genetic;

import java.util.ArrayList;
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
  protected void sortPopulation(List<Individual<T>> pop) {
    Collections.sort(pop);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateIndividual(Individual<T> individual) {
    individual.simulate();
  }

  /**
   * {@inheritDoc}.
   * This particular variant does not do anything.
   */
  @Override
  protected void prepareGeneration(List<Individual<T>> population) {

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
