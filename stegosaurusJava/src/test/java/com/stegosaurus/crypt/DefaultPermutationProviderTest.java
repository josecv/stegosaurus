package com.stegosaurus.crypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the DefaultPermutationProvider class.
 */
public class DefaultPermutationProviderTest {

  /**
   * The permutation provider under test.
   */
  private DefaultPermutationProvider provider;

  /**
   * Set up our test.
   */
  @Before
  public void setUp() {
    provider = new DefaultPermutationProvider();
  }

  /**
   * Test that the permutations provided are of the expected size.
   */
  @Test
  public void testCorrectSize() {
    for(int i = 1; i < 6; i++) {
      int size = i * 100;
      Permutation p = provider.getPermutation(size, 20);
      assertEquals(size, p.getSize());
    }
  }

  /**
   * Test that requesting two permutations of the same length and seed returns
   * the same objects.
   */
  @Test
  public void testSamePermutations() {
    Permutation first = provider.getPermutation(100, 20);
    Permutation second = provider.getPermutation(100, 20);
    assertTrue(first == second);
  }

  /**
   * Test that the permutations returned actually correspond to the seed
   * we passed in.
   */
  @Test
  public void testCorrectPermutation() {
    final long seed = 0xDEADBEEF;
    final int length = 1000;
    Permutation expected = new Permutation(length, seed);
    expected.init();
    Permutation returned = provider.getPermutation(length, seed);
    for(int i = 0; i < expected.getSize(); i++)  {
      assertEquals(expected.get(i), returned.get(i));
    }
  }
}
