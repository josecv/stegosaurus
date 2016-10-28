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
    permutation = new Permutation(size, r.nextLong());
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
    Permutation p = new Permutation(size, r.nextLong());
    p.get(0);
  }

  /**
   * Test that the init() method can only be called once on a given
   * permutation.
   */
  @Test(expected = IllegalStateException.class)
  public void testMultipleInit() {
    permutation.init();
  }
}
