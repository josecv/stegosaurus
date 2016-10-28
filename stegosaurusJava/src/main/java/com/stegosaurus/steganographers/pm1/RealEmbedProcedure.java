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

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.stegostreams.BitInputStream;


/**
 * An embed procedure that _will_ in fact embed into an image.
 */
public class RealEmbedProcedure extends EmbedProcedure {

  /**
   * The sequence used to embed.
   */
  private PMSequence seq;

  /**
   * The accessor representing the image.
   */
  private CoefficientAccessor acc;

  /**
   * The total number of bits we've seen, regardless of whether they required
   * an actual change.
   */
  private int bitsSeen;

  public RealEmbedProcedure(BitInputStream in, CoefficientAccessor acc,
                            PMSequence seq) {
    super(in);
    this.seq = seq;
    this.acc = acc;
    bitsSeen = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doEmbed(int index, int val, int bit) {
    if(changeNeeded(val, bit)) {
      incrementChanges();
      val += (seq.atIndex(bitsSeen) ? 1 : -1);
      if(val == 0) {
        val = (bit == 0 ? -1 : 1);
      }
      acc.setCoefficient(index, val);
    }
    bitsSeen++;
    return true;
  }
}
