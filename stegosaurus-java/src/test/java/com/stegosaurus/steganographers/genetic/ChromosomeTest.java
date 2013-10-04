package com.stegosaurus.steganographers.genetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the Chromosome class.
 */
public class ChromosomeTest {

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
    Chromosome[] chromosomes = {
      new Chromosome(SIZE, random).randomize(),
      new Chromosome(SIZE, random).randomize()
    };
    /* By reseeding this thing, we can make sure that the expected[]
     * chromosomes are identical to the previously generated ones.
     */
    random.setSeed(SEED);
    Chromosome[] expected = {
      new Chromosome(SIZE, random).randomize(),
      new Chromosome(SIZE, random).randomize()
    };
    Chromosome.crossover(chromosomes[0], chromosomes[1], index);
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
   * Test the crossover method with a random index.
   * We can't really know what's going to happen, so we'll just test to make
   * sure that the changed chromosomes are different from what they were
   * before.
   */
  @Test
  public void testRandomCrossover() {
    /* The set up is nearly identical to testCrossover's */
    Chromosome[] chromosomes = {
      new Chromosome(SIZE, random).randomize(),
      new Chromosome(SIZE, random).randomize()
    };
    random.setSeed(SEED);
    Chromosome[] expected = {
      new Chromosome(SIZE, random).randomize(),
      new Chromosome(SIZE, random).randomize()
    };
    Chromosome.crossover(chromosomes[0], chromosomes[1]);
    String msg = "Crossover left chromosome unchanged";
    for(int i = 0; i < 2; i++) {
      assertFalse(msg, expected[0].equals(chromosomes[0]));
      assertFalse(msg, expected[1].equals(chromosomes[1]));
    }
  }

  /**
   * Test the crossover method when the chromosomes are of unequal length.
   */
  @Test
  public void testCrossoverUnequalChromosomeLengths() {
    Chromosome first = new Chromosome(SIZE, random);
    Chromosome second = new Chromosome((SIZE * 2) - 7, random);
    try {
      Chromosome.crossover(first, second, 22);
      fail("Crossover with unequally sized chromosomes did not throw");
    } catch(IllegalArgumentException e) { }
  }

  /**
   * Test the crossover method when the index given is out of bounds
   * for the chromosomes.
   */
  @Test
  public void testCrossoverIndexOutOfBounds() {
    Chromosome first = new Chromosome(SIZE, random);
    Chromosome second = new Chromosome(SIZE, random);
    try {
      Chromosome.crossover(first, second, SIZE * 2 - 17);
      fail("Crossover with bad index did not throw");
    } catch(IndexOutOfBoundsException e) { }
  }

  /**
   * Test the mutate method.
   * This is done by mutating a chromosome, counting the changed genes, and
   * figuring out if the rate of mutation is equivalent to the rate requested.
   */
  @Test
  public void testMutate() {
    final double p = 0.4;
    /* We need a larger size than the default on this class, since as the
     * the chromosomes grow, our rate calculation grows more precise.
     */
    final int size = 16384;
    Chromosome chromosome = new Chromosome(size, random).randomize();
    random.setSeed(SEED);
    Chromosome original = new Chromosome(size, random).randomize();
    chromosome.mutate(p);
    double different = 0.0;
    for(int i = 0; i < size; i++) {
      if(chromosome.atIndex(i) != original.atIndex(i)) {
        different += 1.0;
      }
    }
    double rate = different / size;
    /* A delta of 0.1 is really not tolerable, especially for a large enough
     * chromosome, but 0.05 is probably small enough for our purposes.
     */
    assertEquals(p, rate, 0.05);
  }

}
