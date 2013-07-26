package com.stegosaurus.jpeg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

/**
 * Tests the jpeg processor class.
 * TODO A better source for test data!
 */
public class JPEGProcessorTest {

  /**
   * Monstruous table of data. Subset of a JPEG file, with casts wherever the
   * compiler complained about overflow.
   */
  private static final byte[] DATA = {
    (byte) 0xFF, (byte) 0xD8, (byte) 0xFF,
    (byte) 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05,
    0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D,
    0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A,
    0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20,
    0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31,
    0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E,
    0x33, 0x34, 0x32, (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x01, 0x09,
    0x09, 0x09, 0x0C, 0x0B, 0x0C, 0x18, 0x0D, 0x0D, 0x18, 0x32, 0x21,
    0x1C, 0x21, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
    0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
    0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
    0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
    0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, (byte) 0xFF, (byte) 0xD9
  };

  /**
   * Some sample scan data to test with. Note that only the scan data is
   * included, not the marker and descriptors.
   */
  private static final byte[] SCAN = {
    (byte) 0xFF, 0x00, (byte) 0xF6, 0x18, (byte) 0xD8, 0x37, (byte) 0xDD, 0x7E,
    (byte) 0x9D, (byte) 0xF3, (byte) 0x9C, (byte) 0xD5, (byte) 0xA0,
    (byte) 0xE0, 0x1E, (byte) 0xFF, 0x00, 0x77, 0x66, 0x2A, 0x23,
    (byte) 0xD2, 0x40, 0x39, 0x20, (byte) 0xE4, 0x76, 0x15,
    (byte) 0xFF, 0x00, (byte) 0x8B, (byte) 0xE2, (byte) 0x8D, 0x75, 0x34,
    0x0F, 0x0E, (byte) 0xDE, (byte) 0xEA, 0x7B, 0x72, (byte) 0xF0, 0x46, 0x59,
    0x37, 0x29, 0x23, 0x71, (byte) 0xE0, 0x67, 0x1C, (byte) 0xFF, 0x00,
    0x67, 0x1D, (byte) 0xA8, (byte) 0xBE, (byte) 0x83, (byte) 0xB1,
    (byte) 0xF3, 0x1E, (byte) 0xA9, (byte) 0xE3, 0x5D, 0x5F, 0x53,
    (byte) 0x9A, 0x56, (byte) 0xBA, (byte) 0xBF, (byte) 0xBC, 0x76,
    0x76, (byte) 0xC9, 0x06, 0x63, (byte) 0xB4, 0x75, (byte) 0xE8,
    (byte) 0xA3, (byte) 0x81, (byte) 0xCF, (byte) 0xFF, 0x00
  };

  /**
   * The same scan data from SCAN, with 0xFF00 instances replaced by 0xFF.
   */
  private static final byte[] SCAN_UNESCAPED = {
    (byte) 0xFF, (byte) 0xF6, 0x18, (byte) 0xD8, 0x37, (byte) 0xDD, 0x7E,
    (byte) 0x9D, (byte) 0xF3, (byte) 0x9C, (byte) 0xD5, (byte) 0xA0,
    (byte) 0xE0, 0x1E, (byte) 0xFF, 0x77, 0x66, 0x2A, 0x23,
    (byte) 0xD2, 0x40, 0x39, 0x20, (byte) 0xE4, 0x76, 0x15,
    (byte) 0xFF, (byte) 0x8B, (byte) 0xE2, (byte) 0x8D, 0x75, 0x34,
    0x0F, 0x0E, (byte) 0xDE, (byte) 0xEA, 0x7B, 0x72, (byte) 0xF0, 0x46, 0x59,
    0x37, 0x29, 0x23, 0x71, (byte) 0xE0, 0x67, 0x1C, (byte) 0xFF,
    0x67, 0x1D, (byte) 0xA8, (byte) 0xBE, (byte) 0x83, (byte) 0xB1,
    (byte) 0xF3, 0x1E, (byte) 0xA9, (byte) 0xE3, 0x5D, 0x5F, 0x53,
    (byte) 0x9A, 0x56, (byte) 0xBA, (byte) 0xBF, (byte) 0xBC, 0x76,
    0x76, (byte) 0xC9, 0x06, 0x63, (byte) 0xB4, 0x75, (byte) 0xE8,
    (byte) 0xA3, (byte) 0x81, (byte) 0xCF, (byte) 0xFF
  };

  /**
   * Test the findMarker method.
   */
  @Test
  public void testFindMarker() {
    int[] expected = {0, 2, 71, DATA.length - 2};
    for(int i = 0; i < expected.length; i++) {
      int start = (i > 0 ? expected[i - 1] : -1);
      int result = JPEGProcessor.findMarker(start, DATA);
      assertEquals("Bad return value for find marker.", expected[i], result);
    }
  }

  /**
   * Test the nextSegment method.
   */
  @Test
  public void testNextSegment() {
    byte[][] segments = {
      ArrayUtils.subarray(DATA, 0, 2),
      ArrayUtils.subarray(DATA, 2, 71),
      ArrayUtils.subarray(DATA, 71, DATA.length - 2),
      ArrayUtils.subarray(DATA, DATA.length - 2, DATA.length)
    };
    int start = 0;
    for(int i = 0; i < segments.length; i++) {
      byte[] result = JPEGProcessor.nextSegment(start, DATA);
      String msg = String.format("Failure for segment #%d (starts at %d)",
        i, start);
      start += segments[i].length;
      assertArrayEquals(msg, segments[i], result);
    }
  }

  /**
   * Test the findMarker method when the given buffer contains no more
   * markers.
   */
  @Test
  public void testFindMarkerNoMarker() {
    byte[] buffer = { 0x0A, 0x09, (byte) 0xFF, 0x00, 0x05, 0x17,
      0x3A, 0x1D, 0x2E, 0x6B, 0x11 };
    int result = JPEGProcessor.findMarker(-1, buffer);
    assertEquals("Bad return value when there are no markers in the buffer",
      buffer.length, result);
  }

  /**
   * Test the unescape method.
   */
  @Test
  public void testUnescape() {
    byte[] result = JPEGProcessor.unescape(SCAN);
    assertArrayEquals("Unescape Failure", SCAN_UNESCAPED, result);
  }

  /**
   * Test the escape method.
   */
  @Test
  public void testEscape() {
    byte[] result = JPEGProcessor.escape(SCAN_UNESCAPED);
    assertArrayEquals("Escape failure", SCAN, result);
  }

  /**
   * Test that the escape and unescape methods do not change the data given.
   */
  @Test
  public void testImmutability() {
    byte[] original = SCAN.clone();
    JPEGProcessor.unescape(SCAN);
    assertArrayEquals("Unescape changed the array given", original, SCAN);
    original = SCAN_UNESCAPED.clone();
    JPEGProcessor.escape(SCAN_UNESCAPED);
    assertArrayEquals("Escape changed the array given", original,
      SCAN_UNESCAPED);
  }

  /**
   * A basic sanity test for the processImage method. Ensures that the data
   * processed is the same data that was sent and that if no processing is
   * applied, it is returned exactly as it was given.
   */
  @Test
  public void testProcessImageBasic() {
    String file = "lena-colour.jpeg";
    InputStream in = this.getClass().getResourceAsStream(file);
    JPEGProcessor proc = new DummyProcessor(in);
    try {
      proc.init();
      in.close();
      proc.processImage();
      byte[] processed = proc.getProcessed();
      InputStream expectedStream = this.getClass().getResourceAsStream(file);
      byte[] expected = IOUtils.toByteArray(expectedStream);
      expectedStream.close();
      assertArrayEquals("processImage failure", expected, processed);
    } catch (IOException ioe) {
      fail("Unexpected exception!");
    }
    
  }
}
