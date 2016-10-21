package com.stegosaurus.steganographers.pm1;

import com.google.inject.Inject;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedderFactory;
import com.stegosaurus.steganographers.ImagePermuter;
import com.stegosaurus.steganographers.utils.DummyPMSequence;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Builds new PM1Embedder instances.
 * Will use a dummy sequence if none given.
 */
public class PM1EmbedderFactoryImpl implements PM1EmbedderFactory {
  /**
   * The ByteBufferHelper to inject into instances.
   */
  private ByteBufferHelper helper;

  /**
   * The ImagePermuter factory that will be injected into created instances.
   */
  private ImagePermuter.Factory permuterFactory;

  /**
   * CTOR.
   * @param helper the helper to be injected into instances.
   * @param permuterFactory the image permuter factory to inject to objects.
   */
  @Inject
  public PM1EmbedderFactoryImpl(ByteBufferHelper helper,
                                ImagePermuter.Factory permuterFactory) {
    this.permuterFactory = permuterFactory;
    this.helper = helper;
  }

  @Override
  public PM1Embedder build(PMSequence seq) {
    return new PM1EmbedderImpl(seq, helper, permuterFactory);
  }

  @Override
  public Embedder build() {
    return new PM1EmbedderImpl(new DummyPMSequence(), helper, permuterFactory);
  }
}

