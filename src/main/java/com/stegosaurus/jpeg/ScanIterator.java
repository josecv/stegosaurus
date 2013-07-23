package com.stegosaurus.jpeg;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Iterates over the parts of a scan, being split up by reset markers.
 */
public class ScanIterator implements Iterator<byte[]> {
  /**
   * The scan data itself.
   */
  private byte[] data;

  /**
   * The start index of the last returned part.
   */
  private int lastIndex = 0;

  /**
   * Construct a new scan iterator to iterate over the scan data given.
   */
  public ScanIterator(byte[] scan) {
    this.data = scan.clone();
  }

  /**
   * Return the next piece of this scan. Note that this includes its reset
   * marker, if any.
   * @return the next piece of the scan, up to but excluding the next reset
   * marker.
   * @throws NoSuchElementException if the scan iterator has run out of
   * elements to return
   */
  @Override
  public byte[] next() {
    if(!hasNext()) {
      throw new NoSuchElementException();
    }
    int nextIndex = JPEGProcessor.findMarker(lastIndex, data);
    byte[] retval = ArrayUtils.subarray(data, lastIndex, nextIndex);
    lastIndex = nextIndex;
    return retval;
  }

  /**
   * Return whether this iterator can keep going.
   */
  @Override
  public boolean hasNext() {
    return lastIndex == data.length;
  }

  /**
   * Attempt to remove the last returned element from the iterator. This
   * is not supported, and will always throw.
   * @throws UnsupportedOperationException always.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Cannot remove pieces of a scan");
  }
}

