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
 * src_mgr.c : implements the stegosaurus source manager.
 */
#include <stdio.h>
#include <stdlib.h>
#include "src_mgr.h"
#define GET_SELF(cinfo) ((stegosaurus_src_mgr *) cinfo->src)

struct _stegosaurus_src_mgr {
  /* From the jpeg_source_mgr structure */
  const JOCTET *next_input_byte;
  size_t bytes_in_buffer;
  void (*init_source) (j_decompress_ptr);
  boolean (*fill_input_buffer) (j_decompress_ptr);
  void (*skip_input_data) (j_decompress_ptr, long);
  boolean (*resync_to_restart) (j_decompress_ptr, int);
  void (*term_source) (j_decompress_ptr);
  /* Custom stuff */
  const JOCTET *buffer_start;
  long total_len;
};

typedef struct _stegosaurus_src_mgr stegosaurus_src_mgr;

/* Interface implementations */
static void init_steg_source(j_decompress_ptr comp) {
  stegosaurus_src_mgr *self = GET_SELF(comp);
  self->next_input_byte = self->buffer_start;
  self->bytes_in_buffer = self->total_len;
}

static boolean fill_steg_input_buffer(j_decompress_ptr comp) {
  stegosaurus_src_mgr *self = GET_SELF(comp);
  /* We've read past the buffer. We'll just give out some EOIs */
  JOCTET buf[2] = { 0xFF, JPEG_EOI };
  self->next_input_byte = buf;
  self->bytes_in_buffer = 2;
  return 1;
}

static void skip_steg_input_data(j_decompress_ptr comp, long n) {
  stegosaurus_src_mgr *self = GET_SELF(comp);
  if(n <= 0) {
    return;
  }
  while(n > self->bytes_in_buffer) {
    n -= self->bytes_in_buffer;
    (*self->fill_input_buffer) (comp);
  }
  self->next_input_byte += n;
  self->bytes_in_buffer -= n;
}

static void term_steg_source(j_decompress_ptr comp) {
  /* No-op. We're not responsible for freeing any data or anything */
}

/* Custom functions */

void steg_src_mgr_for(j_decompress_ptr cinfo, const JOCTET *buffer, long size) {
  stegosaurus_src_mgr *self;
  if(cinfo->src == NULL) {
    cinfo->src = (struct jpeg_source_mgr *)
      (*cinfo->mem->alloc_small) ((j_common_ptr) cinfo, JPOOL_PERMANENT,
          sizeof(stegosaurus_src_mgr));
  }
  self = GET_SELF(cinfo);
  self->buffer_start = buffer;
  self->total_len = size;
  self->init_source = &(init_steg_source);
  self->fill_input_buffer = &(fill_steg_input_buffer);
  self->skip_input_data = &(skip_steg_input_data);
  self->resync_to_restart = &(jpeg_resync_to_restart);
  self->term_source = &(term_steg_source);
}
