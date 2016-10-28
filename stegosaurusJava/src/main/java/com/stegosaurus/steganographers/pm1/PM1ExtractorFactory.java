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

import com.stegosaurus.steganographers.Extractor;
import com.stegosaurus.steganographers.ExtractorFactory;
import com.stegosaurus.steganographers.ImagePermuter;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Builds PM1Extractors.
 */
public class PM1ExtractorFactory implements ExtractorFactory {
  /**
   * The ByteBufferHelper that will be injected into built instances.
   */
  private ByteBufferHelper helper;

  /**
   * The image permuter factory to give to created instances.
   */
  private ImagePermuter.Factory permFactory;

  /**
   * CTOR; should be called by Guice.
   * @param helper the ByteBufferHelper that will be given to built objects.
   * @param permFactory the permuter factory to inject into instances.
   * @param provider the permutation provider for built objects.
   */
  @Inject
  public PM1ExtractorFactory(ByteBufferHelper helper,
                             ImagePermuter.Factory permFactory) {
    this.helper = helper;
    this.permFactory = permFactory;
  }

  /**
   * Construct a new PM1Extractor.
   */
  @Override
  public Extractor build() {
    return new PM1Extractor(helper, permFactory);
  }
}
