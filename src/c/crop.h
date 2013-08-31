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
