#include "jpeg_lib_exception.h"
#include <stdio.h>
#include <string.h>

const char* JPEGLibException::what() {
  return msg.c_str();
}
