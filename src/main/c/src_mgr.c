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

void steg_src_mgr_for(j_decompress_ptr comp, const JOCTET *buffer, long size) {
  stegosaurus_src_mgr *self;
  if(comp->src == NULL) {
    comp->src = (struct jpeg_source_mgr *)
      (*comp->mem->alloc_small) ((j_common_ptr) comp, JPOOL_PERMANENT,
          sizeof(stegosaurus_src_mgr));
  }
  self = GET_SELF(comp);
  self->buffer_start = buffer;
  self->total_len = size;
  self->init_source = &(init_steg_source);
  self->fill_input_buffer = &(fill_steg_input_buffer);
  self->skip_input_data = &(skip_steg_input_data);
  self->resync_to_restart = &(jpeg_resync_to_restart);
  self->term_source = &(term_steg_source);
}
