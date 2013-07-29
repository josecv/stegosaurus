package com.stegosaurus.steganographers.coders;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;

import org.junit.Test;

import com.stegosaurus.stegostreams.BitInputStream;

/**
 * Tests the DummyHider class.
 */
public class DummyHiderTest {
  /**
   * Test the hide method.
   */
  @Test
  public void testHide() {
    byte[] input = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF };
    BitInputStream stream = new BitInputStream(input);
    Hider dummy = new DummyHider();
    try {
      dummy.hide(stream, stream.available());
      stream.close();
      byte[] result = dummy.close();
      assertArrayEquals("Dummy hider failure", input, result);
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
