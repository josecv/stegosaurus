#include "jpeg_image.h"
#include "../c/dest_mgr.h"
#include "../c/src_mgr.h"
#include "../c/crop.h"

JPEGImage::JPEGImage(j_decompress_ptr d, j_compress_ptr c,
                     JOCTET *i, long imglen)
    : decomp(d),
      comp(c),
      image(i),
      len(imglen),
      coeffs(NULL) {
  steg_src_mgr_for(decomp, image, imglen);
  /* TODO : Necessary? */
  /* TODO : Error checking */
  (void) jpeg_read_header(decomp, 1);
  component_count = decomp->num_components;
}

JPEGImage::~JPEGImage() {
  free(this->image);
}

void JPEGImage::readCoefficients(void) {
  coeffs = jpeg_read_coefficients(decomp);
}

JBLOCKARRAY JPEGImage::getBlockArray(int comp, int write) {
  jpeg_component_info *component = getComponentInfo(comp);
  return (*decomp->mem->access_virt_barray) (
    (j_common_ptr) decomp,
    coeffs[comp],
    0,
    component->height_in_blocks,
    write);
}

jpeg_component_info* JPEGImage::getComponentInfo(int comp) {
  return decomp->comp_info + comp;
}

JPEGImage* JPEGImage::writeNew() {
  JOCTET *output = NULL;
  long outlen = 0;
  steg_dest_mgr_for(comp, &output, &len);
  jpeg_copy_critical_parameters(decomp, comp);
  comp->in_color_space = decomp->out_color_space;
  jpeg_write_coefficients(comp, coeffs);
  jpeg_finish_compress(comp);
  return new JPEGImage(decomp, comp, output, outlen);
}

JPEGImage* JPEGImage::doCrop(int x_off, int y_off) {
  JOCTET *output = NULL;
  long outlen = 0;
  steg_dest_mgr_for(comp, &output, &len);
  jpeg_copy_critical_parameters(decomp, comp);
  comp->in_color_space = decomp->out_color_space;
  comp->image_width = decomp->image_width - x_off;
  comp->image_height = decomp->image_height - y_off;
  jpeg_start_decompress(decomp);
  crop(decomp, comp, x_off, y_off);
  jpeg_finish_compress(comp);
  jpeg_finish_decompress(decomp);
  return new JPEGImage(decomp, comp, output, outlen);
}
