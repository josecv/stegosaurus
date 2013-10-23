#include "gtest/gtest.h"
/* TODO THIS IS HORRIBLE!! IT COSTS NOTHING TO LINK THIS IN! I SUCK! */
#include "coefficient_accessor_test.h"
#include "jpeg_image_test.h"
#include "jpeg_component_test.h"
#include "jpeg_blockiness_test.h"

int main(int argc, char **argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
