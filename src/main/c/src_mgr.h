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
