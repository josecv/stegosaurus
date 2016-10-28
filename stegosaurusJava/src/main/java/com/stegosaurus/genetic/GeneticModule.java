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
package com.stegosaurus.genetic;

import com.google.inject.AbstractModule;

import com.stegosaurus.genetic.GAFactory;
import com.stegosaurus.genetic.ParallelGAFactory;


/**
 * A module for genetic algorithm-related classes.
 */
public class GeneticModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GAFactory.class).to(ParallelGAFactory.class);
  }
}
