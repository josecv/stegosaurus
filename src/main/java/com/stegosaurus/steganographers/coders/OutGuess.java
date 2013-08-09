package com.stegosaurus.steganographers.coders;

import gnu.trove.list.TIntList;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.stegosaurus.jpeg.DecompressedScan;
import com.stegosaurus.stegutils.NumUtils;

/**
 * Abstract base class providing common functionality for users of the
 * OutGuess algorithm.
 * Outguess is described in Niels Provos' paper "Defending Against
 * Statistical Steganalysis", which can be found at:
 * http://static.usenix.org/events/sec01/full_papers/provos/provos_html/
 */
public abstract class OutGuess {
  /**
   * The pseudo random number generator in use.
   */
  private Random prng;

  /**
   * The original interval, to be used while dealing with status bytes.
   */
  protected static final int x = 32;

  protected static final int JPG_THRES_MAX = 0x25;

  protected static final int JPG_THRES_LOW = 0x04;

  protected static final int JPG_THRES_MIN = 0x03;

  /**
   * The key for the prng.
   */
  private String key;

  /**
   * Construct a new OutGuess object.
   * @param key the key for the pseudo random number generator to use.
   */
  public OutGuess(String key) {
    this.prng = new Random(key.hashCode());
    this.key = key;
  }

  /**
   * Reset the PRNG to its state immediately after construction. Essentially,
   * reseed it with the key.
   */
  protected void resetPRNG() {
    prng.setSeed(key.hashCode());
  }

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
   * Generate a new seed for the prng (but do not reseed it). This method
   * is not deterministic in any way.
   * @param size how many bytes the seed should contain.
   * @return the new seed.
   */
  protected byte[] generateSeed(int size) {
    /* Non determinism shouldn't matter in this case, since the seed is stored
     * inside the carrier medium anyway.
     */
    SecureRandom rand  = new SecureRandom();
    return rand.generateSeed(size);
  }

  /**
   * Reseed the prng with the seed given.
   * @param seed the seed.
   */
  protected void reseedPRNG(byte[] seed) {
    prng.setSeed(NumUtils.intFromBytes(seed));
  }

  /**
   * Reseed the prng with the seed given.
   * @param seed the seed.
   */
  protected void reseedPRNG(long seed) {
    prng.setSeed(seed);
  }

  /**
   * Get a new pseudo-random number in the interval [1:interval].
   * @param interval the upper bound on the number returned.
   * @return the pseudo random number.
   */
  protected int getRandom(int interval) {
    return prng.nextInt(interval - 1) + 1;
  }

  /**
   * Given a list of decompressed scans, get the one best suited for
   * steganography. Mostly, this means the one with the most data.
   * @param scans the list of scans.
   * @return the data from the best scan, or null if the list is empty.
   */
  protected DecompressedScan getBestScan(List<DecompressedScan> scans) {
    int size = 0;
    DecompressedScan retval = null;
    for(DecompressedScan scan : scans) {
      TIntList current = scan.getCoefficientBuffers().get(0);
      if(current.size() > size) {
        size = current.size();
        retval = scan;
      }
    }
    return retval;
  }

  /**
   * Get the upper bound on the interval for random numbers.
   * @param cover the cover image in use.
   * @param index the last visited index in the cover image.
   * @param remainingMessage the number of message bits still to read.
   */
  protected int getInterval(int[] cover, int index, int remainingMessage) {
    int coverBits = (cover.length - index);
    return (2 * coverBits) / remainingMessage;
  }

  /**
   * Get the key in use by this object.
   * @return the key.
   */
  protected String getKey() {
    return key;
  }
}
