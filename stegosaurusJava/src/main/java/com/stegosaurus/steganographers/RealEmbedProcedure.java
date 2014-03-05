package com.stegosaurus.steganographers;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.stegostreams.BitInputStream;


/**
 * An embed procedure that _will_ in fact embed into an image.
 */
public class RealEmbedProcedure extends EmbedProcedure {

  /**
   * The sequence used to embed.
   */
  private PMSequence seq;

  /**
   * The accessor representing the image.
   */
  private CoefficientAccessor acc;

  /**
   * The total number of bits we've seen, regardless of whether they required
   * an actual change.
   */
  private int bitsSeen;

  public RealEmbedProcedure(BitInputStream in, CoefficientAccessor acc,
                            PMSequence seq) {
    super(in);
    this.seq = seq;
    this.acc = acc;
    bitsSeen = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doEmbed(int index, int val, int bit) {
    if(changeNeeded(val, bit)) {
      incrementChanges();
      val += (seq.atIndex(bitsSeen) ? 1 : -1);
      if(val == 0) {
        val = (bit == 0 ? -1 : 1);
      }
      acc.setCoefficient(index, val);
    }
    bitsSeen++;
    return true;
  }
}
