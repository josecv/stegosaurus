/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stegosaurus.steganographers;

import com.google.inject.AbstractModule;

import com.stegosaurus.steganographers.genetic.GeneticPM1Factory;
import com.stegosaurus.steganographers.genetic.GeneticPM1Parameters;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactoryImpl;
import com.stegosaurus.steganographers.pm1.PM1ExtractorFactory;

/**
 * Declares dependencies for the steganographers module.
 */
public class SteganographersModule extends AbstractModule {

  /**
   * The genetic parameters to use.
   */
  private GeneticPM1Parameters params;

  /**
   * Construct the module, set it to use the genetic parameters given.
   * @param params the genetic parameters.
   */
  public SteganographersModule(GeneticPM1Parameters params) {
    this.params = params;
  }

  /**
   * Set up the service.
   */
  @Override
  protected void configure() {
    bind(PM1EmbedderFactory.class).to(PM1EmbedderFactoryImpl.class);
    bind(EmbedderFactory.class).to(GeneticPM1Factory.class);
    bind(ExtractorFactory.class).to(PM1ExtractorFactory.class);
    bind(GeneticPM1Parameters.class).toInstance(params);
  }
}

