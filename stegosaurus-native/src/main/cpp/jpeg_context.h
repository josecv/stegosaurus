/**
 * Defines a class that encapsulates creation and destruction of libjpeg
 * objects.
 */
#ifndef STEGOSAURUS_JPEG_CONTEXT
#define STEGOSAURUS_JPEG_CONTEXT

#include "jpeg_image.h"

/**
 * Creates and destroys the libjpeg compressors and decompressors. In addition,
 * acts as a factory for JPEGImages and any other class requiring those
 * libjpeg objects.
 */
class JPEGContext {
 public:
  /**
   * CTOR.
   */
  JPEGContext();

  /**
   * Create and return a new JPEG image.
   * @param i the image data.
   * @param len the length of the data buffer.
   * @return a pointer to the new image object.
   */
  JPEGImage* buildImage(JOCTET *i, long len);

  /**
   * Destroy the image given. You should do this to every JPEGImage, whether
   * built by this class or by another JPEGImage.
   * @param image a pointer to the image to destroy.
   */
  void destroyImage(JPEGImage* image);

  /**
   * Destructor.
   */
  ~JPEGContext();
};

#endif
