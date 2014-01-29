package com.stegosaurus.steganographers;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.stegostreams.BitInputStream;


public class FakeEmbedProcedure extends EmbedProcedure {

  public FakeEmbedProcedure(BitInputStream in) {
    super(in);
  }

  public boolean doEmbed(int index, int val, int bit) {
    if(changeNeeded(val, bit)) {
      incrementChanges();
    }
    return true;
  }
}
