#include "jpeg_image.h"
#include "../c/dest_mgr.h"
#include "../c/src_mgr.h"
#include "../c/crop.h"
#include "../c/blockiness.h"
#include "stegosaurus_error_manager.h"
#include <string.h>


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

JPEGImage* JPEGImage::writeNew() throw(JPEGLibException) {
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
  prepareCrop(&outlen, &output, x_off, y_off);
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

void JPEGImage::prepareCrop(long *outlen, JOCTET **output,
                            int x_off, int y_off) {
  jpeg_copy_critical_parameters(decomp, comp);
  comp->in_color_space = decomp->out_color_space;
  comp->image_width = decomp->image_width - x_off;
  comp->image_height = decomp->image_height - y_off;
  steg_dest_mgr_for(comp, output, outlen);
  jpeg_start_compress(comp, 1);
  jpeg_start_decompress(decomp);
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
  retval->err = stegosaurus_error_mgr(new struct jpeg_error_mgr);
  jpeg_create_decompress(retval);
  return retval;
}

j_compress_ptr JPEGImage::buildCompressor() {
  j_compress_ptr retval = (j_compress_ptr) new struct jpeg_compress_struct;
  retval->err = stegosaurus_error_mgr(new struct jpeg_error_mgr);
  jpeg_create_compress(retval);
  return retval;
}

/**
 * Calculate the spatial blockiness for the decompression object given.
 * If requested (decomp != NULL), crop it by 4 pixels, top and right, and
 * write them into the compression object given.
 */
static int calculateDecompBlockiness(j_decompress_ptr decomp,
    j_compress_ptr comp) {
  int value = 0;
  const int off = 4;
  int row = 0;
  JSAMPARRAY buffer;
  /* This second buffer merely exists to prevent memory re-allocation in
   * when cropping. It's messy, but it works */
  JSAMPARRAY buffer2 = new JSAMPROW;
  JSAMPROW   previous_row;
  const int row_stride = decomp->output_width * decomp->output_components;
  /* This is the num value for the memcpy that takes place below. */
  const int size_of_copy = row_stride * sizeof(JSAMPLE);
  previous_row = new JSAMPLE[row_stride];
  buffer = (*decomp->mem->alloc_sarray)
    ((j_common_ptr) decomp, JPOOL_IMAGE, row_stride, 1);
  /* The total number of samples to crop, from the left. */
  const int samples_cropped = off * decomp->output_components;
  while(decomp->output_scanline < decomp->output_height) {
    (void) jpeg_read_scanlines(decomp, buffer, 1);
    if(row >= off && comp != NULL) {
      buffer2[0] = &(buffer[0][samples_cropped]);
      (void) jpeg_write_scanlines(comp, buffer2, 1);
    }
    value += blockinessForRow(decomp->output_components, decomp->output_width,
                              buffer[0], row, previous_row);
    ++row;
    /* The previous_row won't be used unless we're at a vertical boundary,
     * so we don't copy it unless it's absolutely required. */
    if(!(row % 8)) {
      memcpy(previous_row, buffer[0], size_of_copy);
    }
  }
  delete [] previous_row;
  delete buffer2;
  return value;
}

double JPEGImage::calculateReciprocalROB(void) throw (JPEGLibException) {
  JOCTET *output = NULL;
  long outlen = len;
  reset();
  prepareCrop(&outlen, &output, 4, 4);
  double blockiness = calculateDecompBlockiness(decomp, comp);
  jpeg_finish_compress(comp);
  jpeg_finish_decompress(decomp);
  steg_src_mgr_for(decomp, output, outlen);
  jpeg_read_header(decomp, 1);
  jpeg_start_decompress(decomp);
  double cropped_blockiness = calculateDecompBlockiness(decomp, NULL);
  jpeg_finish_decompress(decomp);
  free(output);
  steg_src_mgr_for(decomp, image, len);
  return cropped_blockiness / blockiness;
}
