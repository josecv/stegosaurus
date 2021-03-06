/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Defines a class that encapsulates a single component inside of a jpeg image,
 * and an interface to fulfill the coefficients corresponding to a component.
 */
#ifndef STEGOSAURUS_JPEG_COMPONENT
#define STEGOSAURUS_JPEG_COMPONENT
#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"

class JPEGCoefficientsProvider;

/**
 * Represents a component inside a jpeg image. Permits gathering information
 * about said component, as well as acquiring the actual DCT coefficients.
 * Requires use of a coefficient provider, which will not be deleted by
 * the JPEGComponent, but should not be deleted before the JPEG component
 * is gone.
 */
class JPEGComponent {
 public:
  /**
   * Construct a JPEGComponent using a jpeg_component_info structure.
   * No pointer to the jpeg_component_info given will be kept.
   * @param info a pointer to the jpeg component info structure.
   * @param p a pointer to the coefficient provider to use.
   */
  JPEGComponent(const jpeg_component_info *info,
                JPEGCoefficientsProvider *p);
  /**
   * Construct a JPEGComponent by passing some relevant information directly.
   * @param w_blocks width in blocks
   * @param h_blocks height in blocks
   * @param down_w downsampled width
   * @param down_h downsampled height
   * @param ind the component index
   * @param p pointer to the coefficient provider.
   */
  JPEGComponent(JDIMENSION w_blocks, JDIMENSION h_blocks,
                JDIMENSION down_w, JDIMENSION down_h,
                int ind,
                JPEGCoefficientsProvider *p);
  /**
   * Get the DCT Coefficients for this component. If needed, they'll be
   * acquired from the coefficient provider.
   * @return the coefficients.
   */
  JBLOCKARRAY getCoefficients(void);
  /**
   * Get the width in blocks of this component.
   * @return the width in blocks.
   */
  JDIMENSION getWidthInBlocks(void) const;
  /**
   * Get the height in blocks of this component.
   * @return the height in blocks.
   */
  JDIMENSION getHeightInBlocks(void) const;
  /**
   * Get the downsampled width.
   * @return the downsampled width.
   */
  JDIMENSION getDownsampledWidth(void) const;
  /**
   * Get the downsampled height.
   * @return the downsampled height.
   */
  JDIMENSION getDownsampledHeight(void) const;
  /**
   * Get the width of a single block.
   * @return the block width.
   */
  JDIMENSION getBlockWidth(void) const;

  /**
   * Get the height of a single block.
   * @return the block height.
   */
  JDIMENSION getBlockHeight(void) const;

  /**
   * Get the number of coefficients in a single block inside of this
   * component.
   * @return the block size.
   */
  JDIMENSION getBlockSize(void) const;

  /**
   * Get the component's index.
   * @return the index.
   */
  int getIndex(void) const;

  /**
   * Get the total number of coefficients in this component.
   * @return the total number of coefficients.
   */
  unsigned int getTotalNumberOfCoefficients(void) {
    return downsampled_height * downsampled_width;
  }

  /**
   * Force this object to request coefficients from its provider the next
   * time it needs access to them, instead of using the ones previously
   * returned.
   */
  void forceCoefReloadOnNextAccess(void);
 private:
  /**
   * The DCT coefficients. Unrealized at construction.
   */
  JBLOCKARRAY coefficients;

  /**
   * The Coefficients provider.
   */
  JPEGCoefficientsProvider *provider;

  /**
   * Dimensions in blocks of this component.
   */
  JDIMENSION width_in_blocks, height_in_blocks;

  /**
   * Downsampled pixel dimensions of this component.
   */
  JDIMENSION downsampled_width, downsampled_height;

  /**
   * The dimensions of an individual block.
   */
  JDIMENSION block_width, block_height, block_size;

  /**
   * The component's index.
   */
  int index;
};

/**
 * Provides DCT coefficients corresponding to a JPEG component.
 */
class JPEGCoefficientsProvider {
 public:
  /**
   * Get the DCT coefficients for the component given. Should get all of them.
   * @param comp the JPEGComponent
   * @return the coefficients.
   */
  virtual JBLOCKARRAY getCoefficients(const JPEGComponent *comp) = 0;

  /**
   * Destructor. A no-op.
   */
  virtual ~JPEGCoefficientsProvider() { }
};

#endif
