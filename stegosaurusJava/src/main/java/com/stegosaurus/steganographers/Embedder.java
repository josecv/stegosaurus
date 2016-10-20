package com.stegosaurus.steganographers;

import com.stegosaurus.cpp.JPEGImage;

/**
 * Embeds messages into images.
 */
public interface Embedder {
  /**
   * Embed according to the embed request given.
   * @param request the request
   * @return the image containing the message
   */
  JPEGImage embed(EmbedRequest request);
}
