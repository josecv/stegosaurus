/**
 * dest_mgr.h: defines the stegosaurus destination manager for jpeg files.
 * It's basically a big buffer. libjpeg-turbo has a built in memory
 * destination manager, but we don't use it since we may need stegosaurus
 * to work on systems that make use of the traditional libjpeg.
 */
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
};

typedef struct _stegosaurus_dest_mgr stegosaurus_dest_mgr;

/* Custom methods */

/**
 * Create a new stegosaurus_dest_mgr and associate it to the compression
 * pointer given.
 * @param comp the compression object.
 * @return the new destination manager.
 */
stegosaurus_dest_mgr *steg_dest_mgr_for(j_compress_ptr comp);

/**
 * Destroys a stegosaurus_dest_mgr structure and associated memory. Just like
 * the init_destination method, this doesn't actually free the buffer, which
 * is the bigger memory drain anyway. While you should obviously destroy
 * any destination managers you use, you should keep a closer eye on making
 * sure the buffer is properly disposed of.
 * @param target the destination manager to destroy.
 */
void destroy_steg_dest_mgr(stegosaurus_dest_mgr* target);

