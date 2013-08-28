#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"

int main(int argc, char **argv) {
  char *name;
  FILE *infile;
  struct jpeg_decompress_struct srcinfo;
  struct jpeg_error_mgr djerr, sjerr;
  struct jpeg_compress_struct dstinfo;
  FILE* fp;
  JSAMPARRAY buffer;
  int row_stride, i;
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
  row_stride = srcinfo.output_width * srcinfo.output_components;
  buffer = (*srcinfo.mem->alloc_sarray)
    ((j_common_ptr) &srcinfo, JPOOL_IMAGE, row_stride, 1);
  for(i = 0; i < 4; i++) {
    (void) jpeg_read_scanlines(&srcinfo, buffer, 1);
  }
  while(srcinfo.output_scanline < srcinfo.output_height) {
    (void) jpeg_read_scanlines(&srcinfo, buffer, 1);
    buffer[0] = &(buffer[0][4 * srcinfo.output_components]);
    (void) jpeg_write_scanlines(&dstinfo, buffer, 1);
  }

  /*(void) jpeg_finish_decompress(&srcinfo);
  jpeg_destroy_decompress(&srcinfo);
  fclose(infile);*/

  jpeg_finish_compress(&dstinfo);
  jpeg_destroy_compress(&dstinfo);
  fclose(fp);
  return 0;
}
