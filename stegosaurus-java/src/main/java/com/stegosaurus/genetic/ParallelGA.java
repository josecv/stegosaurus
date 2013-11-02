package com.stegosaurus.genetic;

import java.util.Random;

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
  protected void simulateIndividual(final Individual<T> individual) {
    individual.simulate();
    executorService.submit(new Runnable() {
      public void run() {
        individual.calculateFitness();
      }
    });
  }
}
