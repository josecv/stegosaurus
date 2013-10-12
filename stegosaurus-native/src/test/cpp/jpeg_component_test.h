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
    coeffs = new JBLOCKROW[2];
    int row, col, i = 0;
    for(row = 0; row < 2; ++row) {
      coeffs[row] = new JBLOCK[2];
      for(col = 0; col < 2; ++col) {
        int j;
        for(j = 0; j < 64; ++j, ++i) {
          coeffs[row][col][j] = i;
        }
      }
    }
    provider = new DummyCoefficientsProvider(coeffs);
    component = new JPEGComponent(2, 2, 16, 16, 0, provider);
  }

  /**
   * Clean up after ourselves.
   */
  void TearDown(void) {
    int row;
    delete component;
    delete provider;
    for(row = 0; row < 2; ++row) {
      delete [] coeffs[row];
    }
    delete [] coeffs;
  }
 protected:
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

/* So, we've set up the coefficients to be a 2x2 blob of 64 byte long blocks.
 * In other words, we have a total of 4 blocks, for a total of 4 * 64 = 256
 * coefficients.
 *
 *
 * This is small enough that they've been assigned sequentially, so what
 * we wind up with looks like this:
   0   1   2    3    4    5    6    7    8    9    10   11   12   13   14   15
_______________________________________________________________________________
0 |0   1   2    3    4    5    6    7    64   65   66   67   68   69   70   71
1 |8   9   10   11   12   13   14   15   72   73   74   75   76   77   78   79
2 |16  17  18   19   20   21   22   23   80   81   82   83   84   85   86   87
3 |24  25  26   27   28   29   30   31   88   89   90   91   92   93   94   95
4 |32  33  34   35   36   37   38   39   96   97   98   99   100  101  102  103
5 |40  41  42   43   44   45   46   47   104  105  106  107  108  109  110  111
6 |48  49  50   51   52   53   54   55   112  113  114  115  116  117  118  119 
7 |56  57  58   59   60   61   62   63   120  121  122  123  124  125  126  127
8 |128 129 130  131  132  133  134  135  192  193  194  195  196  197  198  199
9 |136 137 138  139  140  141  142  143  200  201  202  203  204  205  206  207 
10|144 145 146  147  148  149  150  151  208  209  210  211  212  213  214  215 
11|152 153 154  155  156  157  158  159  216  217  218  219  220  221  222  223 
12|160 161 162  163  164  165  166  167  224  225  226  227  228  229  230  231 
13|168 169 170  171  172  173  174  175  232  233  234  235  236  237  238  239 
14|176 177 178  179  180  181  182  183  240  241  242  243  244  245  246  247 
15|184 185 186  187  188  189  190  191  248  249  250  251  252  253  254  255
 */

/**
 * Test the coefficientAt method.
 * Horribly, this is done by just looking at a whole bunch of x, y coordinates
 * and seeing if they match hardcoded values.
 */
TEST_F(JPEGComponentTest, TestCoefficientAt) {
  EXPECT_EQ(0,   component->coefficientAt(0, 0));
  EXPECT_EQ(150, component->coefficientAt(6, 10));
  EXPECT_EQ(94,  component->coefficientAt(14, 3));
  EXPECT_EQ(255, component->coefficientAt(15, 15));
  EXPECT_EQ(241, component->coefficientAt(9, 14));
  EXPECT_EQ(237, component->coefficientAt(13, 13));
  EXPECT_EQ(206, component->coefficientAt(14, 9));
}

#endif
