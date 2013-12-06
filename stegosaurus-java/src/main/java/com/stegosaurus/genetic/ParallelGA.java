package com.stegosaurus.genetic;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.google.common.util.concurrent.ListeningExecutorService;


/**
 * Executes some of the operations in a GA in a concurrent manner.
 * <p>Currently, this only means fitness functions, which will all be
 * executed concurrently.</p>
 * <p>As such, for this to work, the individual in use MUST have a thread-safe
 * fitness function. Usually declaring it as synchronized is enough. In
 * addition, if the implementing individual does not cache its fitness
 * but instead recalculates it every time, using this class will do probably
 * do you no good.</p>
 */
public class ParallelGA<T extends Individual<T>>
  extends GeneticAlgorithm<T> {

  /**
   * The ExecutorService we're using for concurrently-run tasks.
   * Provided to us by a factory.
   */
  private ListeningExecutorService executorService;

  /**
   * The population.
   */
  private List<ParallelIndividual<T>> population;

  /**
   * Construct a new ParallelGeneticAlgorithm instance. Should be invoked
   * by a factory.
   * @param factory the IndividualFactory that'll build Individuals.
   * @param selection the Selection operator to use for crossover selection.
   * @param random the random number generator to use.
   * @param params the GAParameters for this algorithm.
   * @param executorService the ExecutorService to use for concurrent tasks.
   */
  ParallelGA(IndividualFactory<T> factory,
      SelectionOperator<T> selection, Random random,
      GAParameters params, ListeningExecutorService executorService) {
    super(factory, selection, random, params);
    this.executorService = executorService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void prepareGeneration(List<? extends Individual<T>> population) {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateIndividual(int index) {
    population.get(index).startFitnessCalculation(executorService);
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
  public void init() {
    population = new Vector<>(popSize);
    for(int i = 0; i < popSize; i++) {
      population.add(new ParallelIndividual<T>(buildIndividual()));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<? extends Individual<T>> getPopulation() {
    return population;
  }
}
