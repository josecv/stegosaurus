package com.stegosaurus.steganographers.coders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.junit.Test;

import com.stegosaurus.stegutils.NumUtils;

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
    String password = "DOOM";
    InputStream jpeg = getClass().getResourceAsStream(file);
    try {
      Random random = new Random(NumUtils.intFromBytes(password.getBytes()));
      OutGuessHider hider = new OutGuessHider(random);
      byte[] hidden = hider.hide(jpeg, msg.getBytes());
      jpeg.close();
      InputStream hiddenStream = new ByteArrayInputStream(hidden);
      random.setSeed(NumUtils.intFromBytes(password.getBytes()));
      OutGuessUnHider unhider = new OutGuessUnHider(random);
      String result = new String(unhider.unHide(hiddenStream));
      hiddenStream.close();
      assertEquals("OutGuess failure", msg, result);
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
