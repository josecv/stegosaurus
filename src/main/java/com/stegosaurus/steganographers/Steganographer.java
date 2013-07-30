package com.stegosaurus.steganographers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.steganographers.coders.Hider;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.NumUtils;


/**
 * Mediates with a Hider to hide a payload in a carrier. Not re-usable.
 * At no time attempts to preserve any already encoded data.
 */
public class Steganographer {
    
  /**
   * The hider in use.
   */
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
   * Hide the data from the stream given into this steganographer's target.
   * Note that it is not closed.
   * @param stream the data to hide, as a stream.
   * @return a byte array, being the carrier containing the data.
   * @throws IOException if the stream given produces an error, or on
   *    hider failure
   */
  public byte[] hide(InputStream stream) throws IOException {
    return hide(IOUtils.toByteArray(stream));
  }

  /**
   * Hide the message given into this steganographer's target.
   * @param message the data to hide, as a string.
   * @return a byte array, being the carrier containing the data.
   * @throws IOException on hider failure
   */
  public byte[] hide(String message) throws IOException {
    return hide(message.getBytes());
  }

  /**
   * Hide the data given into this steganographer's target.
   * @param data the data to hide.
   * @return a byte array, being the carrier containing the data.
   * @throws IOException on hider failure
   */
  public byte[] hide(byte[] data) throws IOException {
    byte[] encodeData = NumUtils.byteArrayFromInt(data.length);
    encodeData = ArrayUtils.addAll(encodeData, data);
    BitInputStream in = new BitInputStream(encodeData);
    hider.hide(in, in.available());
    in.close();
    return hider.close();
  }

  /**
   * Hide the data given into this steganographer's target.
   * @param datastream the data to hide, as a stream of bits.
   * @return a byte array, being the carrier containing the data.
   * @throws IOException on hider failure
   */
  public byte[] hide(BitInputStream datastream) throws IOException {
    this.hider.hide(datastream, datastream.available());
    return this.hider.close();
  }
}
