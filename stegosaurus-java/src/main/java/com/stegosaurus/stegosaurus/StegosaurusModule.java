package com.stegosaurus.stegosaurus;

import com.google.inject.AbstractModule;
import com.stegosaurus.genetic.DefaultGAFactory;
import com.stegosaurus.genetic.GAFactory;
import com.stegosaurus.stegutils.ByteBufferHelper;
import com.stegosaurus.stegutils.ByteBufferHelperImpl;

/**
 * The Stegosaurus module provides dependency injection to Stegosaurus
 * classes.
 * Particularly, it declares implementations of services required by
 * Stegosaurus classes.
 */
public class StegosaurusModule extends AbstractModule {
  /**
   * Set up the service.
   */
  @Override
  protected void configure() {
    bind(ByteBufferHelper.class).to(ByteBufferHelperImpl.class);
    bind(GAFactory.class).to(DefaultGAFactory.class);
  }
}
