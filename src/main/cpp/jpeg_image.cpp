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
  components = new JPEGComponent*[component_count]();
  coefficients = new JBLOCKARRAY[component_count]();
}

JPEGImage::~JPEGImage() {
  int i;
  free(this->image);
  /* We obviously have to delete the components themselves before we free
   * the pointer array.
   * Since it's possible that not all of them were accessed, we check before
   * we free them.
   */
  for(i = 0; i < component_count; i++) {
    if(components[i] != NULL) {
      delete components[i];
    }
    if(coefficients[i] != NULL) {
      delete [] coefficients[i];
    }
  }
  delete [] components;
  delete [] coefficients;
}

void JPEGImage::readCoefficients(void) {
  coeffs = jpeg_read_coefficients(decomp);
}

JBLOCKARRAY JPEGImage::getCoefficients(const JPEGComponent *comp) const {
  return getCoefficients(comp->getIndex());
}

JBLOCKARRAY JPEGImage::getCoefficients(int component_index) const {
  jpeg_component_info *info = decomp->comp_info + component_index;
  int rows = info->height_in_blocks;
  int i;
  JBLOCKARRAY retval = new JBLOCKROW[rows];
  for(i = 0; i < rows; ++i) {
    JBLOCKARRAY arr = (*decomp->mem->access_virt_barray)
      ((j_common_ptr) decomp, coeffs[component_index], 0, 1, 1);
    retval[i] = arr[0];
  }
  coefficients[component_index] = retval;
  return retval;
}

JPEGImage* JPEGImage::writeNew() {
  JOCTET *output = NULL;
  long outlen = len;
  steg_dest_mgr_for(comp, &output, &outlen);
  jpeg_copy_critical_parameters(decomp, comp);
  comp->in_color_space = decomp->out_color_space;
  jpeg_write_coefficients(comp, coeffs);
  jpeg_finish_compress(comp);
  jpeg_finish_decompress(decomp);
  return new JPEGImage(decomp, comp, output, outlen);
}

JPEGImage* JPEGImage::doCrop(int x_off, int y_off) {
  JOCTET *output = NULL;
  long outlen = len;
  jpeg_copy_critical_parameters(decomp, comp);
  comp->in_color_space = decomp->out_color_space;
  comp->image_width = decomp->image_width - x_off;
  comp->image_height = decomp->image_height - y_off;
  steg_dest_mgr_for(comp, &output, &outlen);
  jpeg_start_compress(comp, 1);
  jpeg_start_decompress(decomp);
  crop(decomp, comp, x_off, y_off);
  jpeg_finish_decompress(decomp);
  jpeg_finish_compress(comp);
  return new JPEGImage(decomp, comp, output, outlen);
}

JPEGComponent* JPEGImage::getComponent(int index) {
  jpeg_component_info *info = decomp->comp_info + index;
  if(components[index] == NULL) {
    components[index] = new JPEGComponent(info, this);
  }
  return components[index];
}

JOCTET* JPEGImage::getData(void) {
  return this->image;
}
