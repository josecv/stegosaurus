package com.stegosaurus.steganographers;

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
   * @param random the random object this object will use. Will be reseeded.
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
