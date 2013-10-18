/**
 * jpeg_image.h: Specifies a class that can handle a single JPEG image.
 */
#ifndef STEGOSAURUS_JPEG_IMAGE
#define STEGOSAURUS_JPEG_IMAGE

#include <stdlib.h>
#include <stdio.h>
#include "jpeglib.h"
#include "jpeg_component.h"
#include "coefficient_accessor.h"

/**
 * Represents a jpeg image in use by stegosaurus. Should be constructed by
 * a factory, or by other JPEG images.
 * Note that this object is not thread safe! It is best to confine a JPEGImage
 * to a single thread.
 * You may also notice that this object is awfully stateful (hence its lack of
 * thread safety).
 * Sadly, that's because of implementation concerns and you, the user, pay
 * for the broken pot. An attempt is made to hide this fact, but you should
 * be aware of some caveats:
 *  - The lack of thread safety.
 *  - Cropping an image will cause its coefficients to be thrown away. They'll
 *    be read automatically when next needed, but this can be a waste of time.
 *    Consider not cropping the image until the last moment.
 *
 * TODO Better document the stateful nature of this object.
 * TODO All around refactoring and clean-up; this class is too fragile.
 * TODO A SERIOUS review of error handling practices.
 */
class JPEGImage : public JPEGCoefficientsProvider {
 public:
  /**
   * CTOR. Should only be invoked from a factory, or by another image.
   * Note that the image data will belong to this instance, which will free it.
   * @param i a pointer to the image data.
   * @param imglen the size of the image data.
   */
  JPEGImage(JOCTET *i, long imglen);

  /**
   * Destructor.
   */
  virtual ~JPEGImage();

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
  virtual JBLOCKARRAY getCoefficients(const JPEGComponent *comp);

  /**
   * Get the DCT coefficients for the component with the index given.
   * Should get them all
   * @param component_index the index of the component
   * @return the coefficients.
   */
  JBLOCKARRAY getCoefficients(int component_index);

  /**
   * Get the raw image data.
   * @return the raw image data.
   */
  JOCTET *getData(void);

  /**
   * Get the total size of the raw image data.
   * @return the size.
   */
  long getDataLen(void) {
    return this->len;
  }

  /**
   * Get a coefficient accessor providing access to this image's DCT
   * coefficients.
   *
   * @return the accessor.
   */
  CoefficientAccessor* getCoefficientAccessor(void);

 private:
  /**
   * Request the DCT coefficients from libjpeg, and store a pointer to them
   * for later retrieval.
   * Note that they're not actually _accessed_ or loaded into memory.
   */
  void readCoefficients(void);

  /**
   * Reset the state of this image object, allowing it to be used differently.
   */
  void reset(void);

  /**
   * Delete all the JBLOCKARRAYs that have been requested from this image.
   */
  void deleteCoefficients(void);

  /**
   * Construct a jpeg_decompression_struct.
   * @return a pointer to the built structure.
   */
  static j_decompress_ptr buildDecompressor();

  /**
   * Construct a jpeg_compression_struct.
   * @return a pointer to the built structure.
   */
  static j_compress_ptr buildCompressor();

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

  /**
   * The DCT coefficients of this image.
   */
  JBLOCKARRAY *coefficients;

  /**
   * The coefficient accessor to provide access to this image's DCT
   * coefficients.
   */
  CoefficientAccessor *accessor;

  /**
   * Whether the headers have been read, by calling jpeg_read_headers on
   * the decompression object.
   * MUST be set to false after any calls to jpeg_finish_decompress.
   * Note that we have the info contained in those headers regardless of
   * whether or not they actually have been read.
   */
  bool headers_read;
};

#endif
