package com.stegosaurus.steganographers.coders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * Tests the outguess classes.
 */
public class OutGuessTest {
  /**
   * Run a big test to see if outguess encoding/decoding works.
   */
  @Test
  public void testOutGuess() {
    String file = "/com/stegosaurus/jpeg/lena-colour.jpeg";
    String msg = "This'll be the day that I die.";
    String password = "Fluttershy";
    InputStream jpeg = getClass().getResourceAsStream(file);
    try {
      OutGuessHider hider = new OutGuessHider(password);
      byte[] hidden = hider.hide(jpeg, msg.getBytes());
      jpeg.close();
      InputStream hiddenStream = new ByteArrayInputStream(hidden);
      OutGuessUnHider unhider = new OutGuessUnHider(password);
      String result = new String(unhider.unHide(hiddenStream));
      hiddenStream.close();
      assertEquals("OutGuess failure", msg, result);
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
