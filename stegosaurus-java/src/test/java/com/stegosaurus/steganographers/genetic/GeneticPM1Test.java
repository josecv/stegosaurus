package com.stegosaurus.steganographers.genetic;

import org.junit.Ignore;
import org.junit.Test;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.PM1Test;

/**
 * Tests the GeneticPM1 class.
 * TODO extending from another test class is completely nuts. Need a mixin.
 */
public class GeneticPM1Test extends PM1Test {

  /* Ensure JUnit doesn't try to run these tests. */
  
  @Override @Test @Ignore
  public void testEmbedExtract() { }

  @Override @Test @Ignore
  public void testFakeEmbedImmutability() { }

  /**
   * One of the all-or-nothing-in-a-huge-operation tests that stegosaurus is
   * famous for.
   */
  @Test
  public void testAllTheThings() {
    GeneticPM1 algo = injector.getInstance(GeneticPM1.class);
    JPEGImage stego = algo.embed(request);
    assertImageContainsMessage("Stego image lacks message", stego, KEY,
                               MSG.getBytes());
  }
}
