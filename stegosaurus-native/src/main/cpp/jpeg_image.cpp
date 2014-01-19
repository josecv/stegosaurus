#include "jpeg_image.h"
#include "../c/dest_mgr.h"
#include "../c/src_mgr.h"
#include "../c/crop.h"
#include "../c/blockiness.h"
#include "stegosaurus_error_manager.h"
#include <string.h>
#include <assert.h>


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
  JPEGImage *r = new JPEGImage(output, outlen);
  r->getCoefficientAccessor()->cannibalizeUsables(getCoefficientAccessor());
  return r;
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
  jpeg_start_compress(comp, 0);
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

static int readNRows(int n, JSAMPARRAY buffer, j_decompress_ptr decomp) {
  int total = 0;
  int read;
  while(n && (decomp->output_scanline < decomp->output_height)) {
    read = jpeg_read_scanlines(decomp, &(buffer[total]), n);
    total += read;
    n -= read;
  }
  return total;
}

static void writeRows(JSAMPARRAY buffer, JSAMPARRAY buffer2, int row_count,
                      int samples_cropped, j_compress_ptr dest) {
  int i;
  for(i = 0; i < row_count; i++) {
    buffer2[i] = &(buffer[i][samples_cropped]);
  }
  jpeg_write_scanlines(dest, buffer2, row_count);
}

static int processRows(int components, int stride, JSAMPARRAY buffer,
                       JSAMPROW previous_row, int size_of_copy, int *rows_read,
                       j_decompress_ptr decomp,
                       blockinessCalcSafe blockinessCalculator) {
  *rows_read = readNRows(8, buffer, decomp);
  int retval = blockinessCalculator(components, stride, buffer, *rows_read,
                                    previous_row);
  if(*rows_read == 8) {
    memcpy(previous_row, buffer[7], size_of_copy);
  }
  return retval;
}

static int processRowsUnsafe(int components, int stride, JSAMPARRAY buffer,
                             JSAMPROW previous_row, int size_of_copy,
                             int *rows_read, j_decompress_ptr decomp,
                             blockinessCalcUnsafe blockinessCalculator) {
  *rows_read = readNRows(8, buffer, decomp);
  int retval = blockinessCalculator(components, stride, buffer, previous_row);
  memcpy(previous_row, buffer[7], size_of_copy);
  return retval;
}

static void chooseBlockinessCalc(int comp_count, blockinessCalcSafe *safe,
                                 blockinessCalcUnsafe *unsafe) {
  switch(comp_count) {
    case 3:
      *safe = &blockinessForRows3Comp;
      *unsafe = &blockinessForRows3CompUnsafe;
      break;
    case 1:
      *safe = &blockinessForRows1Comp;
      *unsafe = &blockinessForRows1CompUnsafe;
      break;
    default:
      *safe = &blockinessForRows;
      *unsafe = &blockinessForRowsUnsafe;
  }
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
  int read;
  JSAMPARRAY buffer;
  /* This second buffer merely exists to prevent memory re-allocation in
   * when cropping. It's messy, but it works */
  JSAMPARRAY buffer2 = new JSAMPROW[8];
  JSAMPROW   previous_row;
  const int row_stride = decomp->output_width * decomp->output_components;
  /* This is for the processRows calls that take place below */
  const int size_of_copy = row_stride * sizeof(JSAMPLE);
  previous_row = new JSAMPLE[row_stride];
  buffer = (*decomp->mem->alloc_sarray)
    ((j_common_ptr) decomp, JPOOL_IMAGE, row_stride, 8);
  /* The total number of samples to crop, from the left. */
  const int samples_cropped = off * decomp->output_components;
  blockinessCalcSafe safe;
  blockinessCalcUnsafe unsafe;
  chooseBlockinessCalc(decomp->output_components, &safe, &unsafe);
  /* We have to deal with the first 8 rows in a special manner, and we're
   * somewhat better served by hardcoding it than by placing it in the loop.
   */
  read = readNRows(8, buffer, decomp);
  value += safe(decomp->output_components, row_stride,
                buffer, read, NULL);
  if(read == 8) {
    memcpy(previous_row, buffer[7], size_of_copy);
  }
  if(comp) {
    int start = read - off;
    writeRows(&(buffer[start]), buffer2, read - start, samples_cropped, comp);
    while(decomp->output_scanline < (decomp->output_height - 8)) {
      value += processRowsUnsafe(decomp->output_components, row_stride,
                                 buffer, previous_row, size_of_copy, &read,
                                 decomp, unsafe);
      writeRows(buffer, buffer2, read, samples_cropped, comp);
    }
    value += processRows(decomp->output_components, row_stride, buffer,
                         previous_row, size_of_copy, &read, decomp,
                         safe);
    writeRows(buffer, buffer2, read, samples_cropped, comp);
  } else {
    while(decomp->output_scanline < (decomp->output_height - 8)) {
      value += processRowsUnsafe(decomp->output_components, row_stride, buffer,
                                 previous_row, size_of_copy, &read, decomp,
                                 unsafe);
    }
    value += processRows(decomp->output_components, row_stride, buffer,
                         previous_row, size_of_copy, &read, decomp, safe);
  }
  delete [] previous_row;
  delete [] buffer2;
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
  headers_read = false;
  free(output);
  steg_src_mgr_for(decomp, image, len);
  return cropped_blockiness / blockiness;
}
