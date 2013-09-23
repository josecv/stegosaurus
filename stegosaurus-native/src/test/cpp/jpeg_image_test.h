#include "../../main/cpp/jpeg_context.h"
#include "../../main/cpp/jpeg_image.h"
#include "../../main/cpp/coefficient_accessor.h"
#include "../../main/c/steg_utils.h"
#include "../../main/c/src_mgr.h"

/**
 * The name of the image file used for testing
 * TODO THIS SUCKS!
 */
static const char* filename = "stegosaurus-native/src/test/resources/cpp/lena-colour.jpeg";

/**
 * Fixture used to run tests on the JPEGImage class.
 * This involves performing a ton of libjpeg operations, which is why
 * the fixture is somewhat verbose.
 */
class JPEGImageTest : public ::testing::Test {
 public:
  /**
   * Set up the test.
   */
  void SetUp(void) {
    context = new JPEGContext();
    imgbuf = NULL;
    imglen = 0;
    /* TODO Some sort of error handling here */
    reffile = fopen(filename, "rb");
    read_file(&imgbuf, &imglen, reffile);
    testImage = context->buildImage(imgbuf, imglen);
    /* We've got the buffer: we'll use it for the test image. Now we have
     * to rewind the file so it can be used by the reference decompression.
     */
    rewind(reffile);
    reference = (j_decompress_ptr) malloc(sizeof(struct jpeg_decompress_struct));
    reference->err = jpeg_std_error(&referr);
    jpeg_create_decompress(reference);
    jpeg_stdio_src(reference, reffile);
    (void) jpeg_read_header(reference, 1);
  }

  /**
   * Tear down the test.
   */
  void TearDown(void) {
    context->destroyImage(testImage);
    delete context;
    jpeg_destroy_decompress(reference);
    free(reference);
    fclose(reffile);
  }
 protected:
  /**
   * The JPEG Context, used to create images and whatnot.
   */
  JPEGContext *context;

  /**
   * The reference decompression object. Will represent the same image as
   * the tested JPEGImage.
   */
  j_decompress_ptr reference;

  /**
   * The buffer to be used by the image object under test.
   */
  JOCTET *imgbuf;

  /**
   * The size of the imgbuf buffer.
   */
  long imglen;

  /**
   * The JPEGImage object under test.
   */
  JPEGImage *testImage;
 private:
  /**
   * The JPEG error manager for the reference decompression object.
   */
  struct jpeg_error_mgr referr;

  /**
   * The reference file.
   */
  FILE *reffile;
};

/**
 * Test the crop method. Since it's a lossy crop, there is just about nothing
 * we can do to ensure that it's accurate other than making sure it doesn't
 * throw any exceptions.
 */
TEST_F(JPEGImageTest, testCrop) {
  JPEGImage *croppedImage = testImage->doCrop(4, 4);
  context->destroyImage(croppedImage);
}

/**
 * Test the writeNew method.
 * Done by requesting a bunch of coefficients, turning them all into zeroes,
 * writing to a new image, and seeing if that works.
 */
TEST_F(JPEGImageTest, testWriteNew) {
  int i, j;
  JPEGImage *other;
  testImage->readCoefficients();
  for(i = 0; i < 3; ++i) {
    JBLOCKARRAY arr = testImage->getCoefficients(i);
    for(j = 0; j < 64; ++j) {
      arr[0][0][j] = 0;
    }
  }
  other = testImage->writeNew();
  other->readCoefficients();
  for(i = 0; i < 3; ++i) {
    JBLOCKARRAY arr = other->getCoefficients(i);
    for(j = 0; j < 64; ++j) {
      EXPECT_EQ(0, arr[0][0][j]) << "Index " << j;
    }
  }
  context->destroyImage(other);
}

/**
 * Test building a coefficient accessor from an image.
 * The coefficient accessor class has its own tests, so we merely ensure
 * that it's been built properly and that all three components are accessible.
 */
TEST_F(JPEGImageTest, testAccessorFromImage) {
  testImage->readCoefficients();
  CoefficientAccessor acc(testImage);
  int off = 0, i;
  for(i = 0; i < testImage->getComponentCount(); ++i) {
    JPEGComponent *c = testImage->getComponent(i);
    EXPECT_EQ(c->getCoefficients()[0][0][0], acc.getCoefficient(off))
      << "Offset " << off;
    off += c->getDownsampledWidth() * c->getDownsampledHeight();
  }
  EXPECT_EQ(off, acc.getLength());
}

/**
 * Test that accessing coefficients in JBLOCKARRAYS works as expected.
 * This is equivalent to ensuring that the getCoefficient method is not
 * doing anything fishy to the data it receives from libjpeg.
 */
TEST_F(JPEGImageTest, testCoefficientAccess) {
  testImage->readCoefficients();
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
