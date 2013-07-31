package com.stegosaurus.jpeg;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * A JPEG image scan that has been run through a jpeg decompressor.
 */
public class DecompressedScan extends Scan {
  /**
   * The decompressed coefficients, where every entry in the list is scan data
   * up to the next reset marker.
   */
  private List<TIntList> coefficients = new ArrayList<>();

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
    coefficients = new ArrayList<>(s.coefficients.size());
    for(TIntList l : s.coefficients) {
      coefficients.add(new TIntArrayList(l));
    }
  }

  /**
   * Default CTOR.
   */
  public DecompressedScan() {
    super();
  }

  /**
   * Add a new coefficient buffer to the list. This would correspond
   * to coefficient data between one reset marker and the next.
   * @return the new coefficient buffer.
   */
  public TIntList growCoefficients() {
    TIntList buffer = new TIntArrayList();
    coefficients.add(buffer);
    return buffer;
  }

  /**
   * Get the coefficient buffers that this scan has on hand.
   */
  public List<TIntList> getCoefficientBuffers() {
    return new ArrayList<TIntList>(coefficients);
  }

  /**
   * Drop all the coefficient buffers from this scan.
   */
  public void dropCoefficients() {
    coefficients.clear();
  }
}
