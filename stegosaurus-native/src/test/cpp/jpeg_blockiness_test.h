#include "test_with_image.h"
#include "gtest/gtest.h"
#include "../../main/c/blockiness.h"

/**
 * The stego file we'll be testing with.
 * TODO This is a terrible way to keep the filename stored.
 */
static const char* steg_filename =
  "stegosaurus-native/src/test/resources/cpp/lena-stego.jpeg";

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
    context->destroyImage(stego);
    TestWithImage::TearDown();
  }

 protected:
  /**
   * The stego image having received a message from outguess.
   */
  JPEGImage *stego;

  /**
   * The number of block boundaries to account for.
   */
  static const int number_of_boundaries = 14;

  /**
   * The sizes of the rows we'll deal with.
   */
  static const int row_size = 64;

  /**
   * Populate the 64-byte long JSAMPROW given with random values. Fill in
   * the boundaries array with any values at the block boundaries (i.e. 7
   * and 8, 15 and 16, etc).
   * @param row the row to populte. Must be 64 bytes long.
   * @param boundaries the values at the boundaries. Must be of size 14.
   */
  void populateSampRow(JSAMPROW row, int boundaries[number_of_boundaries]) {
    int i, j = 0;
    for(i = 0; i < row_size; ++i) {
      row[i] = rand() % 100;
      /* TODO Maybe don't hardcode these values...
       * The reason these are hardcoded right now is that the size is known,
       * and this way we are 100% sure that we'll nab the right values,
       * without having to worry that a specific algorithm will work.
       * Of course, this is equivalent to saying if(i && !(i % 8)) and then
       * getting row[i] and row[i - 1].
       */
      if(i == 7  || i == 8  || i == 15 || i == 16 || i == 23 || i == 24 ||
         i == 31 || i == 32 || i == 39 || i == 40 || i == 47 || i == 48 ||
         i == 55 || i == 56) {
        boundaries[j] = row[i];
        ++j;
      }
    }
  }
};

/**
 * Test the blockinessForRow function for a single row.
 */
TEST_F(JPEGBlockinessTest, testBlockinessSingleRow) {
  JSAMPROW row = new JSAMPLE[row_size];
  int boundaries[number_of_boundaries];
  int result, i, expected = 0;
  populateSampRow(row, boundaries);
  for(i = 0; i < number_of_boundaries; i += 2) {
    expected += abs(boundaries[i] - boundaries[i + 1]);
  }
  result = blockinessForRow(1, row_size, row, 0, NULL);
  ASSERT_EQ(expected, result);
  delete [] row;
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
