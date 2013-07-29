package com.stegosaurus.steganographers.coders;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegostreams.BitOutputStream;

/**
 * Pretends to hide some data inside of a carrier. In reality just buffers
 * any data given. Useful for tests.
 */
public class DummyHider implements Hider {

  byte[] buf;

  /**
   * Closes this dummy hider, returning the data that was passed in.
   * @return the same data that was given.
   */
  @Override
  public byte[] close() {
    return buf.clone();
  }

  /**
   * Pretend to hide the data given.
   * @param stream the stream containing the data to hide.
   * @param len the number of bits that will be hidden.
   */
  @Override
  public void hide(BitInputStream stream, int len) throws IOException {
    BitOutputStream os = new BitOutputStream();
    os.write(IOUtils.toByteArray(stream, len));
    os.close();
    buf = os.data();
  }
}
