#include "blockiness.h"

int blockinessForRow(int components, int width, JSAMPROW samp_row,
                     int row_index, JSAMPROW previous_row) {
  int index;
  int retval = 0;
  int stride = width * components;
  for(index = 0; index < stride; ++index) {
    if(!(row_index % 8) && row_index) {
      retval += abs(samp_row[index] - previous_row[index]);
    }
    if(!((index / components) % 8) && (index / components)) {
      retval += abs(samp_row[index] - samp_row[index - components]);
    }
  }
  return retval;
}
