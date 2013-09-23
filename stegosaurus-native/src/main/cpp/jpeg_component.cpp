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
