package com.stegosaurus.steganographers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.stegosaurus.steganographers.coders.DummyHider;

/**
 * Tests the Steganogrpher class.
 */
public class SteganographerTest {
  /**
   * Tests hiding a byte array.
   */
  @Test
  public void testHideBytes() {
    Steganographer stego = new Steganographer(new DummyHider());
    byte[] input = {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
    byte[] expected = {0, 0, 0, 4};
    expected = ArrayUtils.addAll(expected, input);
    try {
      byte[] result = stego.hide(input);
      assertArrayEquals("Hide not working properly", expected, result);
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
