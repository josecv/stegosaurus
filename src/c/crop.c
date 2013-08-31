#include <stdio.h>
#include <stdlib.h>
#include "crop.h"
#include "dest_mgr.h"
#include "src_mgr.h"

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
    if(!jpeg_read_scanlines(srcinfo, buffer, 1)) {
      fprintf(stderr, "Read error\n");
      return;
    }
    buffer[0] = &(buffer[0][x_off * srcinfo->output_components]);
    if(!jpeg_write_scanlines(dstinfo, buffer, 1)) {
      fprintf(stderr, "Write error\n");
      return;
    }
  }
}

static void read_file(JOCTET **buf, long *bufsize, FILE *fp) {
  if(fseek(fp, 0L, SEEK_END) == 0) {
    *bufsize = ftell(fp);
    if(*bufsize == -1) {
      perror("read_file");
      return;
    }
    *buf = (JOCTET *) malloc(sizeof(JOCTET) * (*bufsize));
    if(!fseek(fp, 0L, SEEK_SET)) {
      *bufsize = fread(*buf, sizeof(JOCTET), *bufsize, fp);
    }
  }
}

int main(int argc, char **argv) {
  char *name;
  struct jpeg_decompress_struct srcinfo;
  struct jpeg_error_mgr djerr, sjerr;
  struct jpeg_compress_struct dstinfo;
  FILE *outfile, *infile;
  long len;
  JOCTET *inbuf = NULL, *outbuf = NULL;
  if(argc != 2) {
    fprintf(stderr, "Hey fuck you man\n");
    return 1;
  }
  name = argv[1];
  if((infile = fopen(name, "rb")) == NULL) {
    fprintf(stderr, "Can't open %s\n", name);
    return 1;
  }
  read_file(&inbuf, &len, infile);
  fclose(infile);
  srcinfo.err = jpeg_std_error(&sjerr);
  jpeg_create_decompress(&srcinfo);
  steg_src_mgr_for(&srcinfo, inbuf, len);
  (void) jpeg_read_header(&srcinfo, 1);

  dstinfo.err = jpeg_std_error(&djerr);
  jpeg_create_compress(&dstinfo);
  jpeg_copy_critical_parameters(&srcinfo, &dstinfo);
  dstinfo.image_width = srcinfo.image_width - 4;
  dstinfo.image_height = srcinfo.image_height - 4;
  dstinfo.in_color_space = srcinfo.out_color_space;

  steg_dest_mgr_for(&dstinfo, &outbuf, &len);
  jpeg_start_compress(&dstinfo, TRUE);


  (void) jpeg_start_decompress(&srcinfo);
  crop(&srcinfo, &dstinfo, 4, 4);


  jpeg_finish_compress(&dstinfo);
  jpeg_destroy_compress(&dstinfo);

  outfile = fopen("out.jpeg", "wb");
  fwrite(outbuf, sizeof(JOCTET), (size_t) len, outfile);
  free(outbuf);
  free(inbuf);
  fclose(outfile);

  return 0;
}
