package com.stegosaurus.jpeg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Test both the JPEGCompressor and the JPEGDecompressor.
 * TODO This is bad, bad stuff.
 */
public class JPEGCompressionTest {
  /**
   * Conduct an insane test by decompressing and then compressing an image,
   * then ensure that the end product is identical to the input.
   */
  @Test
  public void testCompression() {
    runTestCompression("lena-colour.jpeg");
  }

  /**
   * Like testCompression, but make use of an exif image with a thumbnail
   * instead of a traditional JFIF.
   */
  @Test
  public void testCompressionThumbnail() {
    runTestCompression("wanderer-exif.jpeg");
  }

  /**
   * Like testCompression, but make use of an image containing restart markers
   * in the scan.
   */
  @Test
  public void testCompressionRestartMarkers() {
    runTestCompression("etretat-restart.jpeg");
  }

  /**
   * Run actual compression tests, using the file given.
   * @param file the name of the file to use
   */
  private void runTestCompression(String file) {
    InputStream pic = this.getClass().getResourceAsStream(file);
    JPEGProcessor<DecompressedScan> decompressor = new JPEGDecompressor(pic);
    try {
      decompressor.init();
      pic.close();
      JPEGCompressor compressor = new JPEGCompressor();
      List<DecompressedScan> scans = decompressor.processImage();
      for(DecompressedScan scan : scans) {
        byte[] expected = scan.getData().clone();
        byte[] result = compressor.process(scan).getData().clone();
        assertArrayEquals("Failure in decompression/compression pipeline",
          expected, result);
      }
      decompressor.refresh();
      byte[] result = decompressor.getProcessed();
      OutputStream os = new FileOutputStream("horror.jpeg");
      os.write(result);
      os.close();
      InputStream expectedStream = this.getClass()
        .getResourceAsStream(file);
      byte[] expected = IOUtils.toByteArray(expectedStream);
      expectedStream.close();
      assertArrayEquals("Something went wrong in a massive pipeline",
        expected, result);
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
