package com.stegosaurus.crypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.BitSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the Permutation class.
 */
public class PermutationTest {
  /**
   * A random number generator.
   */
  private Random r;

  /**
   * The size of the permutation, completely arbitrary.
   */
  private static final int size = 1201;

  /**
   * The actual permutation.
   */
  private Permutation permutation;

  /**
   * The indices we've seen.
   */
  private BitSet seen;

  /**
   * Set up the test.
   */
  @Before
  public void setUp() {
    r = new Random();
    permutation = new Permutation(size, r);
    permutation.init();
    seen = new BitSet(size);
  }

  /**
   * Test the Permutation class to ensure that there are no repetitions.
   */
  @Test
  public void testNoRepetition() {
    int i = 0;
    while(permutation.hasNext()) {
      int next = permutation.next();
      String msg = "Repetition at " + next + ", iteration " + i;
      assertFalse(msg, seen.get(next));
      seen.set(next);
      i++;
    }
  }

  /**
   * Test that the Permutation class actually returns every element.
   * This is implied by the no repetition test, but what the hell, testing
   * is free.
   */
  @Test
  public void testAllCovered() {
    while(permutation.hasNext()) {
      int next = permutation.next();
      seen.set(next);
    }
    assertEquals("Not every number returned", size, seen.cardinality());
  }
}
