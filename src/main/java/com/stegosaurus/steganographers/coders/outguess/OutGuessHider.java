package com.stegosaurus.steganographers.coders.outguess;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import java.util.BitSet;

import org.apache.commons.lang3.tuple.Pair;

import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.NumUtils;



/**
 * The actual hider for outguess, does the heavy lifting of embedding data
 * in the carrier.
 */
public class OutGuessHider extends OutGuess {
  /**
   * The cover image.
   */
  private int[] cover;

  /**
   * The seed that will be in use by this object once the status bytes
   * are embeded.
   */
  private short seed;

  /**
   * The set of modified coefficients. This includes both coefficients used
   * for the actual embedding of image data, and coefficients used for error
   * correction.
   */
  private BitSet locked;

  /**
   * The frequency of DCT coefficients in the original image.
   */
  private TIntIntMap originalFrequencies;

  /**
   * The error tolerances for the DCT coefficients.
   */
  private TIntDoubleMap tolerances;

  /**
   * The error values for DCT coefficients.
   */
  private TIntIntMap errors;

  /**
   * The scaling factor.
   */
  private double alpha;

  /**
   * The number of modified bits.
   */
  private int modified;

  /**
   * The bias for changed bits.
   */
  private int bias;

  /**
   * Whether to pretend embedding. If true, no embedding or error correction
   * will be performed. Used for seed selection.
   */
  private boolean pretend;

  /**
   * Get the error tolerance value for the coefficient given.
   * @param coeff the coefficient.
   * @return the tolerance.
   */
  private double getTolerance(int coeff) {
    if(!tolerances.containsKey(coeff)) {
      tolerances.put(coeff, alpha * originalFrequencies.get(coeff));
    }
    return tolerances.get(coeff);
  }

  /**
   * Given a cover image, hide the message given in it, starting at the index
   * given.
   * @param message the message
   * @param index the index to starting hiding in.
   */
  private void hideMessage(byte[] message, int index) {
    BitInputStream stream = new BitInputStream(message);
    bias = modified = 0;
    int available = stream.available();
    while(available > 0) {
      int i = 0;
      /* We'll change the interval every 8 bits */
      index += getRandom(getInterval(cover, index, available));
      while(i < 8) {
        if(cover[index] == 0 || cover[index] == 1) {
          index++;
          continue;
        }
        hideAtIndex(stream.read(), index);
        index++;
        i++;
      }
      available = stream.available();
    }
    stream.close();
  }

  /**
   * Correct any remaining errors in the cover image given.
   */
  private void correctErrors() {
    /* We'll just iterate over everything and try to correct any errors
     * we can */
    errors.forEachEntry(new TIntIntProcedure() {
      public boolean execute(int coeff, int errors) {
        while(errors > 0) {
          errors--;
          exchDCT(cover.length, coeff);
        }
        return true;
      }
    });
  }

  /**
   * Hide some status info in the cover, namely some length and a new seed
   * for the prng.
   * @param length the length of the message, to be hidden.
   * @return the next index to use.
   */
  private int hideStatus(int length) {
    byte[] len = NumUtils.byteArrayFromInt(length);
    byte[] seedBytes = NumUtils.byteArrayFromShort(seed);
    BitInputStream in = new BitInputStream(len, seedBytes);
    int index = 0;
    int total = in.available();
    while(total > 0) {
      if(cover[index] == 0 || cover[index] == 1) {
        index++;
        continue;
      }
      hideAtIndex(in.read(), index);
      total--;
      index += getRandom(X);
    }
    in.close();
    reseedPRNG(seed);
    return index;
  }

  /**
   * Attempt to correct an error inside the cover medium, at the index given.
   * @param index the index pointing to the error.
   * @return whether the operation worked out.
   */
  private boolean exchDCT(int index) {
    return exchDCT(index, cover[index]);
  }

  /**
   * Attempt to correct an error inside the cover medium, for the coefficient
   * given, and start looking from the index given.
   * @param index the index to start looking at. Not necessarily the same
   * as the index containing the error.
   * @param coeff the coefficient to correct an error for.
   * @return whether the correction was successful.
   */
  private boolean exchDCT(int index, int coeff) {
    int adj = coeff ^ 1;
    for(int j = index - 1; j >= 0; j--) {
      if(cover[j] == coeff && !locked.get(j)) {
        cover[j] = adj;
        locked.set(j);
        return true;
      }
    }
    return false;
  }

  /**
   * Hide the bit given inside of the cover medium, at the index given, and
   * if necessary attempt to perform some error correction.
   * @param bit the bit to hide.
   * @param index the index to hide the bit in.
   */
  private void hideAtIndex(int bit, int index) {
    int original = cover[index];
    bias += getDetectability(original);
    modified++;
    int val = NumUtils.placeInLSB(cover[index], bit);
    locked.set(index);
    if(original == val) {
      return;
    }
    if(pretend) {
      return;
    }
    cover[index] = val;
    /* Let's get the adjacent coefficient, and see if we can't correct this
     * one and that one at the same time.
     */
    int adj = val ^ 1;
    if(errors.get(adj) > 0) {
      errors.adjustValue(adj, -1);
    } else if(errors.get(val) < getTolerance(val)) {
      /* The error is still within acceptable bounds, so we can keep going */
      errors.adjustOrPutValue(val, 1, 1);
    } else if(!exchDCT(index)) {
      /* We couldn't fix the error for now, so we'll just have to increment
       * the error counter anyway and keep going. We'll try and fix it later
       * on.
       */
      errors.increment(val);
    }
  }

  /**
   * Construct a new OutGuess hider.
   * Should the pretend value be true, no embedding or error correction
   * will be done. This is used in seed selection.
   * @param cover the cover image.
   * @param key the key to seed the prng with initially.
   * @param freq the frequency counts of the DCT coeffiencts in the image.
   * @param tolerances the tolerances derived from the frequency counts.
   * @param pretend whether to pretend embedding.
   */
  public OutGuessHider(int[] cover, String key, TIntIntMap freq,
    TIntDoubleMap tolerances, short seed, boolean pretend) {
    super(key);
    locked = new BitSet(cover.length);
    this.cover = cover;
    if(!pretend) {
      this.cover = cover.clone();
    }
    /* TODO Unsure about precedence here. Paper not clear. Investigate. */
    alpha = 0.03 * 5000 / cover.length;
    this.tolerances = tolerances;
    originalFrequencies = freq;
    this.seed = seed;
    this.pretend = pretend;
  }

  /**
   * Go ahead and hide the message given, then return the cover image with
   * it.
   * @param message the message to hide.
   * @return a pair containing the carrier and the number of changed bits.
   */
  public Pair<int[], Integer> hide(byte[] message) {
    errors = new TIntIntHashMap(message.length * 4, (float) 0.75, -1, -1);
    int index = hideStatus(message.length);
    hideMessage(message, index);
    if(!pretend) {
      correctErrors();
    }
    return Pair.of(cover, bias + modified);
  }
}
