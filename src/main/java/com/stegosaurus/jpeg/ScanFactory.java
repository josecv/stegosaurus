package com.stegosaurus.jpeg;

/**
 * Factory for the Scan type. Allows construction of a given Scan type.
 */
public interface ScanFactory<E extends Scan> {
  /**
   * Construct a new scan of the type provided by this factory, using the
   * default argument-less constructor.
   * @return the new scan
   */
  E build();

  /**
   * Construct a new scan of the type provided by this factory, copying one
   * over.
   * @param scan the scan to copy
   * @return the new scan
   */
  E build(E scan);
}
