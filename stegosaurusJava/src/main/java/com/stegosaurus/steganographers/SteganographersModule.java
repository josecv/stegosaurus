package com.stegosaurus.steganographers;

import com.google.inject.AbstractModule;

import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactoryImpl;

/**
 * Declares dependencies for the steganographers module.
 */
public class SteganographersModule extends AbstractModule {
  /**
   * Set up the service.
   */
  @Override
  protected void configure() {
    bind(PM1EmbedderFactory.class).to(PM1EmbedderFactoryImpl.class);
  }
}

