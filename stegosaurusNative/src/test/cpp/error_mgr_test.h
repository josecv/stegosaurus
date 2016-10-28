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
#ifndef STEG_ERROR_MGR_TEST
#define STEG_ERROR_MGR_TEST

#include "gtest/gtest.h"
#include "../../main/cpp/jpeg_lib_exception.h"
#include "../../main/cpp/stegosaurus_error_manager.h"
#include "test_with_image.h"

/**
 * Tests the stegosaurus error manager.
 */
class ErrorMgrTest : public ::testing::Test {
 public:
  /**
   * Set up the test by building the image decompressor.
   */
  virtual void SetUp(void) {
    cinfo = (j_decompress_ptr) new struct jpeg_decompress_struct;
    cinfo->err = stegosaurus_error_mgr(&error_manager);
    jpeg_create_decompress(cinfo);
    infile = fopen(TestWithImage::filename, "rb");
    jpeg_stdio_src(cinfo, infile);
  }

  /**
   * Tear down the test.
   */
  virtual void TearDown(void) {
    jpeg_destroy_decompress(cinfo);
    fclose(infile);
    delete cinfo;
  }

 protected:
  /**
   * The decompression object we're using.
   */
  j_decompress_ptr cinfo;

  /**
   * The error manager under test.
   */
  struct jpeg_error_mgr error_manager;

 private:
  /**
   * The file we're reading from.
   */
  FILE *infile;
};

/**
 * Test that a JPEGLibException is thrown when an illegal operation is
 * attempted on a libjpeg object.
 */
TEST_F(ErrorMgrTest, testExceptionThrown) {
  try {
    jpeg_read_header(cinfo, 1);
    /* We can't finish decompress before starting it, so this should throw. */
    jpeg_finish_decompress(cinfo);
    FAIL() << "No exception thrown";
  } catch(JPEGLibException &e) {
    EXPECT_STREQ("Improper call to JPEG library in state 202", e.what());
  }
}

#endif
