/**
 * Defines the blockiness function for an array of DCT coefficients
 * representing a single component in an image.
 */
#include "jpeglib.h"

/**
 * Get the blockiness for a single component.
 * @param cinfo a pointer to the decompression handler.
 * @param cmp a pointer to the component info structure.
 * @param buffer the DCT coefficients themselves.
 */
int get_blockiness(struct jpeg_decompress_struct *cinfo,
    jpeg_component_info *cmp, JBLOCKARRAY buffer);
