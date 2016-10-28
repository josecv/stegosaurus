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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Wraps stegosaurus functionality together to offer a simple facade.
 */
public interface StegosaurusFacade {
  /**
   * Embed the message given, using the key given, into the image contained
   * in the input stream given, placing the resulting jpeg image at the
   * output stream given.
   *
   * @param in the input stream
   * @param out the output stream
   * @param message the message
   * @param key the key
   * @throws IOException on io failure
   */
  void embed(InputStream in, OutputStream out, String message, String key)
      throws IOException;

  /**
   * Extract a message from the image inside the input stream given, using
   * the key given.
   *
   * @param in the input stream
   * @param key the key
   * @return the message
   * @throws IOException on io failure
   */
  String extract(InputStream in, String key) throws IOException;
}
