#include <stdio.h>
#include <stdlib.h>
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
    int x_off, int y_off) {
  JSAMPARRAY buffer;
  int row_stride, i;
  row_stride = srcinfo->output_width * srcinfo->output_components;
  buffer = (*srcinfo->mem->alloc_sarray)
    ((j_common_ptr) srcinfo, JPOOL_IMAGE, row_stride, 1);
  for(i = 0; i < y_off; i++) {
    (void) jpeg_read_scanlines(srcinfo, buffer, 1);
  }
  while(srcinfo->output_scanline < srcinfo->output_height) {
    (void) jpeg_read_scanlines(srcinfo, buffer, 1);
    buffer[0] = &(buffer[0][x_off * srcinfo->output_components]);
    (void) jpeg_write_scanlines(dstinfo, buffer, 1);
  }
}


int main(int argc, char **argv) {
  char *name;
  FILE *infile;
  struct jpeg_decompress_struct srcinfo;
  struct jpeg_error_mgr djerr, sjerr;
  struct jpeg_compress_struct dstinfo;
  FILE* fp;
  if(argc != 2) {
    fprintf(stderr, "Hey fuck you man\n");
    return 1;
  }
  name = argv[1];
  if((infile = fopen(name, "rb")) == NULL) {
    fprintf(stderr, "Can't open %s\n", name);
    return 1;
  }
  srcinfo.err = jpeg_std_error(&sjerr);
  jpeg_create_decompress(&srcinfo);
  jpeg_stdio_src(&srcinfo, infile);
  (void) jpeg_read_header(&srcinfo, 1);

  dstinfo.err = jpeg_std_error(&djerr);
  jpeg_create_compress(&dstinfo);
  jpeg_copy_critical_parameters(&srcinfo, &dstinfo);
  dstinfo.image_width = srcinfo.image_width - 4;
  dstinfo.image_height = srcinfo.image_height - 4;
  dstinfo.in_color_space = srcinfo.out_color_space;
  fp = fopen("out.jpeg", "wb");
  jpeg_stdio_dest(&dstinfo, fp);
  jpeg_start_compress(&dstinfo, TRUE);

  (void) jpeg_start_decompress(&srcinfo);
  crop(&srcinfo, &dstinfo, 4, 4);

  /*(void) jpeg_finish_decompress(&srcinfo);
  jpeg_destroy_decompress(&srcinfo);*/
  fclose(infile);

  jpeg_finish_compress(&dstinfo);
  jpeg_destroy_compress(&dstinfo);
  fclose(fp);
  return 0;
}
