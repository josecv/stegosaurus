package com.stegosaurus.steganographers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.cpp.JPEGContext;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.stegutils.NativeUtils;
import com.stegosaurus.testing.TestWithInjection;

/**
 * Test the PM1Embedder and the PM1Extractor classes
 */
public class PM1Test extends TestWithInjection {
  /**
   * The JPEGContext in use here.
   */
  private JPEGContext con;

  /**
   * The carrier image.
   */
  private JPEGImage cover;

  /**
   * An object capable of building PM1Exctractors.
   */
  private PM1Extractor.Factory extractorFactory;

  private PM1Embedder.Factory embedderFactory;

  /**
   * A dummy PM sequence that returns true for every even index and false
   * for every odd index.
   */
  private static class DummySequence implements PMSequence {
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
    con = new JPEGContext();
    extractorFactory = injector.getInstance(PM1Extractor.Factory.class);
    embedderFactory = injector.getInstance(PM1Embedder.Factory.class);
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
   * Conduct a crazy test by embedding a message into an image and then
   * extracting it.
   */
  @Test
  public void testEmbedExtract() {
    String key = "fluttershy";
    String msg = "Sing, goddess, the anger of Peleus' son Achilles";
    Random r = new Random();
    PMSequence seq = new DummySequence();
    PM1Embedder emb = embedderFactory.build(r, seq);
    JPEGImage stego = emb.embed(msg.getBytes(), cover, key, (short) 0xABBA);
    PM1Extractor ex = extractorFactory.build(r);
    byte[] out = ex.extract(stego, key);
    String outStr = new String(out);
    assertEquals(msg, outStr);
  }
}
