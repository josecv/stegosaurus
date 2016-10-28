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
#ifndef STEGOSAURUS_COEFFICIENT_ACCESSOR
#define STEGOSAURUS_COEFFICIENT_ACCESSOR
#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"
#include "jpeg_component.h"

/**
 * Allows uniform access to a bunch of DCT coefficients, accross components,
 * as though we they were arranged in a one dimensional array.
 * This allows for simple use of DCT coefficients within permutations or
 * similar contexts.
 */
class CoefficientAccessor {
 public:
  /**
   * Construct a coefficient accessor directly from an array of components.
   * @param componentArray array to the pointers to the components.
   * @param total total number of components given.
   */
  CoefficientAccessor(JPEGComponent **componentArray, int total);

  /**
   * Destructor.
   */
  virtual ~CoefficientAccessor(void);

  /**
   * Get the coefficient at a given index.
   * @param index the index.
   * @return the coefficient.
   */
  JCOEF getCoefficient(unsigned int index);

  /**
   * Set the coefficient at a given index.
   * @param index the index
   * @param value the value to set.
   */
  void setCoefficient(unsigned int index, JCOEF value);

  /**
   * Get the length, being the total number of coefficients that may be
   * accessed.
   * @return the length.
   */
  unsigned int getLength(void);

  /**
   * Figure out whether the coefficient at a given index is a DC coefficient, 
   * as opposed to an AC coefficient).
   * @param the index of the coefficient in question.
   * @return 1 if it's a DC coefficient, 0 otherwise.
   */
  bool isDC(unsigned int index);

  /**
   * Get an array, u, so that u[i] is the index of the ith coefficient in this
   * accessor that can be used for embedding purposes. A coefficient is
   * considered usable when it is a non-zero AC coefficient.
   * The array will be sorted.
   * @return the vector with the indices of usable coefficients.
   */
  int *getUsableCoefficients(void);

  /**
   * Get the number of usable coefficients.
   * @return the number of usable coefficients this accessor contains.
   * @see getUsableCoefficients
   */
  int getUsableCoefficientCount(void);

  /**
   * Take the array of usable coefficients from the accessor given, copy it
   * over to this one and use it as our own. Prevents recalculation when the
   * accessors represent the same underlying image.
   * TODO SAFETY HERE, SAFETY!
   * TODO Bad access rules here!
   */
  void cannibalizeUsables(CoefficientAccessor *other);

 private:
  /**
   * The array of components that can be used by this accessor.
   */
  JPEGComponent **components;
  /**
   * The total number of components available.
   */
  unsigned int totalComponents;

  /**
   * The length of this accessor; lazily evaluated, so use the getLength()
   * method always.
   */
  int length;

  /**
   * The usable coefficients: an array as described in the getUsables()
   * documentation.
   */
  int *usables;

  /**
   * The number of usable coefficients we have on hand.
   */
  int usableCount;

  /**
   * Find the component where a given index belongs; alter the index to
   * be the index within that component.
   * @param index pointer to the index; at the end will point to the index
   *    within the component.
   * @return the component.
   */
  JPEGComponent *findComponent(unsigned int *index);

  /**
   * Get a pointer to the coefficient corresponding to the index given, within
   * the component given.
   * The index should be local to the component.
   * @param index the index
   * @param comp a pointer to the component.
   * @return a pointer to the coefficient.
   */
  JCOEF *getInComponent(unsigned int index, JPEGComponent *comp);
};

#endif
