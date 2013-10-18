
#include "test_with_image.h"
#include "gtest/gtest.h"

/**
 * The stego file we'll be testing with.
 * TODO This is a terrible way to keep the filename stored.
 */
static const char* steg_filename =
  "stegosaurus-native/src/test/resources/cpp/lena-stego.jpeg";

/**
 * Test the JPEGComponent class' calculateBlockiness method.
 * The strategy involves comparing the lena-colour picture (via the
 * TestWithImage class) to another, lena-stego.jpeg.
 * The latter image is the end result of embedding the US declaration of
 * independence into the first image, using the stegosaurus java component,
 * and the dummy sequence declared in PM1Test.java.
 */
class JPEGComponentBlockinessTest : public TestWithImage {
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
 * Try to calculate the blockiness for our image; then crop it and get the
 * ratio of cropped-to-original. Do the same for a stego image, and ensure
 * that the former is smaller than the latter.
 */
TEST_F(JPEGComponentBlockinessTest, testCalculateBlockiness) {
  int i;
  double blockiness = 0, steg_blockiness = 0;
  int cropped_blockiness = 0;

  JPEGImage *cropped = testImage->doCrop(4, 4);
  for(i = 0; i < testImage->getComponentCount(); ++i) {
    blockiness += testImage->getComponent(i)->calculateBlockiness();
    cropped_blockiness += cropped->getComponent(i)->calculateBlockiness();
  }
  context->destroyImage(cropped);
  blockiness = cropped_blockiness / blockiness;

  cropped = stego->doCrop(4, 4);
  cropped_blockiness = 0;
  for(i = 0; i < stego->getComponentCount(); ++i) {
    steg_blockiness += stego->getComponent(i)->calculateBlockiness();
    cropped_blockiness += cropped->getComponent(i)->calculateBlockiness();
  }
  steg_blockiness = cropped_blockiness / steg_blockiness;
  context->destroyImage(cropped);
  EXPECT_LE(blockiness, steg_blockiness);
}

