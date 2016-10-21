package com.stegosaurus.stegosaurus;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Wraps stegosaurus functionality together to offer a simple facade.
 */
public interface StegosaurusFacade {
  /**
   * Embed the message given, using the key given, into the image contained
   * in the input stream given, placing the resulting jpeg image at the
   * output stream given.
   *
   * @param in the input stream
   * @param out the output stream
   * @param message the message
   * @param key the key
   * @throws IOException on io failure
   */
  void embed(InputStream in, OutputStream out, String message, String key)
      throws IOException;

  /**
   * Extract a message from the image inside the input stream given, using
   * the key given.
   *
   * @param in the input stream
   * @param key the key
   * @return the message
   * @throws IOException on io failure
   */
  String extract(InputStream in, String key) throws IOException;
}
