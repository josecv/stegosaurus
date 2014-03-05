package com.stegosaurus.genetic;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.DummyIndividual;
import com.stegosaurus.genetic.Individual;
import com.stegosaurus.genetic.RankSelection;
import com.stegosaurus.genetic.SelectionOperator;

/**
 * Test the Rank Selection class.
 */
public class RankSelectionTest {

  /**
   * The random number generator.
   */
  private Random random;

  /**
   * The operator.
   */
  private SelectionOperator<DummyIndividual> selector;

  /**
   * The size of the population.
   */
  private static final int popSize = 5;

  /**
   * The size of the chromosomes in the individuals.
   */
  private static final int chromosomeSize = 10;

  /**
   * The population.
   */
  private List<Individual<DummyIndividual>> population;

  /**
   * The factor for the operator.
   */
  private static final double factor = 20.0;

  /**
   * Set up the test.
   */
  @Before
  public void setUp() {
    random = new Random();
    selector = new RankSelection<>(factor);
    population = new ArrayList<>(popSize);
    for(int i = 0; i < popSize; i++) {
      Chromosome c = new Chromosome(chromosomeSize, random);
      population.add(new DummyIndividual(c).simulate());
    }
  }

  /**
   * Select from the population a bunch of times, and ensure that the long
   * term relative frequency is preserved (ie that higher ranking members
   * of the population get selected more often than lower ranking ones, as
   * per the probability distribution implemented).
   */
  @Test
  public void testLongTermRelativeFrequency() {
    /* We know the probability of selection for all individuals, so
     * we'll keep that in a map, and we'll count the actual selections in
     * another map, then compare.
     */
    TObjectDoubleMap<Individual<DummyIndividual>> expected = 
      new TObjectDoubleHashMap<>(popSize);
    TObjectIntMap<Individual<DummyIndividual>> counts =
      new TObjectIntHashMap<>(popSize);
    /* The following are known, pre-calculated, values that've been rounded
     * to add up to 1. */
    double[] values = { 0.381, 0.290, 0.200, 0.110, 0.019 };
    for(int i = 0; i < popSize; i++) {
      expected.put(population.get(i), values[i]);
      counts.put(population.get(i), 0);
    }
    final int runs = 10000;
    for(int i = 0; i < runs; i++) {
      Individual<DummyIndividual> selected =
        population.get(selector.select(population, random));
      counts.increment(selected);
    }
    for(int i = 0; i < popSize; i++) {
      Individual<DummyIndividual> individual = population.get(i);
      double frequency = ((double) counts.get(individual)) / runs;
      assertEquals("Unexpected frequency.", values[i], frequency, 0.15);
    }
  }
}
