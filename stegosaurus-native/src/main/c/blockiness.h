/**
 * This file defines some functions useful in the computation of an image's
 * spatial blockiness.
 * These are kept separate from, say, the JPEGImage so as to try and reduce
 * the (somewhat complex) algorithm into more testable parts.
 */
#ifndef STEGOSAURUS_BLOCKINESS
#define STEGOSAURUS_BLOCKINESS
#include <stdlib.h>
#include <stdio.h>
#include "jpeglib.h"

/**
 * Calculate the blockiness for a single row. The firstRow parameter is
 * the index of the first row in the buffer. So that if buffer[0] is the
 * tenth row, firstRow would be 10.
 * If the row given is the very first in a block, use previous_row as part
 * of the calculation.
 *
 * @param components the number of components in the buffer
 * @param width the width of the buffer in pixels
 * @param samp_buffer the row to process.
 * @param first_row the index of the first row in the buffer.
 * @param row_index the row above the one to process.
 * @return the blockiness for these rows.
 */
int blockinessForRow(int components, int width, JSAMPROW samp_row,
                     int row_index, JSAMPROW previous_row);


int blockinessForRows(int components, int width, JSAMPARRAY buffer,
                      int row_count, JSAMPROW previous_block_last_row);

#endif /* STEGOSAURUS_BLOCKINESS */
