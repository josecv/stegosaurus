#include "jpeg_context.h"

JPEGContext::JPEGContext() {

}

JPEGImage* JPEGContext::buildImage(JOCTET *i, long len) {
  return new JPEGImage(i, len);
}

void JPEGContext::destroyImage(JPEGImage* image) {
  delete image;
}

JPEGContext::~JPEGContext() {

}
