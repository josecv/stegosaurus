#include "jpeg_image.h"
#include "../c/dest_mgr.h"
#include "../c/src_mgr.h"
#include "../c/crop.h"


JPEGImage::JPEGImage(JOCTET *i, long imglen)
    : decomp(NULL),
      comp(NULL),
      image(i),
      len(imglen),
      coeffs(NULL),
      accessor(NULL),
      headers_read(true) {
  decomp = buildDecompressor();
  comp = buildCompressor();
  steg_src_mgr_for(decomp, image, imglen);
  /* TODO : Error checking */
  (void) jpeg_read_header(decomp, 1);
  component_count = decomp->num_components;
  components = new JPEGComponent*[component_count]();
  coefficients = new JBLOCKARRAY[component_count]();
}

/* TODO: Holy cow this method is long and ugly. */
JPEGImage::~JPEGImage() {
  int i;
  free(this->image);
  if(accessor != NULL) {
    delete accessor;
  }
  /* We obviously have to delete the components themselves before we free
   * the pointer array.
   * Since it's possible that not all of them were accessed, we check before
   * we free them.
   */
  for(i = 0; i < component_count; i++) {
    if(components[i] != NULL) {
      delete components[i];
    }
  }
  deleteCoefficients();
  delete [] components;
  delete [] coefficients;
  delete decomp->err;
  delete comp->err;
  jpeg_destroy_decompress(decomp);
  jpeg_destroy_compress(comp);
  delete decomp;
  delete comp;
}

void JPEGImage::readCoefficients(void) {
  if(coeffs == NULL) {
    reset();
    coeffs = jpeg_read_coefficients(decomp);
  }
}

JBLOCKARRAY JPEGImage::getCoefficients(const JPEGComponent *comp) {
  return getCoefficients(comp->getIndex());
}

JBLOCKARRAY JPEGImage::getCoefficients(int component_index) {
  readCoefficients();
  jpeg_component_info *info = decomp->comp_info + component_index;
  int rows = info->height_in_blocks;
  int i;
  if(coefficients[component_index] != NULL) {
    return coefficients[component_index];
  }
  JBLOCKARRAY retval = new JBLOCKROW[rows];
  for(i = 0; i < rows; ++i) {
    JBLOCKARRAY arr = (*decomp->mem->access_virt_barray)
      ((j_common_ptr) decomp, coeffs[component_index], i, 1, 1);
    retval[i] = arr[0];
  }
  coefficients[component_index] = retval;
  return retval;
}

JPEGImage* JPEGImage::writeNew() {
  readCoefficients();
  JOCTET *output = NULL;
  long outlen = len;
  steg_dest_mgr_for(comp, &output, &outlen);
  jpeg_copy_critical_parameters(decomp, comp);
  comp->in_color_space = decomp->out_color_space;
  jpeg_write_coefficients(comp, coeffs);
  jpeg_finish_compress(comp);
  reset();
  return new JPEGImage(output, outlen);
}

JPEGImage* JPEGImage::doCrop(int x_off, int y_off) {
  reset();
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
  jpeg_finish_compress(comp);
  jpeg_finish_decompress(decomp);
  headers_read = false;
  return new JPEGImage(output, outlen);
}

JPEGComponent* JPEGImage::getComponent(int index) {
  if(!headers_read) {
    reset();
  }
  jpeg_component_info *info = decomp->comp_info + index;
  if(components[index] == NULL) {
    components[index] = new JPEGComponent(info, this);
  }
  return components[index];
}

JOCTET* JPEGImage::getData(void) {
  return this->image;
}

CoefficientAccessor* JPEGImage::getCoefficientAccessor(void) {
  if(accessor == NULL) {
    int i;
    /* Ensure that the components have been realized */
    for(i = 0; i < component_count; ++i) {
      getComponent(i);
    }
    accessor = new CoefficientAccessor(components, component_count);
  }
  return accessor;
}

int JPEGImage::calculateComponentBlockinessSum(void) {
  int retval = 0, i;
  for(i = 0; i < component_count; ++i) {
    retval += getComponent(i)->calculateBlockiness();
  }
  return retval;
}

void JPEGImage::reset(void) {
  int i;
  if(coeffs != NULL) {
    coeffs = NULL;
    deleteCoefficients();
    for(i = 0; i < component_count; ++i) {
      if(components[i] != NULL) {
        components[i]->forceCoefReloadOnNextAccess();
      }
    }
    jpeg_finish_decompress(decomp);
    jpeg_read_header(decomp, 1);
  } else if(!headers_read) {
    jpeg_read_header(decomp, 1);
  }
  headers_read = true;
}

void JPEGImage::deleteCoefficients(void) {
  int i;
  for(i = 0; i < component_count; ++i) {
    if(coefficients[i] != NULL) {
      delete [] coefficients[i];
      coefficients[i] = NULL;
    }
  }
}

j_decompress_ptr JPEGImage::buildDecompressor() {
  j_decompress_ptr retval =
    (j_decompress_ptr) new struct jpeg_decompress_struct;
  retval->err = jpeg_std_error(new struct jpeg_error_mgr);
  jpeg_create_decompress(retval);
  return retval;
}

j_compress_ptr JPEGImage::buildCompressor() {
  j_compress_ptr retval = (j_compress_ptr) new struct jpeg_compress_struct;
  retval->err = jpeg_std_error(new struct jpeg_error_mgr);
  jpeg_create_compress(retval);
  return retval;
}
