package com.stegosaurus.jpeg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

/**
 * Test the DCT class.
 */
public class DCTTest {

  /**
   * Acceptable range for DCT coefficients. Thus, if we expect 6 but we get 7
   * or 5 that's fine, but 2 is not.
   */
  private static final int RANGE = 4;

  /**
   * Test the discrete cosine transform and its inverse.
   */
  @Test
  public void testDCT() {
    String file = "lena-colour.jpeg";
    InputStream in = getClass().getResourceAsStream(file);
    try {
      JPEGDecompressor decomp = new JPEGDecompressor(in);
      decomp.init();
      DecompressedScan scan = decomp.processImage().get(0);
      JPEGQuantizer.deQuantize(scan);
      int[] expected = scan.getCoefficients().toArray();
      DCT.inverseDct(scan);
      DCT.dct(scan);
      int[] result = scan.getCoefficients().toArray();
      assertEquals("Length disparity. This is VERY bad",
        expected.length, result.length);
      for(int i = 0; i < expected.length; i++) {
        String msg = String.format("Error at index %d. Expected ~%d was %d",
          i, expected[i], result[i]);
        int r = result[i], e = expected[i];
        boolean expr = e - RANGE <= r && r <= e + RANGE;
        assertTrue(msg, expr);
      }
      JPEGQuantizer.quantize(scan);
      JPEGCompressor comp = new JPEGCompressor();
      comp.process(scan);
      decomp.refresh();
      byte[] end = decomp.getProcessed();
      OutputStream out = new FileOutputStream("horror.jpg");
      out.write(end);
      out.close();
    } catch(IOException e) {
      assumeNoException(e);
    }
  }
}
