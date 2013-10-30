#ifndef STEG_JPEG_COMPONENT_TEST
#define STEG_JPEG_COMPONENT_TEST

#include "../../main/cpp/dummy_coefficients_provider.h"
#include "../../main/cpp/jpeg_component.h"
#include "gtest/gtest.h"


/**
 * Test the JPEGComponent class.
 * Any blockiness related tests are left out, since those are quite complex.
 * Instead, they may be found in the JPEGComponentBlockinessTest.
 */
class JPEGComponentTest : public ::testing::Test {
 public:
  /**
   * Set up the test.
   */
  void SetUp(void) {
    coeffs = buildArray(0);
    provider = new DummyCoefficientsProvider(coeffs);
    component = new JPEGComponent(2, 2, 16, 16, 0, provider);
  }

  /**
   * Clean up after ourselves.
   */
  void TearDown(void) {
    delete component;
    delete provider;
    destroyArray(coeffs);
  }
 protected:
  /**
   * Smash the JBLOCKARRAY given.
   * @param array the doomed array.
   */
  void destroyArray(JBLOCKARRAY array) {
    int row;
    for(row = 0; row < 2; ++row) {
      delete [] array[row];
    }
    delete [] array;
  }

  /**
   * Construct a JBLOCKARRAY, of the dimensions this test uses, built
   * sequentially from the starting number given (i.e. so that the first
   * coefficient is start, the second is start + 1, etc).
   * @param start the value of the first coef.
   */
  JBLOCKARRAY buildArray(int start) {
    JBLOCKARRAY retval = new JBLOCKROW[2];
    int row, col, i = start;
    for(row = 0; row < 2; ++row) {
      retval[row] = new JBLOCK[2];
      for(col = 0; col < 2; ++col) {
        int j;
        for(j = 0; j < 64; ++j, ++i) {
          retval[row][col][j] = i;
        }
      }
    }
    return retval;
  }

  /**
   * The component we'll be using to test.
   */
  JPEGComponent *component;
  /**
   * The coefficient provider.
   */
  DummyCoefficientsProvider *provider;

  /**
   * The coefficients themselves.
   */
  JBLOCKARRAY coeffs;

  /**
   * The number of rows of coefficients.
   */
  static const int rows = 2;

  /**
   * The number of columns of coefficients.
   */
  static const int cols = 2;
};

/**
 * Test the getCoefficients method.
 */
TEST_F(JPEGComponentTest, TestGetCoefficients) {
  EXPECT_EQ(coeffs, component->getCoefficients());
}

/**
 * Test the forceCoefReloadOnNextAccess method.
 */
TEST_F(JPEGComponentTest, TestForceCoefReload) {
  JBLOCKARRAY other = buildArray(10);
  /* Ensure they've been accessed */
  component->getCoefficients();
  provider->setArray(other);
  EXPECT_EQ(coeffs, component->getCoefficients());
  component->forceCoefReloadOnNextAccess();
  EXPECT_EQ(other, component->getCoefficients());
  destroyArray(other);
}

#endif
