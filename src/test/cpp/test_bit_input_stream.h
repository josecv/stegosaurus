/* What's more portable than hardcoding a directory path, after all? */
#include "../../main/cpp/bit_input_stream.h"
#include "gtest/gtest.h"

class BitInputStreamTest : public ::testing::Test {
  /* We don't need any set up or tear down, so nothing here */
};

TEST_F(BitInputStreamTest, TestRead) {
  char arg[] = { 0x5D, 0x2A, 0x3F };
  BitInputStream *st = new BitInputStream(arg, 3);
  char expected[] = { 0, 1, 0, 1,  1, 1, 0, 1,
                      0, 0, 1, 0,  1, 0, 1, 0,
                      0, 0, 1, 1,  1, 1, 1, 1 };
  int total = 3 * 8, i;
  for(i = 0; i < total; i++) {
    EXPECT_EQ(total - i, st->available());
    EXPECT_EQ(expected[i], st->read());
  }
  delete st;
}
