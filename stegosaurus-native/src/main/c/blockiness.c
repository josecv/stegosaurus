#include "blockiness.h"
#include <assert.h>

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
   * Thus, this function is divided in two.
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
    int current_comp;
    int block;
    for(block = block_width; block < stride; block += block_width) {
      for(current_comp = 0; current_comp < components; current_comp++) {
        index = block + current_comp;
        retval += abs(samp_row[index] - samp_row[index - components]);
      }
    }
  }
  return retval;
}

/**
 * Process the blockiness in between the row given, and that of the previous
 * component; this does NOT get the horizontal blockiness of the actual row
 * given; that is left to the caller, if needed.
 * @param stride the row stride: its pixel width * component count
 * @param row the row
 * @param previous_row the last row of the previous row; non NULL
 * @return the blockiness for this single row, taking the last one into account
 */
static unsigned int firstRow(int stride, JSAMPROW row, JSAMPROW previous_row) {
  int index;
  unsigned int result = 0, val;
  for(index = 0; index < stride; index++) {
    val = row[index];
    result += abs(val - previous_row[index]);
  }
  return result;
}

int blockinessForRows(int components, int stride, JSAMPARRAY buffer,
                      int row_count, JSAMPROW previous_block_last_row) {
  int result = 0;
  const int block_width = components * 8;
  int index, row;
  int current_comp;
  int block;
  if(previous_block_last_row) {
    result += firstRow(stride, buffer[0], previous_block_last_row);
  } else {
    for(block = block_width; block < stride; block += block_width) {
      for(current_comp = 0; current_comp < components; current_comp++) {
        index = block + current_comp;
        result += abs(buffer[0][index] - buffer[0][index - components]);
      }
    }
  }
  for(row = 0; row < row_count; row++) {
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
  /*
   * For the functions marked unsafe.
  if(row_count == 8) {
    int prev;
    int tmp1 = 0, tmp2 = 0, tmp3 = 0, tmp4 = 0, tmp5 = 0, tmp6 = 0, tmp7 = 0;
    JSAMPROW buf1 = buffer[1], buf2 = buffer[2], buf3 = buffer[3],
             buf4 = buffer[4], buf5 = buffer[5], buf6 = buffer[6],
             buf7 = buffer[7];
    switch(components) {
      case 1:
        for(index = block_width; index < stride; index += block_width) {
          prev = index - 1;
          tmp1 += abs(buf1[index] - buf1[prev]);
          tmp2 += abs(buf2[index] - buf2[prev]);
          tmp3 += abs(buf3[index] - buf3[prev]);
          tmp4 += abs(buf4[index] - buf4[prev]);
          tmp5 += abs(buf5[index] - buf5[prev]);
          tmp6 += abs(buf6[index] - buf6[prev]);
          tmp7 += abs(buf7[index] - buf7[prev]);
        }
        break;
      case 3:
        break;
      default:
    }
    result += tmp1 + tmp2 + tmp3 + tmp4 + tmp5 + tmp6 + tmp7;
  } else {
  } */
  return result;
}

int blockinessForRowsUnsafe(int components, int stride, JSAMPARRAY buffer,
                            JSAMPROW previous_block_last_row) {
  int result = 0;
  const int block_width = components * 8;
  int index;
  int current_comp;
  int block;
  int prev;
  JSAMPROW buf0 = buffer[0], buf1 = buffer[1], buf2 = buffer[2],
           buf3 = buffer[3], buf4 = buffer[4], buf5 = buffer[5],
           buf6 = buffer[6], buf7 = buffer[7];
  result = firstRow(stride, buffer[0], previous_block_last_row);
  for(block = block_width; block < stride; block += block_width) {
    for(current_comp = 0; current_comp < components; current_comp++) {
      index = block + current_comp;
      prev = index - components;
      result += abs(buf0[index] - buf0[prev]);
      result += abs(buf1[index] - buf1[prev]);
      result += abs(buf2[index] - buf2[prev]);
      result += abs(buf3[index] - buf3[prev]);
      result += abs(buf4[index] - buf4[prev]);
      result += abs(buf5[index] - buf5[prev]);
      result += abs(buf6[index] - buf6[prev]);
      result += abs(buf7[index] - buf7[prev]);
    }
  }
  return result;
}

int blockinessForRows3Comp(int components, int stride, JSAMPARRAY buffer,
                           int row_count, JSAMPROW previous_block_last_row) {
  int result = 0;
  int row, index;
  JSAMPROW r;
  unsigned int tmp1 = 0, tmp2 = 0, tmp3 = 0;
  const int block_width = 24;
  if(previous_block_last_row) {
    result += firstRow(stride, buffer[0], previous_block_last_row);
  }
  for(row = 0; row < row_count; row++) {
    for(index = block_width; index < stride; index += block_width) {
      r = buffer[row];
      tmp1 += abs(r[index] - r[index - 3]);
      tmp2 += abs(r[index + 1] - r[index - 2]);
      tmp3 += abs(r[index + 2] - r[index - 1]);
    }
  }
  return result + tmp1 + tmp2 + tmp3;
}

int blockinessForRows3CompUnsafe(int components, int stride, JSAMPARRAY buffer,
                                 JSAMPROW previous_block_last_row) {
  const int block_width = 24;
  int result, index;
  unsigned int index_m_1, index_m_2, index_m_3, index_p_1, index_p_2;
  unsigned int tmp1 = 0, tmp2 = 0, tmp3 = 0;
  JSAMPROW buf0 = buffer[0], buf1 = buffer[1], buf2 = buffer[2],
           buf3 = buffer[3], buf4 = buffer[4], buf5 = buffer[5],
           buf6 = buffer[6], buf7 = buffer[7];
  result = firstRow(stride, buffer[0], previous_block_last_row);
  for(index = block_width; index < stride; index += block_width) {
    index_m_1 = index - 1;
    index_m_2 = index - 2;
    index_m_3 = index - 3;
    index_p_1 = index + 1;
    index_p_2 = index + 2;

    tmp1 += abs(buf0[index] - buf0[index_m_3]);
    tmp2 += abs(buf0[index_p_1] - buf0[index_m_2]);
    tmp3 += abs(buf0[index_p_2] - buf0[index_m_1]);

    tmp1 += abs(buf1[index] - buf1[index_m_3]);
    tmp2 += abs(buf1[index_p_1] - buf1[index_m_2]);
    tmp3 += abs(buf1[index_p_2] - buf1[index_m_1]);

    tmp1 += abs(buf2[index] - buf2[index_m_3]);
    tmp2 += abs(buf2[index_p_1] - buf2[index_m_2]);
    tmp3 += abs(buf2[index_p_2] - buf2[index_m_1]);

    tmp1 += abs(buf3[index] - buf3[index_m_3]);
    tmp2 += abs(buf3[index_p_1] - buf3[index_m_2]);
    tmp3 += abs(buf3[index_p_2] - buf3[index_m_1]);

    tmp1 += abs(buf4[index] - buf4[index_m_3]);
    tmp2 += abs(buf4[index_p_1] - buf4[index_m_2]);
    tmp3 += abs(buf4[index_p_2] - buf4[index_m_1]);

    tmp1 += abs(buf5[index] - buf5[index_m_3]);
    tmp2 += abs(buf5[index_p_1] - buf5[index_m_2]);
    tmp3 += abs(buf5[index_p_2] - buf5[index_m_1]);

    tmp1 += abs(buf6[index] - buf6[index_m_3]);
    tmp2 += abs(buf6[index_p_1] - buf6[index_m_2]);
    tmp3 += abs(buf6[index_p_2] - buf6[index_m_1]);

    tmp1 += abs(buf7[index] - buf7[index_m_3]);
    tmp2 += abs(buf7[index_p_1] - buf7[index_m_2]);
    tmp3 += abs(buf7[index_p_2] - buf7[index_m_1]);
  }
  return result + tmp1 + tmp2 + tmp3;
}

int blockinessForRows1Comp(int components, int stride, JSAMPARRAY buffer,
                           int row_count, JSAMPROW previous_block_last_row) {
  return blockinessForRows(components, stride, buffer, row_count,
                           previous_block_last_row);
}

int blockinessForRows1CompUnsafe(int components, int stride, JSAMPARRAY buffer,
                                 JSAMPROW previous_block_last_row) {
  return blockinessForRows(components, stride, buffer, 8,
                           previous_block_last_row);
}
