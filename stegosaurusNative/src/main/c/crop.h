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
/**
 * Defines a function to perform a very simple crop on a jpeg image.
 */
#ifndef STEGOSAURUS_CROP
#define STEGOSAURUS_CROP

#include "jpeglib.h"

/**
 * Perform a crop of the source image from the top left, and write it into
 * the destination image.
 * You should probably ensure that compression
 * parameters have been set properly, as not even the new dimensions will
 * be set by this function.
 * In addition, it is assumed that jpeg_start_decompress has been called
 * on the source object right before this function's invocation.
 * @param src the source image
 * @param dst the destination image
 * @param x_off the horizontal offset
 * @param y_off the vertical offset
 */
void crop(j_decompress_ptr srcinfo, j_compress_ptr dstinfo,
    int x_off, int y_off);

#endif
