#include <stdio.h>
#include <stdlib.h>
#include "crop.h"
#include "dest_mgr.h"
#include "src_mgr.h"

void crop(j_decompress_ptr srcinfo, j_compress_ptr dstinfo,
    int x_off, int y_off) {
  /* We use the second buffer here to be able to get an offset of the first
   * one's first row, without fooling around with the first buffer (which
   * causes memory corruption, as you might expect)
   */
  JSAMPARRAY buffer, buffer2;
  buffer2 = (JSAMPARRAY) malloc(sizeof(JSAMPROW));
  int row_stride, i;
  row_stride = srcinfo->output_width * srcinfo->output_components;
  buffer = (*srcinfo->mem->alloc_sarray)
    ((j_common_ptr) srcinfo, JPOOL_IMAGE, row_stride, 1);
  for(i = 0; i < y_off; i++) {
    (void) jpeg_read_scanlines(srcinfo, buffer, 1);
  }
  while(srcinfo->output_scanline < srcinfo->output_height) {
    if(!jpeg_read_scanlines(srcinfo, buffer, 1)) {
      fprintf(stderr, "Read error\n");
      return;
    }
    buffer2[0] = &(buffer[0][x_off * srcinfo->output_components]);
    if(!jpeg_write_scanlines(dstinfo, buffer2, 1)) {
      fprintf(stderr, "Write error\n");
      return;
    }
  }
  free(buffer2);
}
