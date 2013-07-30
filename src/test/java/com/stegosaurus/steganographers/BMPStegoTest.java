package com.stegosaurus.steganographers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.stegosaurus.steganographers.coders.BMPHider;
import com.stegosaurus.steganographers.coders.BMPUnHider;

/**
 * Performs full tests of BMP steganography capabilities.
 */
public class BMPStegoTest {
  /**
   * Perform a test of bmp steganography with a colour image. The image in use
   * is lena-colour.bmp
   */
  @Test
  public void colourTest() {
    runTestWith(this.getClass().getResourceAsStream("lena-colour.bmp"));
  }

  /**
   * Perform a test of bmp steganography with a black and white image. The
   * image in use is lena-bw.bmp
   */
  @Test
  public void blackWhiteTest() {
    runTestWith(this.getClass().getResourceAsStream("lena-bw.bmp"));
  }

  /**
   * Run a test of bmp steganography with the image given.
   */
  private void runTestWith(InputStream pic) {
    String msg = "Batman vs Superman";
    try {
      Steganographer stego = new Steganographer(new BMPHider(pic));
      byte[] output = stego.hide(msg);
      InputStream resultInput = new ByteArrayInputStream(output);
      Desteganographer destego =
        new Desteganographer(new BMPUnHider(resultInput));
      String result = new String(destego.unHide());
      assertEquals("Wrong message from encoding/decoding", msg, result);
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
