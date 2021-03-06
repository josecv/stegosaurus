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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.genetic.Chromosome;

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
   * Test the equals and hashCode methods
   */
  @Test
  public void testEqualsHashCode() {
    Chromosome first = new Chromosome(SIZE, random);
    Chromosome second = new Chromosome(SIZE, random);
    first.randomize();
    random.setSeed(SEED);
    second.randomize();
    assertEquals(first, second);
    assertEquals(second, first);
    assertTrue("Equals() idiocy", first.equals(first));
    assertFalse("Equals() idiocy", first.equals(null));
    assertEquals("hashCode() returning different hash codes for equal objects",
      first.hashCode(), second.hashCode());
    second.randomize();
    assertNotEquals(first, second);
    assertNotEquals(second, first);
    assertNotEquals("hashCode() returning same hash codes for equal objects",
      first.hashCode(), second.hashCode());
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

  /* TODO Figure out a way to factor out common elements in the asX tests */

  /**
   * Test the asShort method.
   */
  @Test
  public void testAsShort() {
    /* The short value is almost like a truncated hash, so the only thing
     * we really care about is that for two chromosomes A and B, A has
     * the same asShort value as B iff A.equals(B) */
    final int tries = 100;
    for(int i = 0; i < tries; i++) {
      random.setSeed(SEED);
      Chromosome a = new Chromosome(SIZE, random).randomize();
      /* Every so often we actually want them to be equal, or we're testing
       * nothing at all... */
      if(i % 5 == 0) {
        random.setSeed(SEED);
      }
      Chromosome b = new Chromosome(SIZE, random).randomize();
      assertTrue(!a.equals(b) || a.asShort() == b.asShort());
      assertTrue(a.asShort() != b.asShort() || a.equals(b));
    }
  }

  /**
   * Test the asDouble method.
   */
  @Test
  public void testAsDouble() {
    /* This is basically identical as the testAsShort method. */
    final int tries = 100;
    for(int i = 0; i < tries; i++) {
      random.setSeed(SEED);
      Chromosome a = new Chromosome(SIZE, random).randomize();
      if(i % 5 == 0) {
        random.setSeed(SEED);
      }
      Chromosome b = new Chromosome(SIZE, random).randomize();
      assertTrue(!a.equals(b) || a.asDouble() == b.asDouble());
      assertTrue(a.asDouble() != b.asDouble() || a.equals(b));
    }
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

  /**
   * Test that the atIndex() method does bounds checking.
   */
  @Test(expected=IndexOutOfBoundsException.class)
  public void testAtIndexBounds() {
    Chromosome chromosome = new Chromosome(SIZE, random).randomize();
    chromosome.atIndex(SIZE * 2);
  }
}
