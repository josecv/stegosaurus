package com.stegosaurus.jpeg;

import static org.junit.Assert.assertArrayEquals;
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
   * Test the discrete cosine transform and its inverse.
   * TODO ACTUALLY TEST SOMETHING!!
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
      //assertArrayEquals("DCT failure", expected, result);
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
