package com.stegosaurus.jpeg;

/**
 * Constructs regular old JPEG Scan objects.
 */
public class DefaultScanFactory implements ScanFactory<Scan> {

  /**
   * Build a new scan, with no arguments.
   * @return the new scan.
   */
  public Scan build() {
    return new Scan();
  }

  /**
   * Build a new scan by copying over the one given
   * @param scan the scan to copy over.
   * @return the new scan.
   */
  public Scan build(Scan scan) {
    return new Scan(scan);
  }
}
