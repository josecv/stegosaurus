#include "blockiness.h"

int blockinessForRow(int components, int width, JSAMPROW samp_row,
                     int row_index, JSAMPROW previous_row) {
  int index;
  int retval = 0;
  int stride = width * components;
  /* This represents the index that any given pixel would have if the image
   * contained a single component. */
  int index_in_component;
  for(index = 0; index < stride; ++index) {
    if(!(row_index % 8) && row_index) {
      retval += abs(samp_row[index] - previous_row[index]);
    }
    index_in_component = index / components;
    if(!(index_in_component % 8) && index_in_component) {
      retval += abs(samp_row[index] - samp_row[index - components]);
    }
  }
  return retval;
}
