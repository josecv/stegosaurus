package com.stegosaurus.steganographers;

/**
 * Constructs new embedders.
 */
public interface EmbedderFactory {
  /**
   * Construct and return a new embedder.
   * @return the embedder.
   */
  Embedder build();
}
