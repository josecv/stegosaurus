/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stegosaurus.genetic;

import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.DummyIndividual;
import com.stegosaurus.genetic.Individual;

/**
 * Tests the AbstractIndividual class, via the DummyIndividual.
 */
public class AbstractIndividualTest {
  /**
   * The individual under test.
   */
  private Individual<DummyIndividual> individual;

  /**
   * The individual's chromosome.
   */
  private Chromosome chromosome;

  /**
   * The random number generator in use.
   */
  private Random random;

  /**
   * The size of the chromosome.
   */
  private static final int CHROMOSOME_SIZE = 20;

  @Before
  public void setUp() {
    random = new Random();
    chromosome = new Chromosome(CHROMOSOME_SIZE, random);
    individual = new DummyIndividual(chromosome);
  }

  /**
   * Test the getChromosome method.
   */
  @Test
  public void testGetChromosome() {
    assertTrue(chromosome == individual.getChromosome());
  }

  /**
   * Test that calculateFitness throws if we haven't run a simulation.
   */
  @Test
  public void testCalculateFitnessBeforeSimulation() {
    try {
      individual.calculateFitness();
      fail("calculteFitness did not throw on being called before simulation");
    } catch(IllegalStateException e) { }
  }

  /**
   * Test that calculateFitness throws if we've just crossed over the
   * individual.
   */
  @Test
  public void testCalculateFitnessAfterCrossover() {
    Chromosome otherChromosome = new Chromosome(CHROMOSOME_SIZE, random);
    DummyIndividual other = new DummyIndividual(otherChromosome);
    other.simulate();
    individual.simulate().crossover(other);
    try {
      individual.calculateFitness();
      fail("calculateFitness did not throw after crossover");
    } catch(IllegalStateException e) { }
    try {
      other.calculateFitness();
      fail("calculateFitness did not throw after crossover");
    } catch(IllegalStateException e) { }
  }

  /**
   * Test the compareTo method.
   */
  @Test
  public void testCompareTo() {
    Chromosome otherChromosome = new Chromosome(CHROMOSOME_SIZE, random);
    DummyIndividual other = new DummyIndividual(otherChromosome);
    individual.simulate();
    other.simulate();
    assertEquals(0, individual.compareTo(other));
    assertEquals(0, other.compareTo(individual));
    other.mutate(0.5).simulate();
    assertTrue(other.compareTo(individual) < 0);
    assertTrue(individual.compareTo(other) > 0);
  }

  /**
   * Test that calculateFitness throws if we've just mutated this individual.
   */
  @Test
  public void testCalculateFitnessAfterMutation() {
    individual.simulate().mutate(0.5);
    try {
      individual.calculateFitness();
      fail("calculateFitness did not throw after mutation");
    } catch(IllegalStateException e) { }
  }

  /**
   * Test that calculate fitness works under valid circumnstances.
   */
  @Test
  public void testCalculateFitness() {
    double fitness = individual.simulate().calculateFitness();
    assertEquals(fitness, 1.0, 0.02);
  }

  /**
   * Test that the result of calculateFitness is stored by the
   * AbstractIndividual and only recalculated when strictly necessary.
   */
  @Test
  public void testFitnessRecalculation() {
    for(int j = 1; j < 4; j++)  {
      individual.mutate(0.5).simulate();
      for(int i = 0; i < 3; i++) {
        assertEquals("Fitness not recalculated",
          1.0 / j, individual.calculateFitness(), 0.02);
      }
    }
  }

  /**
   * Ensure that the individual will not run a simulation unless needed.
   */
  @Test
  public void testPreventUnwarrantedSimulation() {
    individual.simulate();
    /* We know from other tests that simulate() will ordinarily force a
     * fitness recalculation (eg from testCalculateFitness()), so we merely
     * need to verify that none has ocurred to confirm that there has been
     * no simulation.
     */
    for(int i = 0; i < 3; i++) {
      individual.simulate();
      assertEquals(1.0, individual.calculateFitness(), 0.02);
    }
  }
}
