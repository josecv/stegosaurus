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
 * A function that will calculate the blockiness of a number of rows, and
 * may make use of the last row of the previous block, if it is non-null.
 */
typedef int (*blockinessCalcSafe) (int, int, JSAMPARRAY, int, JSAMPROW);

/**
 * A function that will calculate the blockiness of exactly 8 rows, making use
 * of the last row of the previous block.
 * Such functions do not do any safety checks.
 */
typedef int (*blockinessCalcUnsafe) (int, int, JSAMPARRAY, JSAMPROW);

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


/**
 * Calculate the blockiness for a set of rows. If previous_block_last_row
 * is not NULL, it will be taken into account in the calculation.
 * This is the most general, and safest, blockiness calculation function.
 * It is also the slowest as it does not try to optimize anything at all.
 *
 * @param components the number of components in the buffer
 * @param stride the row stride of the image i.e. width * components
 * @param buffer the buffer with the rows
 * @param row_count the number of rows in the buffer
 * @param previous_block_last_row the last row of the previous block
 */
int blockinessForRows(int components, int stride, JSAMPARRAY buffer,
                      int row_count, JSAMPROW previous_block_last_row);


/**
 * Like the blockinessForRows function, except this assumes that 8 rows are
 * given, and that the previous_block_last_row is not NULL.
 * As such, this allows for some optimization to take place. Handle with care.
 *
 * @param components the number of components in the buffer
 * @param stride the row stride of the image i.e. width * components
 * @param buffer the buffer containing the rows
 * @param previous_block_last_row the last row of the previous block; non NULL
 */
int blockinessForRowsUnsafe(int components, int stride, JSAMPARRAY buffer,
                            JSAMPROW previous_block_last_row);

/**
 * Like the blockinessForRows function, but assumes that the image contains
 * 3 components. This is, of course, the most common set up for JPEG colour
 * images.
 * Note that this function takes the STRIDE as opposed to the width.
 * The stride is equal to the pixel width times the number of components
 * (so the width * 3).
 * This function checks if previous_block_last_row is NULL and process
 * it accordingly.
 * @param components the number of components; ignored, assumed to be 3
 * @param stride the row stride of the image (width * 3)
 * @param buffer the image buffer containing the rows to process
 * @param row_count the number of rows in the buffer given
 * @param previous_block_last_row the last row of the previous block
 */
int blockinessForRows3Comp(int components, int stride, JSAMPARRAY buffer,
                           int row_count, JSAMPROW previous_block_last_row);


/**
 * Like the blockinessForRows3Comp function, but assumes that 8 rows are given,
 * and that the previous_block_last_row is not NULL.
 * @param components the number of components; ignored, assumed to be 3
 * @param stride the row stride of the image (width * 3)
 * @param buffer the image buffer containing the rows to process
 * @param previous_block_last_row the last row of the previous block; non NULL
 */
int blockinessForRows3CompUnsafe(int components, int stride, JSAMPARRAY buffer,
                                 JSAMPROW previous_block_last_row);


/**
 * Like the blockinessForRows function, but assumes that the image contains
 * a single component. This is typical for black and white JPEG images.
 * The function will check if previous_block_last_row is NULL, and process
 * it accordingly.
 * @param components the number of components; ignored, assumed to be 1
 * @param stride the row stride of the image; equal to the width
 * @param buffer the buffer containing the rows to process
 * @param row_count the number of rows to process
 * @param previous_block_last_row the last row of the previous block
 */
int blockinessForRows1Comp(int components, int stride, JSAMPARRAY buffer,
                           int row_count, JSAMPROW previous_block_last_row);


/**
 * Like the blockinessForRows1Comp function, but assumes that 8 rows are
 * given, and that the previous_block_last_row is not NULL.
 * @param components the number of components; ignored, assumed to be 1
 * @param stride the row stride of the image; equal to the width
 * @param buffer the buffer containing the rows to process
 * @param previous_block_last_row the last row of the previous block; non NULL
 */
int blockinessForRows1CompUnsafe(int components, int width, JSAMPARRAY buffer,
                                 JSAMPROW previous_block_last_row);

#endif /* STEGOSAURUS_BLOCKINESS */
