package com.stegosaurus.steganographers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.stegosaurus.steganographers.coders.BMPHider;
import com.stegosaurus.steganographers.coders.BMPUnHider;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.MessageHandler;

/**
 * Performs full tests of BMP steganography capabilities.
 */
public class BMPStegoTest {
  /**
   * Perform a test of bmp steganography with a black and white image. The
   * image in use is lena-bw.bmp
   */
  @Test
  public void blackWhiteTest() {
    String msg = "Batman vs Superman";
    InputStream pic = this.getClass().getResourceAsStream("lena-bw.bmp");
    try {
      Steganographer stego = new Steganographer(new BMPHider(pic));
      MessageHandler h = new MessageHandler(msg);
      byte[] output = stego.hide(new BitInputStream(h.asByteArray()));
      InputStream resultInput = new ByteArrayInputStream(output);
      Desteganographer destego =
        new Desteganographer(new BMPUnHider(resultInput));
      String result = new String(destego.unHide());
      assertEquals("Wrong message from encoding/decoding", msg, result);
    } catch (IOException ioe) {
      fail("unexpected exception");
    }
  }
}
