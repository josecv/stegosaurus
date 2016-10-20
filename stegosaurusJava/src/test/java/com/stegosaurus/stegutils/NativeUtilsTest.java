package com.stegosaurus.stegutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assert.assertEquals;

/**
 * Tests the NativeUtils class.
 */
public class NativeUtilsTest {
  /**
   * Test the readInputStream method.
   */
  @Test
  public void testReadInputStream() {
    String test = "Now is the winter of our discontent";
    byte[] b = test.getBytes();
    InputStream in = new ByteArrayInputStream(b);
    try {
      NativeUtils.StegJoctetArray returned = NativeUtils.readInputStream(in);
      assertEquals("Bad length for array", b.length, returned.length());
      for(int i = 0; i < b.length; i++) {
        assertEquals("Bad at position " + i, b[i], returned.getitem(i));
      }
      /* This stuff is so primitive, we have to explicitely smash it */
      returned.delete();
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
  }

  /**
   * Test the writeOctetArray method.
   */
  @Test
  public void testWriteOctetArray() {
    String test = "Mozart 40";
    byte[] b = test.getBytes();
    InputStream in = new ByteArrayInputStream(b);
    try {
      NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      NativeUtils.writeOctetArray(out, arr, arr.length());
      byte[] result = out.toByteArray();
      assertEquals("Bad number of bytes read.", b.length, result.length);
      for (int i = 0; i < b.length; i++) {
        assertEquals("Bad at position " + i, b[i], result[i]);
      }
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
