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

import com.stegosaurus.stegostreams.BitInputStream;


/**
 * Acts like an embed procedure but does not actively embed anything, instead
 * merely counts how many changes would be needed.
 */
public class FakeEmbedProcedure extends EmbedProcedure {

  /**
   * CTOR.
   * @param in the input stream.
   */
  public FakeEmbedProcedure(BitInputStream in) {
    super(in);
  }

  @Override
  public boolean doEmbed(int index, int val, int bit) {
    if(changeNeeded(val, bit)) {
      incrementChanges();
    }
    return true;
  }
}
