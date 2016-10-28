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

import java.util.List;
import java.util.Random;

/**
 * Selects individuals for reproduction out of a list of possible candidates.
 * @param <T> the kind of Individual to operate on.
 */
public interface SelectionOperator<T extends Individual<T>> {
  /**
   * Select an individual for reproduction from the population pool given, and
   * return its index in the population.
   * Note that the population pool must be sorted from fittest to least fit
   * (i.e. from the individual with the smallest fitness value, to the one
   * with the largest fitness value).
   * @param population the population pool to select from.
   * @param random the random  number generator to use.
   * @return the index selected individual.
   */
  int select(List<? extends Individual<T>> population, Random random);
}
