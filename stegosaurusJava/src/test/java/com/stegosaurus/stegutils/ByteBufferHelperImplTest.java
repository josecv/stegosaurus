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
package com.stegosaurus.stegutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests teh ByteBufferHelperImpl class.
 * Yes, that is quite a mouthful unfortunately.
 */
public class ByteBufferHelperImplTest {

  /**
   * The helper under test.
   */
  private ByteBufferHelper helper;

  /**
   * Set up the test.
   */
  @Before
  public void setUp() {
    helper = new ByteBufferHelperImpl();
  }

  /**
   * Ensure that the buffers provided support the array() method.
   */
  @Test
  public void testArray() {
    ByteBuffer buffer = helper.getClearedBuffer(4);
    try {
      buffer.array();
    } catch(Exception e) {
      fail("Exception thrown by array() method " + e);
    }
  }

  /**
   * Ensure that the buffers given are of the size requested.
   */
  @Test
  public void testSize() {
    for(int i = 1; i < 64; i <<= 1) {
      ByteBuffer buffer = helper.getClearedBuffer(i);
      assertEquals("Wrong size for buffer", i, buffer.array().length);
    }
  }

  /**
   * Test to ensure that the buffers have been apropriately cleared.
   */
  @Test
  public void testClearedBuffers() {
    ByteBuffer buffer = helper.getClearedBuffer(4);
    assertEquals("Wrong position for new buffer", 0, buffer.position());
    buffer.put((byte) 0);
    buffer.put((byte) 1);
    ByteBuffer bufferAgain = helper.getClearedBuffer(4);
    assertTrue("Buffer not cached", buffer == bufferAgain);
    assertEquals("Buffer not cleared", 0, bufferAgain.position());
  }

  /**
   * Ensure that the byte buffers' byte order is big endian.
   */
  @Test
  public void testByteOrder() {
    ByteBuffer buffer = helper.getClearedBuffer(2);
    assertEquals("Wrong byte order", ByteOrder.BIG_ENDIAN, buffer.order());
  }
}
