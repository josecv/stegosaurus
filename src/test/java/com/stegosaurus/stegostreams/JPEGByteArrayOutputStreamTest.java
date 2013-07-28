package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests the JPEGByteArrayOutputStream class.
 */
public class JPEGByteArrayOutputStreamTest {
  /**
   * Test the write method, in all its forms.
   */
  @Test
  public void testWrite() {
    try {
      ByteArrayOutputStream os = new JPEGByteArrayOutputStream();
      os.write((byte) 0xDE);
      os.write((byte) 0xAD);
      os.write((byte) 0xBE);
      os.write((byte) 0xEF);
      os.write((byte) 0xFF);
      byte[] writeArray = {0x15, (byte) 0xBA, (byte) 0xDD, (byte) 0xFF, 0x38};
      os.write(writeArray, 0, writeArray.length);
      byte[] writeArray2 = {(byte) 0xFF, 0x10, 0x0A, 0x7A, 0x44};
      os.write(writeArray2);
      byte[] expected = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF,
                         (byte) 0xFF,        0x00,        0x15, (byte) 0xBA,
                         (byte) 0xDD, (byte) 0xFF,        0x00,        0x38,
                         (byte) 0xFF,        0x00,        0x10,        0x0A,
                                0x7A,        0x44};
      byte[] result = os.toByteArray();
      assertArrayEquals("write method failure", expected, result);
      os.close();
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
