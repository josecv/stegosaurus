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

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedRequest;

/**
 * Embeds a message into a JPEG Image, using a plus-minus sequence given.
 */
public interface PM1Embedder extends Embedder {
  /**
   * Pretend to fulfill the embed request given, by faking embedding
   * its message into its cover. Do not actually modify anything.
   * @param request the embed request.
   * @param seed the seed to reseed the permutation with.
   * @return the number of changes required.
   */
  public int fakeEmbed(EmbedRequest request, short seed);

  /**
   * Fulfill an embed request, by embedding a message into a cover.
   * @param request the embed request to fulfill.
   * @param seed the seed to reseed the permutation with.
   * @return the new image, containing the message.
   */
  public JPEGImage embed(EmbedRequest request, short seed);
}
