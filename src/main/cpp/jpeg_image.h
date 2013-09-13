/**
 * jpeg_image.h: Specifies a class that can handle a single JPEG image.
 */
#ifndef STEGOSAURUS_JPEG_IMAGE
#define STEGOSAURUS_JPEG_IMAGE

#include <stdlib.h>
#include <stdio.h>
#include "jpeglib.h"
#include "jpeg_component.h"

/**
 * Represents a jpeg image in use by stegosaurus. Should be constructed by
 * a factory, or by other JPEG images.
 */
class JPEGImage : public JPEGCoefficientsProvider {
 public:
  /**
   * CTOR. Should only be invoked from a factory, or by another image.
   * Note that the image data will belong to this instance, which will free it.
   * @param d the decompression object.
   * @param c the compression object.
   * @param i a pointer to the image data.
   * @param imglen the size of the image data.
   */
  JPEGImage(j_decompress_ptr d, j_compress_ptr c, JOCTET *i, long imglen);

  /**
   * Destructor.
   */
  virtual ~JPEGImage();

  /**
   * Read the coefficients and store them for later retrieval through use
   * of the function getBlockArray.
   */
  void readCoefficients(void);

  /**
   * Write the current state of the jpeg coefficients to a new image, and
   * return it.
   * This is useful when, for example, embedding a message into said
   * coefficients.
   * @return the new image
   */
  JPEGImage* writeNew();

  /**
   * Crop this image to start at the offsets given, and return the new image.
   * @param x_off the offset from the left where the new image should start.
   * @param y_off the offset from the top where the new image should start.
   */
  JPEGImage* doCrop(int x_off, int y_off);

  /**
   * Get the total number of components in the image.
   * @return the number of components.
   */
  int getComponentCount() {
    return this->component_count;
  }

  /**
   * Get the component corresponding to a given index (its id - 1).
   * It belongs to this image, which will clean it up on destruction.
   * @param index the index.
   * @return the component.
   */
  JPEGComponent *getComponent(int index);

  /**
   * Get the DCT coefficients for the component given. Should get all of them.
   * @param comp the JPEGComponent
   * @return the coefficients.
   */
  virtual JBLOCKARRAY getCoefficients(const JPEGComponent *comp) const;

  /**
   * Get the raw image data.
   * @return the raw image data.
   */
  JOCTET *getData(void);

 private:
  /**
   * A decompression object.
   */
  j_decompress_ptr decomp;
  /**
   * A compression object.
   */
  j_compress_ptr comp;
  /**
   * The compressed image.
   */
  JOCTET *image;
  /**
   * The size of the buffer with the image.
   */
  long len;

  /**
   * The number of components in the image.
   */
  int component_count;

  /**
   * The coefficients.
   */
  jvirt_barray_ptr *coeffs;

  /**
   * The JPEGComponents of this image.
   */
  JPEGComponent **components;
};

#endif
