package com.stegosaurus.stegutils;

import java.nio.ByteBuffer;

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
}
