#include "jpeg_component.h"

JPEGComponent::JPEGComponent(const jpeg_component_info *info,
                             JPEGCoefficientsProvider *p)
    : coefficients(NULL),
      provider(p),
      width_in_blocks(info->width_in_blocks),
      height_in_blocks(info->height_in_blocks),
      downsampled_width(info->downsampled_width),
      downsampled_height(info->downsampled_height),
      index(info->component_index) {
  /* I believe the docs specify that the blocks are always 64 bytes long */
  block_width = 8;
  block_height = 8;
  block_size = block_width * block_height;
}


JPEGComponent::JPEGComponent(JDIMENSION w_blocks, JDIMENSION h_blocks,
                             JDIMENSION down_w, JDIMENSION down_h,
                             int ind,
                             JPEGCoefficientsProvider *p)
  : coefficients(NULL),
    provider(p),
    width_in_blocks(w_blocks),
    height_in_blocks(h_blocks),
    downsampled_width(down_w),
    downsampled_height(down_h),
    index(ind) {
  block_width = 8;
  block_height = 8;
  block_size = block_width * block_height;
}

JBLOCKARRAY JPEGComponent::getCoefficients(void) {
  if(coefficients == NULL) {
    coefficients = provider->getCoefficients(this);
  }
  return coefficients;
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

JDIMENSION JPEGComponent::getBlockSize(void) const {
  return this->block_size;
}

int JPEGComponent::getIndex(void) const {
  return this->index;
}
