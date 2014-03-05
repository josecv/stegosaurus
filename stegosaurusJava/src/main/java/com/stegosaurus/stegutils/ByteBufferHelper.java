package com.stegosaurus.stegutils;

import java.nio.ByteBuffer;

/**
 * Provides byte buffers on request.
 * All the provided byte buffers must support the array() operation, and
 * must be in big endian order.
 */
public interface ByteBufferHelper {
  /**
   * Get a byte buffer of the size given.
   * Its limit will be set to its size and its current position will
   * be set to 0.
   */
  ByteBuffer getClearedBuffer(int size);
}
