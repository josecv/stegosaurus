
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

/**
 * Test that the JPEGImage's calculateComponentBlockinessSum method works as
 * expected, and is indeed a sum of the other components' blockiness.
 */
TEST_F(JPEGBlockinessTest, testCalculateComponentBlockinessSum) {
  int expected = 0, i;
  int result = testImage->calculateComponentBlockinessSum();
  for(i = 0; i < testImage->getComponentCount(); ++i) {
    expected += testImage->getComponent(i)->calculateBlockiness();
  }
  ASSERT_EQ(expected, result);
}

/**
 * Try to calculate the blockiness for our image; then crop it and get the
 * ratio of cropped-to-original. Do the same for a stego image, and ensure
 * that the former is smaller than the latter.
 */
TEST_F(JPEGBlockinessTest, testCalculateBlockiness) {
  int i;
  double blockiness = 0, steg_blockiness = 0;

  blockiness = testImage->calculateComponentBlockinessSum();
  steg_blockiness = stego->calculateComponentBlockinessSum();
  EXPECT_LE(blockiness, steg_blockiness);

  JPEGImage *cropped = testImage->doCrop(4, 4);
  blockiness = cropped->calculateComponentBlockinessSum() / blockiness;
  context->destroyImage(cropped);
  cropped = stego->doCrop(4, 4);
  steg_blockiness = cropped->calculateComponentBlockinessSum() /
    steg_blockiness;
  context->destroyImage(cropped);
  EXPECT_LE(blockiness, steg_blockiness);
}

