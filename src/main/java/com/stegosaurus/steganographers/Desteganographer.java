/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stegosaurus.steganographers;

import java.io.IOException;
import java.nio.ByteOrder;

import com.stegosaurus.steganographers.coders.UnHider;
import com.stegosaurus.stegutils.NumUtils;


/**
 * Mediates with an UnHider to unhide a payload from a carrier.
 *
 * @author joe
 */
public class Desteganographer {

  private UnHider unhider;

  public Desteganographer(UnHider u) {
    this.unhider = u;
  }

  /**
   * UnHide an entire message from the carrier.
   *
   * @return a byte array, the bytes found in there.
   * @throws Exception
   */
  public byte[] unHider() throws IOException {
    byte[] unhidden = unhider.unHide(4);
    int size = NumUtils.intFromBytes(unhidden, ByteOrder.LITTLE_ENDIAN);
    byte[] retval = unhider.unHide(size);
    /* We don't want the starting four bytes in the message, so trim them
     * by not returning unhider.close */
    unhider.close();
    return retval;
  }
}
