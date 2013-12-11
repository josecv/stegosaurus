package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.stegostreams.BitInputStream;

/**
 * The actual callable used to embed bits into an image. Should be used with
 * an ImagePermutation instance. Construction is cheap, so should be used
 * in a throwaway manner.
 */
class EmbedProcedure implements TIntIntProcedure {
  /**
   * The message stream.
   */
  private BitInputStream in;

  /**
   * The accessor representing the image.
   */
  private CoefficientAccessor acc;

  /**
   * The sequence used to embed.
   */
  private PMSequence seq;

  /**
   * The number of changes required for the embedding.
   */
  private int changes;

  /**
   * The total number of bits we've dealt with (whether or not they
   * required a change).
   */
  private int bitsSeen;

  /**
   * Whether we are doing any changes to the image.
   */
  private boolean real;

  /**
   * CTOR.
   * @param in the message stream.
   * @param acc the accessor for the image.
   * @param seq the plus minus sequence used to embed.
   * @param real whether to actually do any embedding.
   */
  public EmbedProcedure(BitInputStream in, CoefficientAccessor acc,
      PMSequence seq, boolean real) {
    this.in = in;
    this.acc = acc;
    this.seq = seq;
    this.real = real;
  }

  @Override
  public boolean execute(int index, int val) {
    int bit = in.read();
    if(bit < 0) {
      return false;
    }
    /*
     * A Negative even coefficient is a one, a negative odd coefficient
     * is a zero, a positive even coefficient is a zero, and a positive
     * even coefficient is a one.
     * Hence the following if statement to determine whether something
     * needs to be changed in the carrier.
     */
    if(((val < 0 ? ~val : val) & 1) != bit) {
      changes++;
      if(!real) {
        return true;
      }
      val += (seq.atIndex(bitsSeen) ? 1 : -1);
      if(val == 0) {
        val = (bit == 0 ? -1 : 1);
      }
      acc.setCoefficient(index, val);
    }
    bitsSeen++;
    return true;
  }

  /**
   * Get the change count for this embedding.
   * @return the change count.
   */
  public int getChanges() {
    return changes;
  }
}
