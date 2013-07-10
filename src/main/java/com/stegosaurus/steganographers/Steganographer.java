package com.stegosaurus.steganographers;

import java.io.IOException;

import com.stegosaurus.steganographers.coders.Hider;
import com.stegosaurus.stegostreams.BitInputStream;


/*
 * TODO: Make some kind of abstract Stegano-thing and then have it have two
 * abstract children: Stego and DeStego, and etc.
 */
/**
 * Mediates with a Hider to hide a payload in a carrier. Not re-usable.
 *
 * @author joe
 */
public class Steganographer {
    
  private Hider hider;

  /**
   * Initialize a steganographer.
   *
   * @param h the hider into which the payload will be hidden.
   */
  public Steganographer(Hider h) {
    this.hider = h;
  }

  /**
   * Hide the data given into this steganographer's target. Notice that this
   * makes no effort to preserve any previous data that may have been hidden
   * there.
   *
   * @param datastream the data to hide, as a stream of bits.
   * @return a byte array with the hidden data.
   * @throws IOException
   */
  public byte[] hide(BitInputStream datastream) throws IOException {
    this.hider.hide(datastream, datastream.available());
    return this.hider.close();
  }
}
