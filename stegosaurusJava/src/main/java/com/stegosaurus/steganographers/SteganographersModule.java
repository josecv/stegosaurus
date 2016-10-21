package com.stegosaurus.steganographers;

import com.google.inject.AbstractModule;

import com.stegosaurus.steganographers.genetic.GeneticPM1Factory;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactoryImpl;
import com.stegosaurus.steganographers.pm1.PM1ExtractorFactory;

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
    bind(EmbedderFactory.class).to(GeneticPM1Factory.class);
    bind(ExtractorFactory.class).to(PM1ExtractorFactory.class);
  }
}

