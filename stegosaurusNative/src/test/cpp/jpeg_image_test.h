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
#ifndef STEG_JPEG_IMAGE_TEST
#define STEG_JPEG_IMAGE_TEST

#include "gtest/gtest.h"
#include "test_with_image.h"
#include "../../main/cpp/jpeg_image.h"
#include "../../main/cpp/coefficient_accessor.h"
#include "../../main/c/steg_utils.h"
#include "../../main/c/src_mgr.h"


/**
 * Fixture used to run tests on the JPEGImage class.
 */
class JPEGImageTest : public TestWithImage {
 public:
  /**
   * Set up the test.
   */
  virtual void SetUp(void) {
    TestWithImage::SetUp();
  }

  /**
   * Tear down the test.
   */
  virtual void TearDown(void) {
    TestWithImage::TearDown();
  }
};

/**
 * Test the writeNew method.
 * Done by requesting a bunch of coefficients, turning them all into zeroes,
 * writing to a new image, and seeing if that works.
 */
TEST_F(JPEGImageTest, testWriteNew) {
  int i, j;
  JPEGImage *other;
  for(i = 0; i < 3; ++i) {
    JBLOCKARRAY arr = testImage->getCoefficients(i);
    for(j = 0; j < 64; ++j) {
      arr[0][0][j] = 0;
    }
  }
  other = testImage->writeNew();
  for(i = 0; i < 3; ++i) {
    JBLOCKARRAY arr = other->getCoefficients(i);
    for(j = 0; j < 64; ++j) {
      EXPECT_EQ(0, arr[0][0][j]) << "Index " << j;
    }
  }
  delete other;
}

/**
 * Test that the writeNew method can be used to copy an image.
 */
TEST_F(JPEGImageTest, testCopy) {
  JPEGImage *copyImage = testImage->writeNew();
  CoefficientAccessor *test_acc = testImage->getCoefficientAccessor(),
                      *copy_acc = copyImage->getCoefficientAccessor();
  unsigned int i;
  EXPECT_EQ(test_acc->getLength(), copy_acc->getLength());
  for(i = 0; i < test_acc->getLength(); ++i) {
    EXPECT_EQ(test_acc->getCoefficient(i), copy_acc->getCoefficient(i));
  }
  delete copyImage;
}

/**
 * Test building a coefficient accessor from an image.
 * The coefficient accessor class has its own tests, so we merely ensure
 * that it's been built properly and that all three components are accessible.
 */
TEST_F(JPEGImageTest, testAccessorFromImage) {
  CoefficientAccessor *acc = testImage->getCoefficientAccessor();
  int off = 0, i;
  for(i = 0; i < testImage->getComponentCount(); ++i) {
    JPEGComponent *c = testImage->getComponent(i);
    EXPECT_EQ(c->getCoefficients()[0][0][0], acc->getCoefficient(off))
      << "Offset " << off;
    off += c->getDownsampledWidth() * c->getDownsampledHeight();
  }
  ASSERT_EQ(off, acc->getLength());
}

/**
 * Test that accessing coefficients in JBLOCKARRAYS works as expected.
 * This is equivalent to ensuring that the getCoefficient method is not
 * doing anything fishy to the data it receives from libjpeg.
 */
TEST_F(JPEGImageTest, testCoefficientAccess) {
  int c, r, col, i;
  for(c = 0; c < testImage->getComponentCount(); ++c) {
    JBLOCKARRAY arr = testImage->getCoefficients(c);
    for(r = 0; r < 3; ++r) {
      for(col = 0; col < 3; ++col) {
        for(i = 0; i < 3; ++i) {
          /* Just a pretty generic operation; should ensure a decent
           * enough difference between coefficients. */
          arr[r][col][i] = (c * r * col) + i;
        }
      }
    }
  }
  for(c = 0; c < testImage->getComponentCount(); ++c) {
    JBLOCKARRAY arr = testImage->getCoefficients(c);
    for(r = 0; r < 3; ++r) {
      for(col = 0; col < 3; ++col) {
        for(i = 0; i < 3; ++i) {
          EXPECT_EQ(arr[r][col][i], (c * r * col) + i);
        }
      }
    }
  }
}

/* The following are "life tests"; in other words, they don't do many
 * assertions, and those that are performed are of tangential importance.
 * Instead, if they fail, the whole program crashes.
 * You'll be surprised to hear that the policy is to ensure that they always
 * pass.
 */

/**
 * Try building two coefficient accessors from this image, and ensure they all
 * provide access to coefficients equally.
 * This is one of those tests that was motivated by actual breakage,
 * specifically when a reset() is performed that should not have been.
 * For instance, when we've got some coefficients hanging about in an accessor,
 * and the creation of another accessor induces a reset(), we try to access
 * both (not even necessarily simultaneously).
 * TODO This test makes no sense now, at least not the way it's written.
 */
TEST_F(JPEGImageTest, testManyAccessors) {
  unsigned int i;
  CoefficientAccessor* first = testImage->getCoefficientAccessor();
  /* This will force coefficient access */
  for(i = 0; i < first->getLength(); ++i) {
    first->getCoefficient(i);
  }
  CoefficientAccessor* second = testImage->getCoefficientAccessor();
  for(i = 0; i < first->getLength(); ++i) {
    EXPECT_EQ(first->getCoefficient(i), second->getCoefficient(i));
  }
}

#endif
