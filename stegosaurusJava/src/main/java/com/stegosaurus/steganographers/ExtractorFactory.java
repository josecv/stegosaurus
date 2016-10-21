package com.stegosaurus.steganographers;

/**
 * Constructs new extractors.
 */
public interface ExtractorFactory {
  /**
   * Construct and return a new extractor.
   * @return the embedder.
   */
  Extractor build();
}

