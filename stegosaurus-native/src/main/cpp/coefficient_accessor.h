#ifndef STEGOSAURUS_COEFFICIENT_ACCESSOR
#define STEGOSAURUS_COEFFICIENT_ACCESSOR
#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"
#include "jpeg_image.h"

/**
 * Allows uniform access to a bunch of DCT coefficients, accross components,
 * as though we they were arranged in a one dimensional array.
 * This allows for simple use of DCT coefficients within permutations or
 * similar contexts.
 */
class CoefficientAccessor {
 public:
  /**
   * Construct a coefficient accessor from an image.
   * @param img the image.
   */
  CoefficientAccessor(JPEGImage *img);

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
  int getLength(void);

  /**
   * Figure out whether the coefficient at a given index is a DC coefficient, 
   * as opposed to an AC coefficient).
   * @param the index of the coefficient in question.
   * @return 1 if it's a DC coefficient, 0 otherwise.
   */
  int isDC(unsigned int index);
 private:
  /**
   * The array of components that can be used by this accessor.
   */
  JPEGComponent **components;
  /**
   * The total number of components available.
   */
  int totalComponents;

  /**
   * Whether the component array should be freed by us.
   * True if we created it, false otherwise.
   * Note that either way we don't free the actual components!
   */
  int freeComponentArray;

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
