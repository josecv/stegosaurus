
#include "test_with_image.h"
#include "gtest/gtest.h"

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
};

TEST_F(JPEGBlockinessTest, testReciprocalROB) {
  double blockiness = testImage->calculateReciprocalROB();
  double steg_blockiness = stego->calculateReciprocalROB();
  EXPECT_GE(blockiness, steg_blockiness);
}
