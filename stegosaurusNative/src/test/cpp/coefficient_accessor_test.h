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
#ifndef STEG_COEFFICIENT_ACCESSOR_TEST
#define STEG_COEFFICIENT_ACCESSOR_TEST

#include "../../main/cpp/coefficient_accessor.h"
#include "../../main/cpp/dummy_coefficients_provider.h"
#include "gtest/gtest.h"
#include <stdlib.h>
#include <time.h>

/**
 * Tests the coefficient accessor class. Creates some dummy JPEG components
 * and their DCT coefficients.
 * It's possible to use up to three components, with the third one
 * being twice as wide and twice as high as the preceding two, to simulate
 * subsampling.
 */
class CoefficientAccessorTest : public ::testing::TestWithParam<int> {
 public:
  /**
   * Set up the test. This means constructing some random block arrays
   * corresponding to every component, sampling their values according to
   * the tests array, setting the components and their providers up, and then
   * constructing the actual CoefficientAccessor.
   */
  virtual void SetUp(void) {
    int i;
    compCount = GetParam();
    for(i = 0; i < compCount; ++i) {
      int factor = (i == 2 ? 2 : 1);
      rowInfo[i] = rowCount * factor;
      colInfo[i] = colCount * factor;
    }
    blockArrays = createRandomBlockArrays(rowInfo, colInfo);
    for(i = 0; i < compCount; ++i) {
      p[i] = new DummyCoefficientsProvider(blockArrays[i]);
      components[i] = new JPEGComponent(colInfo[i], rowInfo[i], colInfo[i] * 8,
        rowInfo[i] * 8, i, p[i]);
    }
    acc = new CoefficientAccessor(components, compCount);
  }

  /**
   * Delete the memory allocated by this fixture.
   */
  virtual void TearDown(void) {
    int row, i;
    delete acc;
    for(i = 0; i < compCount; i++) {
      delete components[i];
      delete p[i];
    }
    for(i = 0; i < compCount; ++i) {
      for(row = 0; row < rowInfo[i]; ++row) {
        delete [] (blockArrays[i][row]);
      }
      delete [] blockArrays[i];
    }
    delete [] blockArrays;
  }

 protected:
  /**
   * The actual coefficient accessor object.
   */
  CoefficientAccessor *acc;
  /**
   * The number of rows of blocks.
   */
  static const int rowCount = 16;

  /**
   * The number of columns of blocks.
   */
  static const int colCount = 16;

  /**
   * An array of row counts corresponding to the components.
   */
  int rowInfo[3];

  /**
   * An array of column counts corresponding to the components.
   */
  int colInfo[3];

  /**
   * The total number of components in this image.
   */
  int compCount;

  /**
   * The expected length reported by the accessor.
   */
  unsigned int expectedLength;

  /**
   * The array of block arrays.
   */
  JBLOCKARRAY *blockArrays;

  /**
   * The number of values that will be tested.
   */
  static const int sampleSize = 25;

  /**
   * This sorted array contains the indices that will be sampled for use in
   * testing.
   */
  static const unsigned int tests[25];

  /**
   * The sampled values for the indices corresponding to the test array.
   */
  JCOEF values[25];

 private:
  /**
   * The coefficient providers for the generated coefficients.
   */
  DummyCoefficientsProvider *p[3];

  /**
   * The component objects for the generated coefficients.
   */
  JPEGComponent *components[3];

  /**
   * Create a number of block arrays with random coefficients.
   * @param rows array where rows[n] = number of rows in the nth block array
   * @param cols array where cols[n] = number of cols in the nth block array
   */
  JBLOCKARRAY *createRandomBlockArrays(int *rows, int *cols) {
    JBLOCKARRAY *retval = new JBLOCKARRAY[compCount];
    int n;
    const int size = 64;
    int row, col, i, j = 0;
    unsigned int c = 0;
    srand(time(NULL));
    for(n = 0; n < compCount; ++n) {
      retval[n] = new JBLOCKROW[rows[n]];
      JBLOCKARRAY arr = retval[n];
      for(row = 0; row < rows[n]; ++row) {
        arr[row] = new JBLOCK[cols[n]];
        for(col = 0; col < cols[n]; ++col) {
          for(i = 0; i < size; ++i, ++c) {
            JCOEF val = (rand() % 65534) - 32767;
            if(tests[j] == c && j < sampleSize) {
              values[j] = val;
              ++j;
            }
            arr[row][col][i] = val;
          }
        }
      }
    }
    expectedLength = c;
    return retval;
  }
};

/**
 * This array includes 8 special cases:
 *
 *  - The very first one (0)
 *  - The first one in the second column (64)
 *  - The first one in the second row (1024)
 *  - The last one in the first component (16383)
 *  - The first coef in the second component (16384)
 *  - The last coef in the second component (32767)
 *  - The first coef in the third component (32768)
 *  - The last coef altogether (98303)
 * The rest of them were randomly generated by Python, with a particularly
 * large concentration in the first component.
 */
const unsigned int CoefficientAccessorTest::tests[25] = {
    0, 64, 1024, 1194, 2132, 12634, 12849, 13320, 15244, 16383, 16384, 17399,
    22661, 23006, 28042, 29460, 29624, 32767, 32768, 51230, 65105, 77865,
    90453, 95958, 98303
  };

/**
 * Test with different numbers of randomly generated components.
 */
TEST_P(CoefficientAccessorTest, TestAccess) {
  int i;
  EXPECT_EQ(expectedLength, acc->getLength());
  for(i = 0; i < sampleSize; i++) {
    unsigned int index = tests[i];
    if(index < expectedLength) {
      EXPECT_EQ(values[i], acc->getCoefficient(index)) << "Index " << index;
    }
  }
}

/**
 * Test the isDC coefficient.
 */
TEST_P(CoefficientAccessorTest, TestIsDC) {
  int i;
  const int isDCVal[25] = {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
                           0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
  for(i = 0; i < sampleSize; i++) {
    unsigned int index = tests[i];
    if(index < expectedLength) {
      EXPECT_EQ(isDCVal[i], acc->isDC(index)) << "Index " << index;
    }
  }
}

/**
 * Test the setCoefficient method.
 */
TEST_P(CoefficientAccessorTest, TestSetCoefficient) {
  int i;
  for(i = 0; i < sampleSize; i++) {
    unsigned int index = tests[i];
    if(index < expectedLength) {
      /* There's really nothing special about the new value, but it's a
       * reasonable, safe way to get a different value while avoiding any
       * kind of overflow
       */
      int newVal = values[i] ^ 1;
      acc->setCoefficient(index, newVal);
      EXPECT_EQ(newVal, acc->getCoefficient(index)) << "Index " << index;
    }
  }
}

/**
 * Test the getUsableCoefficients method.
 */
TEST_P(CoefficientAccessorTest, TestGetUsableCoefficients) {
  int *usables = acc->getUsableCoefficients();
  unsigned int i;
  int j = 0;
  for(i = 0; i < acc->getLength(); ++i) {
    if(!acc->isDC(i) && acc->getCoefficient(i)) {
      EXPECT_EQ(i, usables[j]);
      ++j;
    }
  }
  EXPECT_EQ(acc->getUsableCoefficientCount(), j);
}

INSTANTIATE_TEST_CASE_P(CoefficientInstantiation, CoefficientAccessorTest,
  ::testing::Values(1, 3));

#endif
