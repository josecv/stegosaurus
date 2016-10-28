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

import java.nio.ByteBuffer;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Any algorithm that makes use of a permutation and a plus minus one sequence
 * may be described as a PM1 algorithm.
 * This class provides some common functionality for such algorithms.
 */
public abstract class PM1Algorithm {

  /**
   * The buffer helper we use to get byte buffers.
   */
  private ByteBufferHelper buffers;

  /**
   * CTOR.
   * @param buffers an object that can provide us with ByteBuffers.
   */
  protected PM1Algorithm(ByteBufferHelper buffers) {
    this.buffers = buffers;
  }

  /**
   * Get a byte buffer from the ByteBufferHelper this class has on hand.
   * @param size the size of the required buffer.
   * @return the byte buffer.
   */
  protected ByteBuffer getClearedBuffer(int size) {
    return buffers.getClearedBuffer(size);
  }

  /**
   * Get a 2-byte long byte buffer from the ByteBufferHelper this class
   * has on hand.
   * @return the byte buffer.
   */
  protected ByteBuffer getClearedBuffer() {
    return getClearedBuffer(2);
  }

  /**
   * Get a coefficient accessor for the image given.
   * @param image the image.
   * @return the built accessor.
   */
  protected CoefficientAccessor getAccessorForImage(JPEGImage image) {
    return image.getCoefficientAccessor();
  }
}
