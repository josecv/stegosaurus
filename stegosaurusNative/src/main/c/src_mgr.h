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
 * src_mgr.h: defines the stegosaurus source manager for jpeg files.
 * It's basically a big buffer. libjpeg-turbo has a built in memory
 * source manager, but we don't use it since we may need stegosaurus
 * to work on systems that haven't been compiled with that manager.
 */
#ifndef STEGOSAURUS_SRC_MGR
#define STEGOSAURUS_SRC_MGR

#include "jpeglib.h"

/**
 * Create a new stegosaurus source manager and attach it to the compression
 * pointer given.
 * @param comp the compression object.
 * @param buffer the buffer from which we'll be returning data.
 * @param size the size of the buffer.
 */
void steg_src_mgr_for(j_decompress_ptr comp, const JOCTET *buffer, long size);

#endif
