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

import gnu.trove.procedure.TIntIntProcedure;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.stegostreams.BitInputStream;

/**
 * The abstract callables used to embed data into images.
 * The class provides a method to actually construct an EmbedProcedure
 * depending on what is required.
 * Should be used with an ImagePermutater instance.
 * Construction is cheap, so it is safe to use this in a throwaway manner.
 */
abstract class EmbedProcedure implements TIntIntProcedure {
  /**
   * The message stream.
   */
  private BitInputStream in;

  /**
   * The number of changes required for the embedding.
   */
  private int changes;

  /**
   * CTOR.
   * @param in the message stream.
   * @param acc the accessor for the image.
   */
  public EmbedProcedure(BitInputStream in) {
    this.in = in;
    changes = 0;
  }

  protected abstract boolean doEmbed(int index, int val, int bit);

  protected void incrementChanges() {
    changes++;
  }

  protected boolean changeNeeded(int val, int bit) {
    /*
     * A Negative even coefficient is a one, a negative odd coefficient
     * is a zero, a positive even coefficient is a zero, and a positive
     * even coefficient is a one.
     * Hence the following if statement to determine whether something
     * needs to be changed in the carrier.
     */
    return ((val < 0 ? ~val : val) & 1) != bit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean execute(int index, int val) {
    int bit = in.read();
    if(bit < 0) {
      return false;
    }
    return doEmbed(index, val, bit);
  }

  public static EmbedProcedure build(BitInputStream in, CoefficientAccessor acc,
                                     PMSequence seq, boolean real) {
    if(real) {
      return new RealEmbedProcedure(in, acc, seq);
    }
    return new FakeEmbedProcedure(in);
  }

  /**
   * Get the change count for this embedding.
   * @return the change count.
   */
  public int getChanges() {
    return changes;
  }
}
