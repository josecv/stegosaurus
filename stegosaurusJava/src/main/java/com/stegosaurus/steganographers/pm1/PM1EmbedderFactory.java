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

import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedderFactory;

/**
 * Constructs PM1Embedders.
 */
public interface PM1EmbedderFactory extends EmbedderFactory {
 /**
  * Build a new PM1Embedder.
  * @param seq the plus-minus sequence to direct this object's embedding.
  * @return the embedder
  */
  PM1Embedder build(PMSequence seq);
}
