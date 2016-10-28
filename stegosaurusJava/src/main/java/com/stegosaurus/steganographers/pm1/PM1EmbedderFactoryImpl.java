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

import com.google.inject.Inject;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedderFactory;
import com.stegosaurus.steganographers.ImagePermuter;
import com.stegosaurus.steganographers.utils.DummyPMSequence;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Builds new PM1Embedder instances.
 * Will use a dummy sequence if none given.
 */
public class PM1EmbedderFactoryImpl implements PM1EmbedderFactory {
  /**
   * The ByteBufferHelper to inject into instances.
   */
  private ByteBufferHelper helper;

  /**
   * The ImagePermuter factory that will be injected into created instances.
   */
  private ImagePermuter.Factory permuterFactory;

  /**
   * CTOR.
   * @param helper the helper to be injected into instances.
   * @param permuterFactory the image permuter factory to inject to objects.
   */
  @Inject
  public PM1EmbedderFactoryImpl(ByteBufferHelper helper,
                                ImagePermuter.Factory permuterFactory) {
    this.permuterFactory = permuterFactory;
    this.helper = helper;
  }

  @Override
  public PM1Embedder build(PMSequence seq) {
    return new PM1EmbedderImpl(seq, helper, permuterFactory);
  }

  @Override
  public Embedder build() {
    return new PM1EmbedderImpl(new DummyPMSequence(), helper, permuterFactory);
  }
}

