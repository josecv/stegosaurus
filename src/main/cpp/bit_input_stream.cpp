#include "bit_input_stream.h"

BitInputStream::BitInputStream(const char *input, int length) {
  this->data = input;
  this->index = 0;
  this->length = length;
}

char BitInputStream::read() {
  char retval = (data[index/8] >> (7 - (index % 8))) & 1;
  index++;
  return retval;
}

int BitInputStream::available() {
  return (length * 8) - index;
}
