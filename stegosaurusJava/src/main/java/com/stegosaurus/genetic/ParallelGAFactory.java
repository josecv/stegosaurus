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

import java.util.Random;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Builds Parallel genetic algorithms.
 */
class ParallelGAFactory implements GAFactory {
    /**
     * The executor service to provide to children instances.
     */
    private ListeningExecutorService service;

    /**
     * Construct a factory.
     * @param service the executor service to provide to built instances.
     */
    @Inject
    public ParallelGAFactory(ListeningExecutorService service) {
      this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Individual<C>> GeneticAlgorithm<C> build(
        IndividualFactory<C> factory,
        SelectionOperator<C> selection,
        Random random,
        GAParameters params) {
      return new ParallelGA<C>(factory, selection, random, params, service);
    }
  }
