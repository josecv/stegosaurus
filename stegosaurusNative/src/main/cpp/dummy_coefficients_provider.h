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
#include "coefficient_accessor.h"
#ifndef STEG_DUMMY_COEFFICIENTS_PROVIDER
#define STEG_DUMMY_COEFFICIENTS_PROVIDER

/**
 * Provides JPEG Coefficients from an array given. Useful for testing.
 */
class DummyCoefficientsProvider : public JPEGCoefficientsProvider {
 public:
  /**
   * CTOR.
   * @param arr the array that will be provided to any who ask.
   */
  DummyCoefficientsProvider(JBLOCKARRAY arr) : array(arr) { }

  virtual JBLOCKARRAY getCoefficients(const JPEGComponent *comp) {
    return array;
  }

  /**
   * Change the array to provide.
   * @param arr the array.
   */
  void setArray(JBLOCKARRAY arr) {
    this->array = arr;
  }
 private:
  /**
   * The array to return.
   */
  JBLOCKARRAY array;
};

#endif
