package com.stegosaurus.stegutils;

import static org.junit.Assert.assertArrayEquals;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

/**
 * Tests the message handler class.
 */
public class MessageHandlerTest {
  /**
   * Test the asByteArray method.
   */
  @Test
  public void testAsByteArray() {
    String string = "Batman";
    /* We're expecting this stuff to come out with some length data */
    byte[] expected = {0, 0, 0, 6};
    expected = ArrayUtils.addAll(expected, string.getBytes());
    MessageHandler msg = new MessageHandler(string);
    byte[] result = msg.asByteArray();
    assertArrayEquals("asByteArray failure", expected, result);
  }
}
