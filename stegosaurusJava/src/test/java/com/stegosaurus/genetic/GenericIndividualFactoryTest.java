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
