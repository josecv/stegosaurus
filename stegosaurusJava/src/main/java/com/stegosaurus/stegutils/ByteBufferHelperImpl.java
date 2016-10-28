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
package com.stegosaurus.stegutils;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.inject.Singleton;

/**
 * Implements the ByteBufferHelper interface.
 * For any given thread, keeps track of byte buffers of any size that may
 * be requested. In other words, for any thread and any size N, there exists
 * a single byte buffer.
 * Behaves in a thread-safe manner.
 */
@Singleton
public class ByteBufferHelperImpl implements ByteBufferHelper {
  /*
   * The danger to thread safety here is that thread A requests a ByteBuffer
   * of size X, and while it's working with it, thread B requests the same
   * thing. If we're not careful, thread B's request will clear the buffer
   * thread A is working with.
   * Thus, we'll just keep this stuff in a thread local.
   */

  /**
   * A map mapping from the size of the buffers to the buffers themselves.
   */
  private final ThreadLocal<TIntObjectMap<ByteBuffer>> buffers =
    new ThreadLocal<TIntObjectMap<ByteBuffer>>() {
      @Override
      protected TIntObjectMap<ByteBuffer> initialValue() {
        /* The initial capacity is equal to the number of integer types
         * provided by java. */
        return new TIntObjectHashMap<>(4);
      }
    };

  /**
   * {@inheritDoc}
   */
  public ByteBuffer getClearedBuffer(int size) {
    TIntObjectMap<ByteBuffer> map = buffers.get();
    ByteBuffer retval = map.get(size);
    /* This saves us from the overhead of calling containsKey */
    if(retval == null) {
      retval = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
      map.put(size, retval);
    }
    retval.clear();
    return retval;
  }
}
