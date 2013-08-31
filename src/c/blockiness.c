#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"
    
/* SCOOTALOO */
/* Get the coefficient at a given (x, y) location. */
#define COEFF(inf, buf, x, y) (buf[y / inf.blk_h][x / inf.blk_w] \
  [((y % inf.blk_h) * inf.blk_w) + (x % inf.blk_w)])

/**
 * Small struct containing a bunch of info necessary to work with jpeg blocks.
 */
struct blockinfo {
  int blk_w; /* How wide a block is, in coefficients. */
  int blk_h; /* How tall a block is, in coefficients. */
  int comp_w; /* The width of the downsampled component. */
  int comp_h; /* The height of the downsampled component. */
};

/**
 * Get the blockiness for a single component.
 * @param cinfo a pointer to the decompression handler.
 * @param cmp a pointer to the component info structure.
 * @param buffer the DCT coefficients themselves.
 */
int get_blockiness(struct jpeg_decompress_struct *cinfo,
    jpeg_component_info *cmp, JBLOCKARRAY buffer) {
  int first = 0;
  int second = 0;
  struct blockinfo info;
  int i;
  int j;
  info.comp_w = cmp->downsampled_width;
  info.comp_h = cmp->downsampled_height;
  info.blk_w = cmp->downsampled_width / cmp->width_in_blocks;
  info.blk_h = cmp->downsampled_height / cmp->height_in_blocks;
  for(i = 0; i < ((info.comp_w - 1) / 8); i++) {
    for(j = 0; j < (info.comp_h); j++) {
      first += abs(COEFF(info, buffer, 8 * i, j) -
        COEFF(info, buffer, (8 * i)+1, j));
    }
  }
  for(j = 0; j < ((info.comp_h - 1) / 8); j++) {
    for(i = 0; i < (info.comp_w); i++) {
      second += abs(COEFF(info, buffer, i, 8 * j) -
        COEFF(info, buffer, i, (8 * j) + 1));
    }
  }
  return first + second;
}
