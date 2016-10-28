/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
   * @return the image, as a JPEGImage object (a pointer to one)
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

const char* TestWithImage::filename = "resources/lena-colour.jpeg";

#endif
