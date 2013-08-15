package com.stegosaurus.steganographers.coders.outguess;

import gnu.trove.list.TIntList;

import java.util.List;

import com.stegosaurus.jpeg.DecompressedScan;

/**
 * Abstract base class providing common functionality for users of the
 * OutGuess algorithm.
 * Outguess is described in Niels Provos' paper "Defending Against
 * Statistical Steganalysis", which can be found at:
 * http://static.usenix.org/events/sec01/full_papers/provos/provos_html/
 */
final class OutGuess {
  /**
   * Private CTOR.
   */
  private OutGuess() { }

  /**
   * The original interval, to be used while dealing with status bytes.
   */
  protected static final int X = 32;

  protected static final int JPG_THRES_MAX = 0x25;

  protected static final int JPG_THRES_LOW = 0x04;

  protected static final int JPG_THRES_MIN = 0x03;

  /**
   * Get the detectability value for a given coefficient.
   * @param coeff the coefficient
   * @return its detectability
   */
  protected static int getDetectability(int coeff) {
    int abs = Math.abs(coeff);
    if(abs >= JPG_THRES_MAX) {
      return -1;
    } else if(abs >= JPG_THRES_LOW) {
      return 0;
    } else if(abs >= JPG_THRES_MIN) {
      return 1;
    }
    return 2;
  }

  /**
   * Given a list of decompressed scans, get the one best suited for
   * steganography. Mostly, this means the one with the most data.
   * @param scans the list of scans.
   * @return the data from the best scan, or null if the list is empty.
   */
  protected static DecompressedScan getBestScan(List<DecompressedScan> scans) {
    int size = 0;
    DecompressedScan retval = null;
    for(DecompressedScan scan : scans) {
      TIntList current = scan.getCoefficients();
      if(current.size() > size) {
        size = current.size();
        retval = scan;
      }
    }
    return retval;
  }
}
