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
package com.stegosaurus.steganographers;

import com.stegosaurus.cpp.JPEGImage;

/**
 * A very simple data structure that encapsulates the parameters of embedding
 * a message into a JPEG image.
 */
public class EmbedRequest {
  /**
   * The cover image that will receive the embedding.
   */
  private JPEGImage cover;

  /**
   * The message to embed into the image.
   */
  private byte[] message;

  /**
   * The key to use for the embedding.
   */
  private String key;

  /**
   * CTOR.
   * @param cover the cover image that will receive the embedding.
   * @param message the message to embed into the image.
   * @param key the key to use.
   */
  public EmbedRequest(JPEGImage cover, byte[] message, String key) {
    this.cover = cover;
    this.message = message;
    this.key = key;
  }

  /**
   * Copy contructor; will copy the image in the other request, using the
   * writeNew() method.
   * @param other the image to copy.
   */
  public EmbedRequest(EmbedRequest other) {
    this.message = other.message;
    this.key = other.key;
    this.cover = other.cover.writeNew();
  }

  /**
   * Get the cover image for this object.
   * @return the cover
   */
  public JPEGImage getCover() {
    return cover;
  }

  /**
   * Get the message for this object.
   * @return the message
   */
  public byte[] getMessage() {
    return message;
  }

  /**
   * Get the key for this object.
   * @return the key
   */
  public String getKey() {
    return key;
  }
}
