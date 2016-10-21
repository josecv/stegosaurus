package com.stegosaurus.steganographers.pm1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.Extractor;
import com.stegosaurus.steganographers.utils.DummyPMSequence;
import com.stegosaurus.stegutils.NativeUtils;
import com.stegosaurus.testing.TestWithInjection;

/**
 * Test the PM1Embedder and the PM1Extractor classes.
 */
public class AbstractPM1Test extends TestWithInjection {
  /**
   * The stego key.
   */
  protected static final String KEY = "Fluttershy";

  /**
   * The message to embed.
   */
  protected static final String MSG = "Sing, goddess, the anger of Achilles";

  /**
   * The seed for the PM1Embedder.
   */
  private static final short SEED = (short) 0xABBA;

  /**
   * The carrier image.
   */
  protected JPEGImage cover;

  /**
   * An object capable of building PM1Exctractors.
   */
  private PM1ExtractorFactory extractorFactory;

  /**
   * The EmbedRequest crafted from this object's fields.
   */
  protected EmbedRequest request;

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    super.setUp();
    extractorFactory = injector.getInstance(PM1ExtractorFactory.class);
    InputStream in = AbstractPM1Test.class.getResourceAsStream("lena-colour.jpeg");
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
   * Ensure that the image given contains the message given.
   * Really just extracts and then does an assertEquals.
   * @param msg the message to produce on failure.
   * @param image the purported cover image.
   * @param key the key to use when extracting.
   * @param expected the message the image is hoped to contain.
   */
  protected void assertImageContainsMessage(String msg,
      JPEGImage image, String key, byte[] expected) {
    Extractor ex = extractorFactory.build();
    byte[] out = ex.extract(image, key);
    assertArrayEquals(msg, expected, out);
  }
}

