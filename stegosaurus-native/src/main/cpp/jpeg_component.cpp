#include "jpeg_component.h"

JPEGComponent::JPEGComponent(const jpeg_component_info *info,
                             const JPEGCoefficientsProvider *p)
    : coefficients(NULL),
      provider(p),
      width_in_blocks(info->width_in_blocks),
      height_in_blocks(info->height_in_blocks),
      downsampled_width(info->downsampled_width),
      downsampled_height(info->downsampled_height),
      index(info->component_index) {
  block_width = downsampled_width / width_in_blocks;
  block_height = downsampled_height / height_in_blocks;
}


JPEGComponent::JPEGComponent(JDIMENSION w_blocks, JDIMENSION h_blocks,
                             JDIMENSION down_w, JDIMENSION down_h,
                             int ind,
                             const JPEGCoefficientsProvider *p)
  : coefficients(NULL),
    provider(p),
    width_in_blocks(w_blocks),
    height_in_blocks(h_blocks),
    downsampled_width(down_w),
    downsampled_height(down_h),
    index(ind) {
  block_width = downsampled_width / width_in_blocks;
  block_height = downsampled_height / height_in_blocks;
}

JBLOCKARRAY JPEGComponent::getCoefficients(void) {
  if(coefficients == NULL) {
    coefficients = provider->getCoefficients(this);
  }
  return coefficients;
}

int JPEGComponent::calculateBlockiness(void) {
  int retval = 0;
  unsigned int i, j;
  for(i = 0; i < ((downsampled_width - 1) / 8); ++i) {
    for(j = 0; j < downsampled_height; ++j) {
      retval += abs(coefficientAt(8 * i, j) - coefficientAt((8 * i) + 1, j));
    }
  }
  for(j = 0; j < ((downsampled_height - 1) / 8); ++j) {
    for(i = 0; i < downsampled_width; ++i) {
      retval += abs(coefficientAt(i, 8 * j) - coefficientAt(i, (8 * j) + 1));
    }
  }
  return retval;
}

void JPEGComponent::forceCoefReloadOnNextAccess(void) {
  coefficients = NULL;
}

JDIMENSION JPEGComponent::getWidthInBlocks(void) const {
  return this->width_in_blocks;
}

JDIMENSION JPEGComponent::getHeightInBlocks(void) const {
  return this->height_in_blocks;
}

JDIMENSION JPEGComponent::getDownsampledWidth(void) const {
  return this->downsampled_width;
}

JDIMENSION JPEGComponent::getDownsampledHeight(void) const {
  return this->downsampled_height;
}

JDIMENSION JPEGComponent::getBlockWidth(void) const {
  return this->block_width;
}

JDIMENSION JPEGComponent::getBlockHeight(void) const {
  return this->block_height;
}

int JPEGComponent::getIndex(void) const {
  return this->index;
}
