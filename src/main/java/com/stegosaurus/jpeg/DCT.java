package com.stegosaurus.jpeg;

import gnu.trove.list.TIntList;

/**
 * Handles the Discrete cosine transform.
 */
public final class DCT {
  /**
   * Private CTOR.
   */
  private DCT() { }

  /**
   * Get the normalizing scale factor.
   * @param u the coefficient.
   * @return the factor.
   */
  private static double alpha(int u) {
    return (u == 0 ? Math.sqrt(1/8.0) : Math.sqrt(2/8.0));
  }

  /**
   * The cosine matrix, used a whole bunch in the transformation. This
   * way we don't have to calculate every cosine by hand, which would
   * be insane.
   * If we have an operation that looks like:
   *  cos[(pi / 8)(x - 1/2)u]
   * Where x and u are in [0, 7], it would be stored at cos[x][u]
   */
  private static double[][] cos;

  static {
    cos = new double[8][];
    /* In the interest of brevity. */
    double pi = Math.PI;
    for(int i = 0; i < 8; i++) {
      cos[i] = new double[8];
      for(int j = 0; j < 8; j++) {
        cos[i][j] = Math.cos((pi / 8) * (i + 0.5) * j);
      }
    }
  }

  /**
   * Apply a dct transform to the scan given, either the regular or the
   * inverse.
   * @param scan the scan
   * @param inverse whether to apply the inverse dct.
   */
  private static void transform(DecompressedScan scan, final boolean inverse) {
    final TIntList list = scan.getCoefficients();
    scan.forEachDataUnit(new DataUnitProcedure() {
      public void call(int mcu, byte cmp, byte hor, byte vert, int count,
                       Scan scan) {
        int index = count * 64;
        /* As far as I can think, we _have_ to copy the array over, there is
         * no choice, because we might be modifying index 28 and we'd still
         * need to look at indices 0 through 27 as they were before we
         * transformed them.
         */
        int[] dataUnit = list.toArray(index, 64);
        for(int i = 0; i < 64; i++) {
          double next = 0.0;
          /* Get the row, and column */
          int u, v, x, y;
          for(int j = 0; j < 64; j++) {
            if(inverse) {
              x = i / 8;
              y = i % 8;
              u = j / 8;
              v = j % 8;
            } else {
              u = i / 8;
              v = i % 8;
              x = j / 8;
              y = j % 8;
            }
            next += alpha(u) * alpha(v) * dataUnit[j] *
              cos[x][u] * 
              cos[y][v];
          }
          list.set(index + i, (int)Math.round(next));
        }
      }
    });
  }

  /**
   * Apply the Discrete cosine transform to the scan given.
   * @param scan the scan
   */
  public static void dct(DecompressedScan scan) {
    transform(scan, false);
  }

  /**
   * Apply the inverse discrete cosine transform to the scan given.
   * @param scan the scan.
   */
  public static void inverseDct(DecompressedScan scan) {
    transform(scan, true);
  }
}
