package com.stegosaurus.jpeg;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

/**
 * A JPEG image scan that has been run through a jpeg decompressor.
 */
public class DecompressedScan extends Scan {
  /**
   * The decompressed coefficients.
   */
  private TIntList coefficients = new TIntArrayList();

  /**
   * Copy constructor. Copies the scan given into this one.
   * @param s the scan to copy.
   */
  public DecompressedScan(Scan s) {
    super(s);
  }

  /**
   * Copy constructor. Copies the scan given into this one.
   * @param s the scan to copy.
   */
  public DecompressedScan(DecompressedScan s) {
    super(s);
    coefficients = new TIntArrayList(s.coefficients);
  }

  /**
   * Default CTOR.
   */
  public DecompressedScan() {
    super();
  }

  /**
   * Get the coefficient buffer that this scan has on hand.
   */
  public TIntList getCoefficients() {
    return this.coefficients;
  }

  /**
   * Drop all the coefficient buffers from this scan.
   */
  public void dropCoefficients() {
    coefficients.clear();
  }
}
