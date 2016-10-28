/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
  buffer2 = (JSAMPARRAY) malloc(sizeof(JSAMPROW) * y_off);
  int row_stride, i;
  row_stride = srcinfo->output_width * srcinfo->output_components;
  buffer = (*srcinfo->mem->alloc_sarray)
    ((j_common_ptr) srcinfo, JPOOL_IMAGE, row_stride, y_off);
  for(i = 0; i < y_off; ++i) {
    (void) jpeg_read_scanlines(srcinfo, buffer, 1);
  }
  while(srcinfo->output_scanline < srcinfo->output_height) {
    int scanlines_read;
    if(!(scanlines_read = jpeg_read_scanlines(srcinfo, buffer, y_off))) {
      fprintf(stderr, "Read error\n");
      return;
    }
    for(i = 0; i < scanlines_read; ++i) {
      buffer2[i] = &(buffer[i][x_off * srcinfo->output_components]);
    }
    if(!jpeg_write_scanlines(dstinfo, buffer2, scanlines_read)) {
      fprintf(stderr, "Write error\n");
      return;
    }
  }
  free(buffer2);
}
