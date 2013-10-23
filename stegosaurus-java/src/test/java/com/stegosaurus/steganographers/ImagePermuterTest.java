package com.stegosaurus.steganographers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeNoException;

import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.DefaultPermutationProvider;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.crypt.PermutationProvider;
import com.stegosaurus.stegutils.NativeUtils;

/**
 * Test the image permuter class.
 * TODO Need a different way to build up JPEGImage instances in tests.
 */
public class ImagePermuterTest {
  /**
   * The cover image.
   */
  private JPEGImage cover;

  /**
   * The seed for the permutation.
   */
  private static final long SEED = 0xDEADBEEF;

  /**
   * A permutation provider to get permutations quickly.
   * Not used for any fancy stuff; it's just a quicker way to get our hands
   * on Permutation instances.
   */
  private PermutationProvider provider;

  /**
   * The coefficient accessor for our image.
   */
  private CoefficientAccessor accessor;

  /**
   * The permutation we'll use.
   */
  private Permutation permutation;

  /**
   * The image permuter under test.
   */
  private ImagePermuter permuter;

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    provider = new DefaultPermutationProvider();
    InputStream in = getClass().getResourceAsStream("lena-colour.jpeg");
    try {
      NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
      in.close();
      cover = new JPEGImage(arr.cast(), arr.length());
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
    accessor = cover.getCoefficientAccessor();
    permutation = provider.getPermutation(accessor.getLength(), SEED);
    permuter = new ImagePermuter(accessor, permutation);
  }

  /**
   * Tear down the test.
   */
  @After
  public void tearDown() {
    /* TODO I have no clue why this is necessary. It shouldn't be. */
    cover.delete();
  }

  /**
   * Test the reset method.
   */
  @Test
  public void testReset() {
    TIntSet set = new TIntHashSet();
    walk(set, permuter, 100, true);
    permuter.reset();
    walk(set, permuter, 100, false);
  }

  /**
   * Test that changing the permutation behaves as expected.
   */
  @Test
  public void testChangePermutation() {
    final int elements = 1000;
    TIntSet set = new TIntHashSet();
    walk(set, permuter, elements, true);
    Permutation other = provider.getPermutation(permutation.getSize(), 200);
    permuter.setPermutation(other);
    permuter.reset();
    TIntSet otherSet = new TIntHashSet();
    walk(otherSet, permuter, elements, true);
    assertNotEquals("Permutation did not actually change", set, otherSet);
    /* Now we know it changes: let's ensure it doesn't do an automatic reset.
     * This can be verified simply by going back to the first one and checking
     * that we don't get the same thing again; if we don't then visited
     * elements from the second one have been successfully locked away. */
    permuter.setPermutation(permutation);
    otherSet.clear();
    walk(otherSet, permuter, elements, true);
    assertNotEquals("setPermutation did implicit reset", set, otherSet);
  }

  /**
   * Walk the permuter given, recording any indices in the set given.
   * If failOnRepeat is true, and any repeats happen, fail the test.
   * If failOnRepeat is false, and any new indices are seen, fail the test.
   * This might seem kind of counter intuitive, but it should never be the
   * case that some indices are new and some are not anyway.
   * @param set the set.
   * @param permuter the permuter.
   * @param n the number of indices to record.
   * @param failOnRepeat whether to fail on repeat indices; if not, fail on
   *    new indices.
   */
  private void walk(final TIntSet set, ImagePermuter permuter,
                    final int n, final boolean failOnRepeat) {
    permuter.walk(new TIntIntProcedure() {
      public boolean execute(int index, int value) {
        assertEquals(!failOnRepeat, set.contains(index));
        set.add(index);
        return set.size() < n;
      }
    });
  }

  /**
   * Test that the permuter does not produce repeat indices.
   */
  @Test
  public void testNoRepeat() {
    final TIntSet set = new TIntHashSet();
    walk(set, permuter, 100, true);
    /* Doing this is kind of equivalent to starting the walk from scratch,
     * since the only state being kept are the indices we've seen.
     */
    walk(set, permuter, 200, true);
  }
}
