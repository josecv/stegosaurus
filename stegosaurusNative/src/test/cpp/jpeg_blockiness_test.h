#include "test_with_image.h"
#include "gtest/gtest.h"
#include "../../main/c/blockiness.h"
#include <math.h>

/**
 * The stego file we'll be testing with.
 * TODO This is a terrible way to keep the filename stored.
 */
static const char* steg_filename = "resources/lena-stego.jpeg";

/**
 * Test the blockiness calculation methods.
 * This includes the JPEGComponent's calculateBlockiness, and the JPEGImage's
 * own version thereof.
 * The strategy involves comparing the lena-colour picture (via the
 * TestWithImage class) to another, lena-stego.jpeg.
 * The latter image is the end result of embedding the US declaration of
 * independence into the first image, using the stegosaurus java component,
 * and the dummy sequence declared in PM1Test.java.
 */
class JPEGBlockinessTest : public TestWithImage {
 public:
  /**
   * Set up the test.
   */
  virtual void SetUp(void) {
    TestWithImage::SetUp();
    stego = readPath(steg_filename);
    srand(time(NULL));
  }

  /**
   * Tear down the test.
   */
  virtual void TearDown(void) {
    delete stego;
    TestWithImage::TearDown();
  }

 protected:
  /**
   * The stego image having received a message from outguess.
   */
  JPEGImage *stego;

  /**
   * Populate the 64-byte long JSAMPROW given with random values.
   * Sample the values at indices present in the sample_indices array, and
   * place them in the samples array.
   * @param row the row to populte. Must be size bytes long.
   * @param size the size of the row to populate.
   */
  void populateSampRow(JSAMPROW row, int size) {
    int i;
    for(i = 0; i < size; ++i) {
      row[i] = rand() % 100;
    }
  }
};

/**
 * Test the blockinessForRow function for a single row and a single
 * component.
 */
TEST_F(JPEGBlockinessTest, testBlockinessSingleRow) {
  const int row_size = 64;
  const int number_of_boundaries = 14;
  JSAMPROW row = new JSAMPLE[row_size];
  int boundary_indices[number_of_boundaries] = {
    7, 8, 15, 16, 23, 24, 31, 32, 39, 40, 47, 48, 55, 56
  };
  int result, i, expected = 0;
  populateSampRow(row, row_size);
  for(i = 0; i < number_of_boundaries; i += 2) {
    int index = boundary_indices[i];
    int other = boundary_indices[i + 1];
    expected += abs(row[index] - row[other]);
  }
  result = blockinessForRow(1, row_size, row, 0, NULL);
  ASSERT_EQ(expected, result);
  delete [] row;
}


/**
 * Test the blockinessForRow function with a single row and three components.
 */
TEST_F(JPEGBlockinessTest, testBlockinessMultipleComponents) {
  const int components = 3;
  const int row_size = 64 * components;
  const int number_of_boundaries = 14 * components;
  int i, c, result;
  int expected = 0;
  JSAMPROW row = new JSAMPLE[row_size];
  int boundary_indices[number_of_boundaries] = {
    21,  22,  23,  24,  25,  26,  45,  46,  47,  48,  49,  50,  69,  70,  71,
    72,  73,  74,  93,  94,  95,  96,  97,  98,  117, 118, 119, 120, 121, 122,
    141, 142, 143, 144, 145, 146, 165, 166, 167, 168, 169, 170
  };
  populateSampRow(row, row_size);
  for(i = 0; i < number_of_boundaries / components; i += 2) {
    for(c = 0; c < components; ++c) {
      int index = boundary_indices[(i * 3) + c];
      int other_index = boundary_indices[((i + 1) * 3) + c];
      expected += abs(row[index] - row[other_index]);
    }
  }
  result = blockinessForRow(components, row_size / components, row, 0, NULL);
  EXPECT_EQ(expected, result);
  delete [] row;
}

/**
 * Test the blockinessForRow function with multiple rows.
 * Test both with a single component, and three components.
 */
TEST_F(JPEGBlockinessTest, testBlockinessMultiRow) {
  int components;
  const int row_size = 64;
  for(components = 1; components <= 3; components += 2) {
    int i;
    int expected, result;
    JSAMPROW prev = new JSAMPLE[row_size * components];
    JSAMPROW row = new JSAMPLE[row_size * components];
    populateSampRow(prev, row_size * components);
    populateSampRow(row, row_size * components);
    /* First ensure that no monkey business takes place when a previous row
     * is given, but the current row is not a block boundary.
     */
    expected = blockinessForRow(components, row_size, row, 0, NULL);
    for(i = 1; i < 8; ++i) {
      result = blockinessForRow(components, row_size, row, i, prev);
      EXPECT_EQ(expected, result)
        << "Failure for " << components << " components";
    }
    /* Now ensure that everything works out when the current row _is_
     * a block boundary.
     */
    for(i = 0; i < row_size * components; ++i) {
      expected += abs(row[i] - prev[i]);
    }
    result = blockinessForRow(components, row_size, row, 8, prev);
    EXPECT_EQ(expected, result)
      << "Failure for " << components << " components";
    delete [] row;
    delete [] prev;
  }
}

/**
 * Ensure that the reciprocalROB function works as expected: this is verified
 * by ensuring that the reciprocalROB of an image is a larger number than that
 * of the same image containing a steganographic payload.
 */
TEST_F(JPEGBlockinessTest, testReciprocalROB) {
  double blockiness = testImage->calculateReciprocalROB();
  double steg_blockiness = stego->calculateReciprocalROB();
  EXPECT_LE(blockiness, 1.0);
  EXPECT_LE(steg_blockiness, 1.0);
  EXPECT_GE(blockiness, steg_blockiness);
}

/**
 * A rather odd test: ensures that the value returned by the reciprocalROB
 * remains the same (or within a tolerable interval) as the one calculated
 * as of commit 32017c088770866a
 */
TEST_F(JPEGBlockinessTest, testConsistency) {
  const double permissible_distance = 0.001;
  const double expected = 0.972323;
  double result = testImage->calculateReciprocalROB();
  double distance = fabs(result - expected);
  EXPECT_LE(distance, permissible_distance) << "Expected ROB of "
    << expected << " got " << result;
}
