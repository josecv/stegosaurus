package com.stegosaurus.steganographers.genetic;

import static org.junit.Assume.assumeNoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the GeneticAlgorithm class.
 */
public class GeneticAlgorithmTest {
  /**
   * The seed for the pseudo random number generator.
   */
  private final static long SEED = 8909821348789L;

  /**
   * The prng.
   */
  private Random random;

  /**
   * The algorithm.
   */
  private GeneticAlgorithm<DirectFitnessIndividual> algo;

  /**
   * The size of the population for our genetic algorithm.
   */
  private static final int POP_SIZE = 10;

  /**
   * The size of the chromosomes used by the genetic algorithm.
   * Equal to the size of a double.
   */
  private static final int CHROMOSOME_SIZE = Double.SIZE; 

  /**
   * The elitism rate to use for the algorithm.
   */
  private static final double ELITISM_RATE = 0.5;

  /**
   * The mutation rate for the algorithm.
   */
  private static final double MUTATION_RATE = 0.1;

  /**
   * The factor for the rank selection.
   */
  private static final double FACTOR = 20.0;

  /**
   * The factory for individual instances.
   */
  private IndividualFactory<DirectFitnessIndividual> fact;

  /**
   * An individual that acts as a function of the direct fitness individual so
   * that its fitness is the distance from a constant, 0.5, to the fitness
   * of the direct fitness individual with the corresponding chromosome.
   * If that doesn't make sense, the code should hopefully be explanatory.
   */
  private static class DeltaIndividual extends DirectFitnessIndividual {

    /**
     * The constant to go towards.
     */
    private static double n = 0.5;

    /**
     * CTOR.
     * @param c the chromosome.
     * @param n the constant to go towards.
     */
    public DeltaIndividual(Chromosome c) {
      super(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double calculateFitnessImpl() {
      return Math.abs(n - super.calculateFitnessImpl());
    }
  }

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    random = new Random(SEED);
    SelectionOperator<DirectFitnessIndividual> s = new RankSelection<>(FACTOR);
    try {
      fact =
        new GenericIndividualFactory<DirectFitnessIndividual>(DeltaIndividual.class);
    } catch(NoSuchMethodException e) {
      assumeNoException(e);
    }
    algo = new GeneticAlgorithm<>(fact, s, random,
                                  POP_SIZE, CHROMOSOME_SIZE, ELITISM_RATE,
                                  MUTATION_RATE);

  }

  /**
   * Run a single generation of the genetic algorithm. Thus, there will be no
   * crossover and no mutation. Basically, then, whichever chromosome is the
   * fittest on construction should be produced by the algorithm.
   */
  @Test
  public void testSingleGeneration() {
    algo.init();
    /* By reseeding the prng, we can ensure that we'll get our hands on the
     * exact chromosomes that the GeneticAlgorithm instance has, after which
     * it's just a matter of figuring out which one's the fittest. */
    random.setSeed(SEED);
    List<DirectFitnessIndividual> pop = new ArrayList<>(POP_SIZE);
    for(int i = 0; i < POP_SIZE; i++) {
      Chromosome c = new Chromosome(CHROMOSOME_SIZE, random).randomize();
      DirectFitnessIndividual ind = fact.build(c);
      ind.simulate();
      pop.add(ind);
    }
    /* Cf. "figuring out which one's the fittest" */
    Collections.sort(pop);
    Individual<DirectFitnessIndividual> expected = pop.get(0);
    Individual<DirectFitnessIndividual> result = algo.runNGenerations(1);
    assertEquals(result.getChromosome(), expected.getChromosome());
  }

  /**
   * Test running the genetic algorithm until fitness is at most equal to
   * a given threshold.
   */
  @Test
  public void testWithThreshold() {
    /* This threshold is pretty precise, and takes 603 generations to compute
     * on my machine.
     * You might find it kind of obvious that making this threshold an order
     * of magnitude smaller (i.e. 0.00001) makes the test take 10 times as
     * long to complete.
     * Interestingly, the number of generations required doesn't seem to follow
     * such a linear progression, and the more precise number takes around
     * 25745 generations to be computed. This is probably just a curiosity of
     * this particular fitness function, and not a generalizable property.
     * At any rate, the 0.00001 threshold makes the test take like a third
     * of a second, which is really not cool, and so we leave it at 0.0001
     */
    final double threshold = 0.0001;
    algo.init();
    Individual<DirectFitnessIndividual> expected =
      algo.runWithThreshold(threshold);
    assertTrue(expected.calculateFitness() <= threshold);
  }
}
