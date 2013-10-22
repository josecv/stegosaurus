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
    for(int i = 0; i < size; i++) {
      int val = permutation.get(i);
      String msg = "Repetition at " + val + ", index " + i;
      assertFalse(msg, seen.get(val));
      seen.set(val);
    }
  }

  /**
   * Test that the Permutation class actually returns every element.
   * This is implied by the no repetition test, but what the hell, testing
   * is free.
   */
  @Test
  public void testAllCovered() {
    for(int i = 0; i < size; i++) {
      int val = permutation.get(i);
      seen.set(val);
    }
    assertEquals("Not every number returned", size, seen.cardinality());
  }

  /**
   * Test that we can't try to get permutation elements before calling init().
   */
  @Test(expected = IllegalStateException.class)
  public void testGetBeforeInit() {
    /* Set up calls init, so we actually have to build a new one... */
    Permutation p = new Permutation(size, r);
    p.get(0);
  }

  /**
   * Test that the Permutation satisfies the other tests in this class
   * regardless of how many times the init method is called.
   */
  @Test
  public void testMultipleInit() {
    /* 5 times should be informative enough... */
    for(int i = 0; i < 5; i++) {
      /* Again, allCovered and noRepetition imply one another, so this
       * should be fine. */
      testAllCovered();
      seen.clear();
      r.setSeed(i * 1000);
      permutation.init();
    }
  }
}
