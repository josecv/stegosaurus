package com.stegosaurus.steganographers.coders;

import gnu.trove.list.TIntList;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.jpeg.DecompressedScan;
import com.stegosaurus.jpeg.JPEGCompressor;
import com.stegosaurus.jpeg.JPEGDecompressor;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.NumUtils;

/**
 * Hides stuff in a JPEG image using the OutGuess algorithm.
 */
public class OutGuessHider extends OutGuess {
  /**
   * The set of modified coefficients. This includes both coefficients used
   * for the actual embedding of image data, and coefficients used for error
   * correction.
   */
  private TIntSet modified;

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
   * Construct a new OutGuessHider instance.
   * @param prng the pseudo random number generator to use.
   */
  public OutGuessHider(Random prng) {
    super(prng);
  }

  /**
   * Hide some status info in the cover, namely some length and a new seed
   * for the prng.
   * @param cover the cover image.
   * @param length the length of the message, to be hidden.
   * @return the next index to use.
   */
  private int hideStatus(int[] cover, int length) {
    byte[] len = NumUtils.byteArrayFromInt(length);
    byte[] seed = generateSeed(2);
    len = ArrayUtils.addAll(len, seed);
    BitInputStream in = new BitInputStream(len);
    int index = 0;
    while(in.available() > 0) {
      if(cover[index] == 0 || cover[index] == 1) {
        index++;
        continue;
      }
      hideAtIndex(cover, in.read(), index);
      index += getRandom(x);
    }
    in.close();
    reseedPRNG(seed);
    return index;
  }

  /**
   * Attempt to correct an error inside the cover medium, at the index given.
   * @param cover the cover image.
   * @param index the index pointing to the error.
   * @return whether the operation worked out.
   */
  private boolean exchDCT(int[] cover, int index) {
    return exchDCT(cover, index, cover[index]);
  }

  /**
   * Attempt to correct an error inside the cover medium, for the coefficient
   * given, and start looking from the index given.
   * @param cover the cover image.
   * @param index the index to start looking at. Not necessarily the same
   * as the index containing the error.
   * @param coeff the coefficient to correct an error for.
   * @return whether the correction was successful.
   */
  private boolean exchDCT(int[] cover, int index, int coeff) {
    int adj = coeff ^ 1;
    for(int j = index - 1; j >= 0; j--) {
      if(cover[j] == coeff && !modified.contains(j)) {
        cover[j] = adj;
        modified.add(j);
        return true;
      }
    }
    return false;
  }

  /**
   * Hide the bit given inside of the cover medium, at the index given, and
   * if necessary attempt to perform some error correction.
   * @param cover the cover medium to hide the bit in.
   * @param bit the bit to hide.
   * @param index the index to hide the bit in.
   */
  private void hideAtIndex(int[] cover, int bit, int index) {
    cover[index] = NumUtils.placeInLSB(cover[index], bit);
    modified.add(index);
    int val = cover[index];
    /* Let's get adjacent coefficient, and see if we can't correct this one
     * and that one at the same time.
     */
    int adj = cover[index] ^ 1;
    if(errors.containsKey(adj) && errors.get(adj) > 0) {
      errors.adjustValue(adj, -1);
    } else if(!errors.containsKey(val) ||
        (errors.containsKey(val) && errors.get(val) < getTolerance(val))) {
      /* The error is still within acceptable bounds, so we can keep going */
      errors.adjustOrPutValue(val, 1, 1);
    } else if(!exchDCT(cover, index)) {
      /* We couldn't fix the error for now, so we'll just have to increment
       * the error counter anyway and keep going. We'll try and fix it later
       * on.
       */
      errors.increment(val);
    }
  }

  /**
   * Calculate the frequency of every DCT component in the cover image given.
   * @param cover the image to calculate frequencies for.
   * @return the DCT frequency table.
   */
  private TIntIntMap calculateFrequencies(int[] cover) {
    TIntIntMap retval = new TIntIntHashMap();
    for(int i : cover) {
      retval.adjustOrPutValue(i, 1, 1);
    }
    return retval;
  }

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
   * @param cover the cover image
   * @param message the message
   * @param index the index to starting hiding in.
   */
  private void hideMessage(int[] cover, byte[] message, int index) {
    BitInputStream stream = new BitInputStream(message);
    while(stream.available() > 0) {
      int i = 0;
      /* We'll change the interval every 8 bits */
      index += getRandom(getInterval(cover, index, stream.available()));
      while(i < 8) {
        if(cover[index] == 0 || cover[index] == 1) {
          index++;
          continue;
        }
        hideAtIndex(cover, stream.read(), index);
        index++;
        i++;
      }
    }
    stream.close();
  }

  /**
   * Correct any remaining errors in the cover image given.
   * @param cover the cover image.
   */
  private void correctErrors(final int[] cover) {
    /* We'll just iterate over everything and try to correct any errors
     * we can */
    errors.forEachEntry(new TIntIntProcedure() {
      public boolean execute(int coeff, int errors) {
        while(errors > 0) {
          errors--;
          exchDCT(cover, cover.length, coeff);
        }
        return true;
      }
    });
  }

  /**
   * Hide the message given in the cover image data provided.
   * The data provided should be some JPEG scan data. Note that
   * it is mutated.
   * @param cover the cover image data to use.
   * @param message the message to hide.
   * @throws IOException on read error from the cover.
   */
  public void hide(int[] cover, byte[] message) {
    modified = new TIntHashSet();
    originalFrequencies = calculateFrequencies(cover);
    /* TODO Unsure about precedence here. Paper not clear. Investigate. */
    alpha = 0.03 * 5000 / cover.length;
    tolerances = new TIntDoubleHashMap();
    errors = new TIntIntHashMap();
    int index = hideStatus(cover, message.length);
    hideMessage(cover, message, index);
    correctErrors(cover);
  }

  /**
   * Hide the message given in the cover image provided. Return the stegano
   * image containing the message.
   * @param cover the cover image to use.
   * @param message the message to hide.
   * @return the image, containing the message.
   * @throws IOException on read error from the cover.
   */
  public byte[] hide(InputStream cover, byte[] message) throws IOException {
    JPEGDecompressor jpeg = new JPEGDecompressor(cover);
    jpeg.init();
    DecompressedScan scan = getBestScan(jpeg.processImage());
    /* TODO Don't just get the 0th buffer, for fucks sake */
    TIntList data = scan.getCoefficientBuffers().get(0);
    hide(data, message);
    JPEGCompressor comp = new JPEGCompressor();
    comp.process(scan);
    jpeg.refresh();
    return jpeg.getProcessed();
  }

  /**
   * Hide the message given in the cover image data provided.
   * The data provided should be some JPEG scan data. Note that
   * it is mutated.
   * @param cover the cover image data to use.
   * @param message the message to hide.
   * @throws IOException on read error from the cover.
   */
  public void hide(TIntList cover, byte[] message) {
    int[] data = cover.toArray();
    hide(data, message);
    cover.set(0, data);
  }

}
