/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sequential bit inputstream class.
 */
public class BitInputStreamTest {

  /**
   * The stream under test.
   */
  private BitInputStream stream;

  /**
   * Set up the test. Nulls the stream so that we know when it needs to
   * be closed.
   */
  @Before
  public void setUp() {
    stream = null;
  }

  /**
   * Tear down the test.
   */
  @After
  public void tearDown() {
    if(stream != null) {
      stream.close();
    }
  }

  /**
   * Test the read method.
   */
  @Test
  public void testRead() {
    byte[] arg = { 0x5D, 0x2A, 0x3F };
    stream = new BitInputStream(arg);
    byte[] expected = { 0, 1, 0, 1,  1, 1, 0, 1,
                        0, 0, 1, 0,  1, 0, 1, 0,
                        0, 0, 1, 1,  1, 1, 1, 1
                      };
    byte[] retval = new byte[expected.length];
    for (int i = 0; i < retval.length; i++) {
      retval[i] = (byte) stream.read();
    }
    assertArrayEquals("Wrong return value from read", expected, retval);
  }

  /**
   * Ensure that the read() method returns -1 on reaching the end of stream.
   */
  @Test
  public void testEndOfStream() {
    byte[] arg = { 0x5D };
    stream = new BitInputStream(arg);
    for(int i = 0; i < arg.length * 8; i++) {
      stream.read();
    }
    assertEquals("Bad return value from end of stream", -1, stream.read());
  }

  /**
   * Test the skipToEndOfByte method.
   */
  @Test
  public void testSkipToEndOfByte() {
    byte[] arg = { 0b01001111, 0b00001101 };
    stream = new BitInputStream(arg);
    for(int i = 0; i < 3; i++) {
      stream.read();
    }
    stream.skipToEndOfByte();
    for(int i = 0; i < 4; i++) {
      assertEquals("Failed to skip all the way to next byte", 0, stream.read());
    }
  }

  /**
   * Test the skip method.
   */
  @Test
  public void testSkip() {
    byte[] arg = { 0x1A, 0x4B, 0x18 };
    byte[] expected = { 0, 0, 0, 1,  1, 0, 1, 0,
                        0, 1, 0, 0,
                                     1, 0, 0, 0};
    byte[] result = new byte[expected.length];
    stream = new BitInputStream(arg);
    int i;
    for(i = 0; i < 12; i ++) {
      result[i] = (byte) stream.read();
    }
    assertEquals("Incorrect number of bytes skipped", 8, stream.skip(8));
    for(; i < expected.length; i++) {
      result[i] = (byte) stream.read();
    }
    assertArrayEquals("Wrong result after skipping bytes", expected, result);
  }

  /**
   * Test the reset method.
   */
  @Test
  public void testReset() {
    byte[] arg = { 0x1A, 0x4B };
    stream = new BitInputStream(arg);
    byte[] newarg = { (byte) 0xCA, (byte) 0xFE };
    byte[] otherarg = { (byte) 0xBA, (byte) 0xBE };
    stream.reset(newarg, otherarg);
    byte[] expected = { 1, 1, 0, 0,  1, 0, 1, 0,
                        1, 1, 1, 1,  1, 1, 1, 0,
                        1, 0, 1, 1,  1, 0, 1, 0,
                        1, 0, 1, 1,  1, 1, 1, 0
                      };
    byte[] result = new byte[expected.length];
    for(int i = 0; i < expected.length; i++) {
      result[i] = (byte) stream.read();
    }
    assertArrayEquals("Reset not working", expected, result);
  }
}
