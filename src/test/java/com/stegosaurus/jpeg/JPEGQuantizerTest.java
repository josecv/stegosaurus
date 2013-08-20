package com.stegosaurus.jpeg;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * Tests the JPEGQuantizer class.
 */
public class JPEGQuantizerTest {
  /**
   * Test the deQuantize and the quantize methods.
   */
  @Test
  public void testQuantization() {
    String file = "lena-colour.jpeg";
    InputStream in = getClass().getResourceAsStream(file);
    try {
      JPEGDecompressor decomp = new JPEGDecompressor(in);
      decomp.init();
      DecompressedScan scan = decomp.processImage().get(0);
      int[] expected = scan.getCoefficients().toArray();
      JPEGQuantizer.deQuantize(scan);
      JPEGQuantizer.quantize(scan);
      int[] result = scan.getCoefficients().toArray();
      assertArrayEquals("Quantization failure", expected, result);
      in.close();
    } catch(IOException e) {
      assumeNoException(e);
    }
  }
}
