#include "../../main/cpp/jpeg_context.h"
#include "../../main/cpp/jpeg_image.h"
#include "../../main/c/steg_utils.h"
#include "../../main/c/src_mgr.h"

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
    reffile = fopen("src/test/resources/cpp/lena-colour.jpeg", "rb");
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
