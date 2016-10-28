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
package com.stegosaurus.stegosaurus;

import java.nio.charset.Charset;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.AbstractModule;
import com.stegosaurus.concurrent.ListeningExecutorServiceProvider;
import com.stegosaurus.crypt.DefaultPermutationProvider;
import com.stegosaurus.crypt.PermutationProvider;
import com.stegosaurus.genetic.GeneticModule;
import com.stegosaurus.steganographers.SteganographersModule;
import com.stegosaurus.steganographers.genetic.GeneticPM1Parameters;
import com.stegosaurus.stegutils.ByteBufferHelper;
import com.stegosaurus.stegutils.ByteBufferHelperImpl;

/**
 * The Stegosaurus module provides dependency injection to Stegosaurus
 * classes.
 * Particularly, it declares implementations of services required by
 * Stegosaurus classes.
 */
public class StegosaurusModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new SteganographersModule(buildGeneticParams()));
    install(new GeneticModule());
    bind(ByteBufferHelper.class).to(ByteBufferHelperImpl.class);
    bind(PermutationProvider.class).to(DefaultPermutationProvider.class);
    bind(ListeningExecutorService.class)
      .toProvider(ListeningExecutorServiceProvider.class);
    bind(HashFunction.class).toInstance(Hashing.sipHash24());
    bind(Charset.class).toInstance(Charset.defaultCharset());
    bind(StegosaurusFacade.class).to(StegosaurusFacadeImpl.class);
  }

  /**
   * Build the genetic pm1 parameters instance to be used for the hyperparameters
   * of the genetic algorithm.
   * @return the parameters.
   */
  private GeneticPM1Parameters buildGeneticParams() {
    return new GeneticPM1Parameters(
      getSPopSize(),
      getSNumberOfGenerations(),
      getSElitismRate(),
      getSMutationRate(),
      getSSelectionGradient(),
      getBPopSize(),
      getBNumberOfGenerations(),
      getBElitismRate(),
      getBMutationRate(),
      getBSelectionGradient()
    );
  }

  /**
   * Get the population size of the seed-optimizing GA.
   * Defaults to 50.
   */
  protected int getSPopSize() {
    return 50;
  }

  /**
   * Get the number of generations of the seed-optimizing GA to run.
   * Defaults to 50.
   */
  protected int getSNumberOfGenerations() {
    return 50;
  }

  /**
   * Get the elitism rate of the seed-optimizing GA.
   * Defaults to 0.4
   */
  protected double getSElitismRate() {
    return 0.4;
  }

  /**
   * Get the mutation rate of the seed-optimizing GA.
   * Defaults to 0.3
   */
  protected double getSMutationRate() {
    return 0.3;
  }

  /**
   * Get the gradient factor for rank selection in the seed-optimizing GA.
   * Defaults to 10.
   */
  protected double getSSelectionGradient() {
    return 10;
  }

  /**
   * Get the population size for the blockiness-optimizing GA.
   * Defaults to 50.
   */
  protected int getBPopSize() {
    return 50;
  }

  /**
   * Get the number of generations of the blockiness-optimizing GA to run.
   * Defaults to 50
   */
  protected int getBNumberOfGenerations() {
    return 50;
  }

  /**
   * Get the elitism rate of the blockiness-optimizing GA.
   * Defaults to 0.5
   */
  protected double getBElitismRate() {
    return 0.5;
  }

  /**
   * Get the mutation rate of the blockiness-optimizing GA.
   * Defaults to 0.1
   */
  protected double getBMutationRate() {
    return 0.1;
  }

  /**
   * Get the gradient factor for rank selection in the blockiness-optimizing GA.
   * Defaults to 10.
   */
  protected double getBSelectionGradient() {
    return 10;
  }
}
