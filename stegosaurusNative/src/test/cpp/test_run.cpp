#include "gtest/gtest.h"
/* Just include the tests. They're not used anywhere else. */
#include "coefficient_accessor_test.h"
#include "jpeg_image_test.h"
#include "jpeg_component_test.h"
#include "jpeg_blockiness_test.h"
#include "error_mgr_test.h"

int main(int argc, char **argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
