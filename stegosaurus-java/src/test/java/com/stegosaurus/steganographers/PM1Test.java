package com.stegosaurus.steganographers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.stegutils.NativeUtils;
import com.stegosaurus.testing.TestWithInjection;

/**
 * Test the PM1Embedder and the PM1Extractor classes.
 */
public class PM1Test extends TestWithInjection {
  /**
   * The carrier image.
   */
  private JPEGImage cover;

  /**
   * An object capable of building PM1Exctractors.
   */
  private PM1Extractor.Factory extractorFactory;

  /**
   * An object capable of building PM1Embedders.
   */
  private PM1Embedder.Factory embedderFactory;

  /**
   * A random number generator.
   */
  private Random random;

  /**
   * The stego key.
   */
  private static final String KEY = "Fluttershy";

  /**
   * The message to embed.
   */
  private static final String MSG = "Sing, goddess, the anger of Achilles";

  /**
   * The seed for the PM1Embedder.
   */
  private static final short SEED = (short) 0xABBA;

  /**
   * The EmbedRequest crafted from this object's fields.
   */
  private EmbedRequest request;

  /**
   * A dummy PM sequence that returns true for every even index and false
   * for every odd index.
   */
  private static class DummySequence implements PMSequence {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean atIndex(int index) {
      return index % 2 == 0;
    }
  }

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    super.setUp();
    random = new Random();
    extractorFactory = injector.getInstance(PM1Extractor.Factory.class);
    embedderFactory = injector.getInstance(PM1Embedder.Factory.class);
    InputStream in = getClass().getResourceAsStream("lena-colour.jpeg");
    try {
      NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
      in.close();
      cover = new JPEGImage(arr.cast(), arr.length());
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
    request = new EmbedRequest(cover, MSG.getBytes(), KEY);
  }

  /**
   * Conduct a crazy test by embedding a message into an image and then
   * extracting it.
   */
  @Test
  public void testEmbedExtract() {
    PMSequence seq = new DummySequence();
    PM1Embedder emb = embedderFactory.build(random, seq);
    JPEGImage stego = emb.embed(request, SEED);
    PM1Extractor ex = extractorFactory.build(random);
    byte[] out = ex.extract(stego, KEY);
    String outStr = new String(out);
    assertEquals(MSG, outStr);
  }

  /**
   * Ensure that the fakeEmbed doesn't actually change anything at all.
   */
  @Test
  public void testFakeEmbedImmutability() {
    PM1Embedder emb = embedderFactory.build(random, new DummySequence());
    CoefficientAccessor acc = cover.getCoefficientAccessor();
    int[] expected = new int[acc.getLength()];
    for(int i = 0; i < acc.getLength(); i++) {
      expected[i] = acc.getCoefficient(i);
    }
    emb.fakeEmbed(request, SEED);
    JPEGImage other = cover.writeNew();
    acc = other.getCoefficientAccessor();
    int[] result = new int[acc.getLength()];
    for(int i = 0; i < acc.getLength(); i++) {
      result[i] = acc.getCoefficient(i);
    }
    assertArrayEquals(expected, result);
  }
}
