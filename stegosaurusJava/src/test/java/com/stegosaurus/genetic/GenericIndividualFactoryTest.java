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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

import com.stegosaurus.genetic.Chromosome;
import com.stegosaurus.genetic.DummyIndividual;
import com.stegosaurus.genetic.GenericIndividualFactory;

/**
 * Tests the GenericIndividualFactory class.
 */
public class GenericIndividualFactoryTest {

  /**
   * The random number generator.
   */
  private Random random = new Random();

  /**
   * The size of the chromosomes in use.
   */
  private static final int CHROMOSOME_SIZE = 10;

  /**
   * Test the build() method.
   */
  @Test
  public void testBuild() {
    Chromosome chr = new Chromosome(CHROMOSOME_SIZE, random);
    chr.randomize();
    try {
      GenericIndividualFactory<DummyIndividual> factory =
        new GenericIndividualFactory<>(DummyIndividual.class);
      DummyIndividual individual = factory.build(chr);
      assertEquals("Built individual contains wrong chromosome", chr,
        individual.getChromosome());
    } catch(NoSuchMethodException e) {
      fail("Unexpected exception " + e.getMessage());
    }
  }
}
