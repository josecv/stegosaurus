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
#ifndef STEGOSAURUS_JPEG_LIB_EXCEPTION
#define STEGOSAURUS_JPEG_LIB_EXCEPTION
#include <string>

/**
 * An exception to be thrown whenever libjpeg hands out an error.
 * The intent is that this be used in tandem with a custom libjpeg error
 * manager.
 */
class JPEGLibException {
 public:
  /**
   * CTOR.
   * @param message the exception message provided by libjpeg.
   */
  JPEGLibException(const char *message) : msg(message) { }

  /**
   * Get the message.
   */
  const char *what();

 private:
  /**
   * The message.
   */
  std::string msg;
};

#endif
