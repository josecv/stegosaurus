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
import com.stegosaurus.stegutils.NativeUtils;
import com.stegosaurus.testing.TestWithInjection;

/**
 * Test the image permuter class.
 * TODO Need a different way to build up JPEGImage instances in tests.
 */
public class ImagePermuterTest extends TestWithInjection {
  /**
   * The cover image.
   */
  private JPEGImage cover;

  /**
   * The seed for the permutation.
   */
  private static final long SEED = 0xDEADBEEF;

  /**
   * The coefficient accessor for our image.
   */
  private CoefficientAccessor accessor;

  /**
   * The image permuter under test.
   */
  private ImagePermuter permuter;

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    super.setUp();
    InputStream in = getClass().getResourceAsStream("lena-colour.jpeg");
    try {
      NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
      in.close();
      cover = new JPEGImage(arr.cast(), arr.length());
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
    accessor = cover.getCoefficientAccessor();
    permuter = injector.getInstance(ImagePermuter.Factory.class)
      .build(accessor, SEED);
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
   * Test that changing the underlying permutation behaves as expected.
   */
  @Test
  public void testChangeSeed() {
    final int elements = 1000;
    final long otherSeed = 200;
    TIntSet set = new TIntHashSet();
    walk(set, permuter, elements, true);
    permuter.setSeed(otherSeed);
    permuter.reset();
    TIntSet otherSet = new TIntHashSet();
    walk(otherSet, permuter, elements, true);
    assertNotEquals("Permutation did not actually change", set, otherSet);
    /* Now we know it changes: let's ensure it doesn't do an automatic reset.
     * This can be verified simply by going back to the first one and checking
     * that we don't get the same thing again; if we don't then visited
     * elements from the second one have been successfully locked away. */
    permuter.setSeed(SEED);
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
