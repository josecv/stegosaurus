package com.stegosaurus.steganographers.pm1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.utils.DummyPMSequence;
import com.stegosaurus.stegutils.NativeUtils;
import com.stegosaurus.testing.TestWithInjection;

/**
 * Test the PM1Embedder and the PM1Extractor classes.
 */
public class PM1Test extends AbstractPM1Test {
  /**
   * An object capable of building PM1Embedders.
   */
  private PM1Embedder.Factory embedderFactory;

  /**
   * The seed for the PM1Embedder.
   */
  private static final short SEED = (short) 0xABBA;

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    super.setUp();
    embedderFactory = injector.getInstance(PM1Embedder.Factory.class);
  }

  /**
   * Conduct a crazy test by embedding a message into an image and then
   * extracting it.
   */
  @Test
  public void testEmbedExtract() {
    PMSequence seq = new DummyPMSequence();
    PM1Embedder emb = embedderFactory.build(seq);
    JPEGImage stego = emb.embed(request, SEED);
    assertImageContainsMessage("Stego image lacks message",
        stego, KEY, MSG.getBytes());
  }

  /**
   * Ensure that the fakeEmbed doesn't actually change anything at all.
   */
  @Test
  public void testFakeEmbedImmutability() {
    PM1Embedder emb = embedderFactory.build(new DummyPMSequence());
    CoefficientAccessor acc = cover.getCoefficientAccessor();
    int[] expected = new int[(int) acc.getLength()];
    for(int i = 0; i < acc.getLength(); i++) {
      expected[i] = acc.getCoefficient(i);
    }
    emb.fakeEmbed(request, SEED);
    JPEGImage other = cover.writeNew();
    acc = other.getCoefficientAccessor();
    int[] result = new int[(int) acc.getLength()];
    for(int i = 0; i < acc.getLength(); i++) {
      result[i] = acc.getCoefficient(i);
    }
    assertArrayEquals(expected, result);
  }
}
