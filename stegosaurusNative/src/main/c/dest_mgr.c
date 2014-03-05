#include <stdio.h>
#include <stdlib.h>
#include "dest_mgr.h"
#define DEFAULT_BUFFER_SIZE 131072
#define GET_SELF(cinfo) ((stegosaurus_dest_mgr *) cinfo->dest)

/* Interface implementations */

static void init_steg_destination(j_compress_ptr cinfo) {
  stegosaurus_dest_mgr *self = GET_SELF(cinfo);
  size_t len = *(self->outlen);
  self->buffer_start = (JOCTET *) malloc(sizeof(JOCTET) * len);
  self->next_output_byte = self->buffer_start;
  self->free_in_buffer = len;
  self->buffer_len = len;
}

boolean empty_steg_output_buffer(j_compress_ptr cinfo) {
  /* You'll notice that we don't actually empty the buffer: we want the data
   * to be preserved in memory, and it would be absurd to empty the buffer
   * into another buffer. Instead, we grow our current buffer and tell the
   * library that the new buffer begins at the start of the newly allocated
   * data.
   */
  JOCTET *next;
  size_t new_len;
  stegosaurus_dest_mgr *self = GET_SELF(cinfo);
  /* We'll grow this thing by powers of 2, since that's apparently faster
   * than some alternatives. It certainly ensures that we won't be here
   * again for a while, which is a good thing. */
  new_len = self->buffer_len << 1;
  next = (JOCTET *) realloc(self->buffer_start, sizeof(JOCTET) * new_len);
  if(next == NULL) {
    /* TODO Actual error handling... */
    fprintf(stderr, "Out of memory!!\n");
    return 0;
  }
  self->buffer_start = next;
  self->next_output_byte = &(self->buffer_start[self->buffer_len]);
  self->free_in_buffer = new_len - self->buffer_len;
  self->buffer_len = new_len;
  return 1;
}

void term_steg_destination(j_compress_ptr cinfo) {
  size_t used_bytes;
  stegosaurus_dest_mgr *self = GET_SELF(cinfo);
  /* As per the contract in dest_mgr.h we don't free the buffer. We do, however
   * realloc it to its real size.
   * This may seem stupid, since we were so reluctant to copy it in dest_mgr.h.
   * I have no real defense for this policy, so it may change.
   */
  used_bytes = self->buffer_len - self->free_in_buffer;
  self->buffer_start = (JOCTET *) realloc(self->buffer_start,
    used_bytes * sizeof(JOCTET));
  *(self->output) = self->buffer_start;
  *(self->outlen) = used_bytes;
  self->buffer_len = used_bytes;
  self->next_output_byte = NULL;
  self->free_in_buffer = 0;
}

/* Custom stuff */

void steg_dest_mgr_for(j_compress_ptr comp, JOCTET **output, long *outlen) {
  stegosaurus_dest_mgr *self = (stegosaurus_dest_mgr *)
    (*comp->mem->alloc_small) ((j_common_ptr) comp, JPOOL_PERMANENT,
      sizeof(stegosaurus_dest_mgr));
  comp->dest = (struct jpeg_destination_mgr *) self;
  self->buffer_start = NULL;
  self->next_output_byte = NULL;
  self->free_in_buffer = 0;
  self->buffer_len = 0;
  self->init_destination = &(init_steg_destination);
  self->empty_output_buffer = &(empty_steg_output_buffer);
  self->term_destination = &(term_steg_destination);
  self->outlen = outlen;
  self->output = output;
  return;
}
