package com.stegosaurus.steganographers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeNoException;

import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGContext;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.stegutils.NativeUtils;

/**
 * Test the image permuter class.
 * TODO Need a superclass for this and the PM1Test.
 */
public class ImagePermuterTest {
  /**
   * The jpeg context.
   */
  private JPEGContext con;

  /**
   * The cover image.
   */
  private JPEGImage cover;

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    con = new JPEGContext();
    InputStream in = getClass().getResourceAsStream("lena-colour.jpeg");
    try {
      NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
      in.close();
      cover = con.buildImage(arr.cast(), arr.length());
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
  }

  /**
   * Walk the permuter given, recording any indices in the set given; if
   * any repeats happen, fail the test.
   * @param set the set.
   * @param permuter the permuter.
   * @param n the number of indices to record.
   */
  private void walk(final TIntSet set, ImagePermuter permuter, final int n) {
    permuter.walk(new TIntIntProcedure() {
      public boolean execute(int index, int value) {
        assertFalse("Index " + index + " repeated", set.contains(index));
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
    cover.readCoefficients();
    final TIntSet set = new TIntHashSet();
    long seed = "The National".hashCode();
    Random r = new Random(seed);
    CoefficientAccessor ac = cover.getCoefficientAccessor();
    Permutation p = ImagePermuter.buildPermutation(r, ac);
    p.init();
    ImagePermuter permuter = new ImagePermuter(ac, p);
    walk(set, permuter, 100);
    /* Guaranteed to reset the permutation, thus making the ImagePermuter
     * try to repeat some indices, which is what we want. */
    r.setSeed(seed);
    p.init();
    walk(set, permuter, 100);
  }
}
