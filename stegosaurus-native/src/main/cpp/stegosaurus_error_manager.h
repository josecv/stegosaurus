#ifndef STEGOSAURUS_ERROR_HANDLER
#define STEGOSAURUS_ERROR_HANDLER

#include <stdlib.h>
#include <stdio.h>
#include "jpeglib.h"

/**
 * Construct a stegosaurus error manager out of the jpeg_error_mgr given.
 * Note that the returned structure is actually the same as the one given.
 * @param *err the jpeg_error_mgr to turn into a stegosaurus error manager.
 * @return the stegosaurus error manager.
 */
struct jpeg_error_mgr* stegosaurus_error_mgr(struct jpeg_error_mgr *err);

#endif
