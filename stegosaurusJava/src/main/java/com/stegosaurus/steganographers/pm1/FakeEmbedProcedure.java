package com.stegosaurus.steganographers.pm1;

import com.stegosaurus.stegostreams.BitInputStream;


/**
 * Acts like an embed procedure but does not actively embed anything, instead
 * merely counts how many changes would be needed.
 */
public class FakeEmbedProcedure extends EmbedProcedure {

  /**
   * CTOR.
   * @param in the input stream.
   */
  public FakeEmbedProcedure(BitInputStream in) {
    super(in);
  }

  @Override
  public boolean doEmbed(int index, int val, int bit) {
    if(changeNeeded(val, bit)) {
      incrementChanges();
    }
    return true;
  }
}
