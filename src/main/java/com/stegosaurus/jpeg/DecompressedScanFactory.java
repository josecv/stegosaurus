package com.stegosaurus.jpeg;

/**
 * Builds DecompressedScan instances.
 */
public class DecompressedScanFactory implements ScanFactory<DecompressedScan> {

  /**
   * Build a new instance.
   * @return the new scan
   */
  public DecompressedScan build() {
    return new DecompressedScan();
  }

  /**
    * Build a new scan from the one given.
    * @param scan the scan to copy
    * @return the new scan
    */
  public DecompressedScan build(DecompressedScan scan) {
    return new DecompressedScan(scan);
  }
}
