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
   * If we do decide to skip ahead, we then branch out for some common
   * component configurations (i.e. 1 component or 3 components), so as to
   * not have to use a loop and hardcode the logic of calculation.
   * Thus, this function is divided in four, as it were, hopefully to make
   * it as fast as we can.
   */
  if(vertical_boundary) {
    int val;
    for(index = 0; index < stride; index++) {
      val = samp_row[index];
      retval += abs(val - previous_row[index]);
      index_in_component = index / components;
      if(index_in_component && !(index_in_component % 8)) {
        retval += abs(val - samp_row[index - components]);
      }
    }
  } else {
    int tmp0, tmp1, tmp2;
    switch(components) {
      case 3:
        tmp0 = tmp1 = tmp2 = 0;
        /* We'd like to make use of parallelization of operation streams here,
         * so we split up the calculation into three different variables, which
         * are then added up in the end.
         * TODO Examine the compiled code to figure out whether this has a
         * meaningful effect.
         */
        for(index = block_width; index < stride; index += block_width) {
          tmp0 += abs(samp_row[index] - samp_row[index - 3]);
          tmp1 += abs(samp_row[index + 1] - samp_row[index - 2]);
          tmp2 += abs(samp_row[index + 2] - samp_row[index - 1]);
        }
        return tmp0 + tmp1 + tmp2;
      case 1:
        for(index = block_width; index < stride; index += block_width) {
          retval += abs(samp_row[index] - samp_row[index - 1]);
        }
        break;
      default:
        int current_comp;
        int block;
        for(block = block_width; block < stride; block += block_width) {
          for(current_comp = 0; current_comp < components; current_comp++) {
            index = block + current_comp;
            retval += abs(samp_row[index] - samp_row[index - components]);
          }
        }
    }
  }
  return retval;
}

int blockinessForRows(int components, int width, JSAMPARRAY buffer,
                      int row_count, JSAMPROW previous_block_last_row) {
  int i;
  int result = 0;
  const int stride = width * components;
  const int block_width = components * 8;
  int index, index_in_component, row;
  if(previous_block_last_row) {
    int val;
    for(index = 0; index < stride; index++) {
      val = buffer[0][index];
      result += abs(val - previous_block_last_row[index]);
      index_in_component = index / components;
      if(index_in_component && !(index_in_component % 8)) {
        result += abs(val - buffer[0][index - components]);
      }
    }
  }
  for(row = (previous_block_last_row ? 1 : 0); row < row_count; row++) {
    int current_comp;
    int block;
    JSAMPROW samp_row = buffer[row];
    for(block = block_width; block < stride; block += block_width) {
      for(current_comp = 0; current_comp < components; current_comp++) {
        index = block + current_comp;
        result += abs(samp_row[index] - samp_row[index - components]);
      }
    }
  }
  return result;
}
