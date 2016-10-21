package com.stegosaurus.steganographers.pm1;

import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedderFactory;

/**
 * Constructs PM1Embedders.
 */
public interface PM1EmbedderFactory extends EmbedderFactory {
 /**
  * Build a new PM1Embedder.
  * @param seq the plus-minus sequence to direct this object's embedding.
  * @return the embedder
  */
  PM1Embedder build(PMSequence seq);
}
