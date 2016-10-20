package com.stegosaurus.steganographers.utils;

import com.stegosaurus.steganographers.pm1.PMSequence;

/**
 * A dummy PM sequence that returns true for every even index and false
 * for every odd index.
 */
public class DummyPMSequence implements PMSequence {
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean atIndex(int index) {
    return index % 2 == 0;
  }
}

