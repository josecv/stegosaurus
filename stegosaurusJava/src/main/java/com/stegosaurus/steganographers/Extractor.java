package com.stegosaurus.steganographers;

import com.stegosaurus.cpp.JPEGImage;

/**
 * Extracts messages from carrier JPEG images.
 */
public interface Extractor {
  /**
   * Extracts a message from the image given, using the key given, and returns
   * it as a byte array.
   *
   * @param image the image
   * @param key the key
   * @return the message
   */
  byte[] extract(JPEGImage image, String key);
}
