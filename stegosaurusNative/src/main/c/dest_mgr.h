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
 * dest_mgr.h: defines the stegosaurus destination manager for jpeg files.
 * It's basically a big buffer. libjpeg-turbo has a built in memory
 * destination manager, but we don't use it since we may need stegosaurus
 * to work on systems that make use of the traditional libjpeg.
 */
#ifndef STEGOSAURUS_DEST_MGR
#define STEGOSAURUS_DEST_MGR

#include "jpeglib.h"

/**
 * The destination manager for stegosaurus. "Extends" the jpeg_destination_mgr.
 *
 * IMPORTANT: note that calls to  the init_destination or the term_destination
 * methods will NOT nuke the buffer, since in the context of stegosaurus
 * you'll likely want to use it for decompression right away, and it would
 * be a waste of time to copy it over. If you use the stegosaurus_src_mgr, iti
 * will deallocate the buffer for you; otherwise, freeing that memory is on
 * you.
 */
struct _stegosaurus_dest_mgr {
  /* Stuff from the jpeg_destination_mgr struct */
  JOCTET *next_output_byte;
  size_t free_in_buffer;
  void (*init_destination) (j_compress_ptr);
  boolean (*empty_output_buffer) (j_compress_ptr);
  void (*term_destination) (j_compress_ptr);
  /* Custom stuff starts here */
  JOCTET *buffer_start; /* Pointer to the very start of the buffer */
  size_t buffer_len; /* Total length of the buffer, including unused JOCTETs */
  JOCTET **output; /* The output buffer */
  long *outlen; /* The length of the output buffer */
};

typedef struct _stegosaurus_dest_mgr stegosaurus_dest_mgr;

/* Custom methods */

/**
 * Create a new stegosaurus_dest_mgr and associate it to the compression
 * pointer given.
 * Notice that you should give the actual len of the desired buffer in the form
 * of outlen so that it can all be allocated right away: there's a bug of
 * some sort (either in this code or libjpeg turbo) that causes data
 * corruption when the buffer has to be grown, so try to avoid that.
 * @param comp the compression object.
 * @param output pointer to the output buffer, which should be null.
 * @param outlen pointer to the output length.
 */
void steg_dest_mgr_for(j_compress_ptr comp, JOCTET **output, long *outlen);

#endif
