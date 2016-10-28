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

import com.stegosaurus.cpp.JPEGImage;

/**
 * Embeds messages into images.
 */
public interface Embedder {
  /**
   * Embed according to the embed request given.
   * @param request the request
   * @return the image containing the message
   */
  JPEGImage embed(EmbedRequest request);

  /**
   * Get the maximum size of a message supported by this algorithm, in bits.
   * This is obviously contingent on the image being able to cover that many
   * bits.
   * Note that embed has no obligation to check that the message is of the right
   * size, and may cut it off it comes to that: checking is on you.
   */
  long getMaximumMessageSize();
}
