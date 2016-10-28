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
package com.stegosaurus.steganographers.pm1;

/**
 * A plus-minus sequence to use for embedding.
 */
public interface PMSequence {
  /**
   * Get whether we should increment or decrement the value that will contain
   * the bit at the index given.
   * The index itself should correspond to an index in the message, but the
   * value to be modified is a value in the cover.
   * @param index the index
   * @return true if we should increment the value, false otherwise.
   */
  boolean atIndex(int index);
}
