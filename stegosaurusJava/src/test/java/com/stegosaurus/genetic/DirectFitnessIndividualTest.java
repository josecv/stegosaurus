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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.DirectFitnessIndividual;
import com.stegosaurus.genetic.Individual;

/**
 * Test the directFitnessIndividual class.
 */
public class DirectFitnessIndividualTest {
  /**
   * The PRNG.
   */
  private Random random = new Random();

  /**
   * The size of the chromosomes.
   */
  private static final int CHROMOSOME_SIZE = Double.SIZE;

  /**
   * The size of the population.
   */
  private static final int POPULATION_SIZE = 10000;

  /**
   * The population itself.
   */
  private List<Individual<DirectFitnessIndividual>> population;

  /**
   * Set up the test by building the population with some randomized
   * chromosomes.
   */
  @Before
  public void setUp() {
    population = new ArrayList<>(POPULATION_SIZE);
    for(int i = 0; i < POPULATION_SIZE; i++) {
      Chromosome c = new Chromosome(CHROMOSOME_SIZE, random).randomize();
      population.add(new DirectFitnessIndividual(c).simulate());
    }
  }

  /**
   * Test that the algorithm in use corresponds to the one outlined in the
   * documentation.
   * We obviously can't _prove_ this analitically from here, so this test
   * ensures that performance within a population is consistent with what
   * the documentation specifies.
   * Seems a bit like a crazy test, but that javadoc is specific enough that
   * this is needed.
   */
  @Test
  public void testDocumentationAlgoRespected() {
    for(Individual<DirectFitnessIndividual> individual : population) {
      /* Evidently, the following is taken pretty much verbatim from the
       * javadoc for DirectFitnessIndividual. */
      Chromosome c = individual.getChromosome();
      double expected = Math.abs(c.asDouble());
      if(Double.isNaN(expected) || Double.isInfinite(expected)) {
        expected = 1.0;
      }
      if(expected > 1) {
        int log = (int) Math.floor(Math.log10(expected));
        expected = (expected / Math.pow(10, log)) / 10;
      }
      double result = individual.calculateFitness();
      assertEquals("calculateFitness inconsistent with documentated behaviour",
        expected, result, 0.05);
    }
  }

  /**
   * Test that the calculateFitness() produces a number between 0 and 1.
   * Whereas the testDocumentationAlgoRespected() method checks that the
   * algorithm is well implemented, this test instead checks that the math
   * itself is valid.
   * This can probably be proven mathematically, but it sure doesn't hurt
   * to have the test.
   */
  @Test
  public void testFitnessInValidRange() {
    for(Individual<DirectFitnessIndividual> individual : population) {
      double val = individual.calculateFitness();
      assertTrue("fitness < 0 : " + val, individual.calculateFitness() >= 0);
      assertTrue("fitness > 1 : " + val, individual.calculateFitness() <= 1);
    }
  }
}
