package com.stegosaurus.steganographers.genetic;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.pm1.AbstractPM1Test;

/**
 * Tests the GeneticPM1 class.
 */
public class GeneticPM1Test extends AbstractPM1Test {
  /**
   * The algorithm factory.
   */
  private GeneticPM1Factory factory;

  /**
   * Set up the test.
   */
  @Before
  public void setUp() {
    super.setUp();
    factory = injector.getInstance(GeneticPM1Factory.class);
  }

  /**
   * One of the all-or-nothing-in-a-huge-operation tests that stegosaurus is
   * famous for.
   */
  @Test
  public void testAllTheThings() {
    Embedder algo = factory.build();
    JPEGImage stego = algo.embed(request);
    assertImageContainsMessage("Stego image lacks message", stego, KEY,
                               MSG.getBytes());
  }
}
