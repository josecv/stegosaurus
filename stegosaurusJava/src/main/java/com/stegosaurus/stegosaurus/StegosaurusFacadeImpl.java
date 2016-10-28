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
package com.stegosaurus.stegosaurus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import com.google.inject.Inject;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.cpp.JoctetArray;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedderFactory;
import com.stegosaurus.steganographers.Extractor;
import com.stegosaurus.steganographers.ExtractorFactory;
import com.stegosaurus.stegutils.NativeUtils;

/**
 * Implements the stegosaurus facade.
 */
class StegosaurusFacadeImpl implements StegosaurusFacade {

  /**
   * The embedder factory.
   */
  private EmbedderFactory embedderFactory;

  /**
   * The extractor factory.
   */
  private ExtractorFactory extractorFactory;

  /**
   * CTOR.
   *
   * @param embedderFactory the embedder factory
   * @param extractorFactory the extractor factory
   */
  @Inject
  StegosaurusFacadeImpl(EmbedderFactory embedderFactory,
                        ExtractorFactory extractorFactory) {
    this.embedderFactory = embedderFactory;
    this.extractorFactory = extractorFactory;
  }

  @Override
  public void embed(InputStream in, OutputStream out, String message, String key)
      throws IOException {
    NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
    JPEGImage cover = new JPEGImage(arr.cast(), arr.length());
    EmbedRequest request = new EmbedRequest(cover, message.getBytes(), key);
    Embedder embedder = embedderFactory.build();
    JPEGImage result = embedder.embed(request);
    JoctetArray outArray = JoctetArray.frompointer(result.getData());
    NativeUtils.writeOctetArray(out, outArray, result.getDataLen());
  }

  @Override
  public String extract(InputStream in, String key) throws IOException {
    NativeUtils.StegJoctetArray arr = NativeUtils.readInputStream(in);
    JPEGImage cover = new JPEGImage(arr.cast(), arr.length());
    Extractor ex = extractorFactory.build();
    byte[] byteArray = ex.extract(cover, key);
    return new String(byteArray);
  }
}
