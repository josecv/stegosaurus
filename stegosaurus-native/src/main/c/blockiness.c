#include "blockiness.h"

int blockinessForRow(int components, int width, JSAMPROW samp_row,
                     int row_index, JSAMPROW previous_row) {
  int index;
  int retval = 0;
  const int stride = width * components;
  const int block_width = components * 8;
  /* Whether we are at a vertical boundary. */
  const int vertical_boundary = !(row_index % 8) && row_index;
  /* This represents the index that any given pixel would have if the image
   * contained a single component. */
  int index_in_component;
  /* If we're at a vertical boundary we have to get the difference between
   * _every_ pixel and its cross-boundary countrepart. In addition we also
   * have to take into account the horizontal boundaries. On the other hand,
   * if we only have to worry about the horizontal boundaries, we can
   * feel free to skip ahead to relevant blocks.
   * Thus, this function is divided in two, as it were, so as to speed up
   * the calculation.
   */
  if(vertical_boundary) {
    for(index = 0; index < stride; ++index) {
      retval += abs(samp_row[index] - previous_row[index]);
      index_in_component = index / components;
      if(!(index_in_component % 8) && index_in_component) {
        retval += abs(samp_row[index] - samp_row[index - components]);
      }
    }
  } else {
    int current_comp;
    int block;
    for(block = block_width; block < stride; block += block_width) {
      for(current_comp = 0; current_comp < components; ++current_comp) {
        index = block + current_comp;
        retval += abs(samp_row[index] - samp_row[index - components]);
      }
    }
  }
  return retval;
}
