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
  virtual JBLOCKARRAY getCoefficients(const JPEGComponent *comp) const {
    return array;
  }
 private:
  /**
   * The array to return.
   */
  JBLOCKARRAY array;
};

#endif
