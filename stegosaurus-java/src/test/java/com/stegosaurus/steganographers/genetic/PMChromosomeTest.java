package com.stegosaurus.steganographers.genetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the PMChromosome class.
 */
public class PMChromosomeTest {

  /**
   * The random object to use.
   */
  private Random random;

  /**
   * The seed for the random number generator.
   */
  private static final int SEED = 0xDEADBEEF;

  /**
   * The default size of the chromosomes.
   */
  private static final int SIZE = 256;

  /**
   * Set up the test.
   * Construct the random number generator, and seed it with the SEED
   * constant.
   */
  @Before
  public void setUp() {
    random = new Random(SEED);
  }

  /**
   * Test the crossover method with a specific index.
   */
  @Test
  public void testCrossover() {
    int i;
    final int index = 75;
    PMChromosome[] chromosomes = {
      new PMChromosome(SIZE, random).randomize(),
      new PMChromosome(SIZE, random).randomize()
    };
    /* By reseeding this thing, we can make sure that the expected[]
     * chromosomes are identical to the previously generated ones.
     */
    random.setSeed(SEED);
    PMChromosome[] expected = {
      new PMChromosome(SIZE, random).randomize(),
      new PMChromosome(SIZE, random).randomize()
    };
    PMChromosome.crossover(chromosomes[0], chromosomes[1], index);
    String msg = "Bad value for chromosome %d at index %d";
    for(i = 0; i < index; i++) {
      assertEquals(String.format(msg, 0, i),
        chromosomes[0].atIndex(i), expected[0].atIndex(i));
      assertEquals(String.format(msg, 1, i),
        chromosomes[1].atIndex(i), expected[1].atIndex(i));
    }
    for(i = index; i < SIZE; i++) {
      assertEquals(String.format(msg, 0, i),
        chromosomes[0].atIndex(i), expected[1].atIndex(i));
      assertEquals(String.format(msg, 1, i),
        chromosomes[1].atIndex(i), expected[0].atIndex(i));
    }
  }

  /**
   * Test the crossover method when the chromosomes are of unequal length.
   */
  @Test
  public void testCrossoverUnequalChromosomeLengths() {
    PMChromosome first = new PMChromosome(SIZE, random);
    PMChromosome second = new PMChromosome((SIZE * 2) - 7, random);
    try {
      PMChromosome.crossover(first, second, 22);
      fail("Crossover with unequally sized chromosomes did not throw");
    } catch(IllegalArgumentException e) { }
  }

  /**
   * Test the crossover method when the index given is out of bounds
   * for the chromosomes.
   */
  @Test
  public void testCrossoverIndexOutOfBounds() {
    PMChromosome first = new PMChromosome(SIZE, random);
    PMChromosome second = new PMChromosome(SIZE, random);
    try {
      PMChromosome.crossover(first, second, SIZE * 2 - 17);
      fail("Crossover with bad index did not throw");
    } catch(IndexOutOfBoundsException e) { }
  }
}
