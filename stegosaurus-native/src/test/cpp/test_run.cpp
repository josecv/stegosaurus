#include "gtest/gtest.h"
#include "coefficient_accessor_test.h"
#include "jpeg_image_test.h"
#include "jpeg_component_test.h"

int main(int argc, char **argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
