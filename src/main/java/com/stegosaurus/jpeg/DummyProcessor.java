package com.stegosaurus.jpeg;

import java.io.InputStream;

/**
 * A dummy JPEG Processor that simply returns the same scans it is given.
 */
public class DummyProcessor extends JPEGProcessor<Scan> {
  /**
   * Initialize the dummy processor to process the image given.
   * @param in the inputstream for the image to process.
   */
  public DummyProcessor(InputStream in) {
    super(in, new DefaultScanFactory());
  }

  /**
   * Initialize the dummy processor to process the image given.
   * @param bytes the bytes for the image to process.
   */
  public DummyProcessor(byte[] bytes) {
    super(bytes, new DefaultScanFactory());
  }

  /**
   * Pretend to do something to the scan given. Changes nothing, and returns
   * it right back out.
   * @param scan the scan to process.
   * @return that same scan.
   */
  @Override
  public Scan process(Scan scan) {
    return scan;
  }
}

