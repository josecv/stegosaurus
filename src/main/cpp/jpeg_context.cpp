#include "jpeg_context.h"

JPEGContext::JPEGContext() {
  this->decomp = (j_decompress_ptr) malloc(sizeof(struct jpeg_decompress_struct));
  this->comp = (j_compress_ptr) malloc(sizeof(struct jpeg_compress_struct));
  this->decomp->err = jpeg_std_error(&(this->djerr));
  this->comp->err = jpeg_std_error(&(this->cjerr));
  jpeg_create_decompress(this->decomp);
  jpeg_create_compress(this->comp);
}

JPEGImage* JPEGContext::buildImage(JOCTET *i, long len) {
  return new JPEGImage(decomp, comp, i, len);
}

void JPEGContext::destroyImage(JPEGImage* image) {
  delete image;
}

JPEGContext::~JPEGContext() {
  jpeg_destroy_decompress(decomp);
  jpeg_destroy_compress(comp);
}
