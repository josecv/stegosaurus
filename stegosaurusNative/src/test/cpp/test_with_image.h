#ifndef STEG_TEST_WITH_IMAGE
#define STEG_TEST_WITH_IMAGE

#include "gtest/gtest.h"
#include "../../main/cpp/jpeg_image.h"
#include "../../main/cpp/coefficient_accessor.h"
#include "../../main/c/steg_utils.h"
#include "../../main/c/src_mgr.h"

/**
 * A base class for C++ tests that need access to a JPEGImage.
 * Any test class that'll make use of a test image should derive from
 * this one.
 * Any classes that make use of it should invoke its SetUp and TearDown methods
 * in their own SetUp and TearDown.
 */
class TestWithImage : public ::testing::Test {
 public:
  /**
   * Set up the test by constructing our image.
   */
  virtual void SetUp() {
    testImage = readPath(filename);
  }

  /**
   * Tear down the test by smashing the image and whatnot.
   */
  virtual void TearDown() {
    delete testImage;
  }

  /**
   * The name of the image file used for testing
   */
  static const char* filename;

 protected:

  /**
   * Read in the image at the path given, and return it.
   * @param path the path to the image file to read.
   * @return the image, as a (gasp) JPEGImage object (a pointer to one)
   */
  JPEGImage *readPath(const char* path) {
    JOCTET *imgbuf = NULL;
    long imglen = 0;
    FILE *reffile;
    /* TODO Some sort of error handling here */
    reffile = fopen(path, "rb");
    read_file(&imgbuf, &imglen, reffile);
    fclose(reffile);
    return new JPEGImage(imgbuf, imglen);
  }

  /**
   * The JPEGImage object under test.
   */
  JPEGImage *testImage;
};

const char* TestWithImage::filename =
  "stegosaurus-native/src/test/resources/cpp/lena-colour.jpeg";

#endif
