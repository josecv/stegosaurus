package com.stegosaurus.steganographers.coders.outguess;

import java.io.IOException;
import java.io.InputStream;

import com.stegosaurus.jpeg.DecompressedScan;
import com.stegosaurus.jpeg.JPEGDecompressor;

/**
 * Detects images containing messages embedded with the OutGuess algorithm.
 * Produces estimates of message length.
 */
public class OutGuessDetect {
  /**
   * The JPEG image.
   */
  private InputStream image = null;

  /**
   * The scan.
   */
  private DecompressedScan scan = null;

  /**
   * Construct an OutGuessDetect object, which should then be initialized.
   * @param image the JPEG image to work with.
   */
  public OutGuessDetect(InputStream image) {
    this.image = image;
  }

  /**
   * Construct an OutGuessDetect object, which should NOT be initialized again.
   * @param scan the decompressed scan to use.
   */
  public OutGuessDetect(DecompressedScan scan) {
    this.scan = scan;
  }

  /**
   * Initialize this object.
   * This is a somewhat costly operation that involves reading the entirety of
   * the image into a buffer and decompressing it.
   * @throws IOException if the image given throws on read.
   */
  public void init() throws IOException {
    if(image == null) {
      throw new IllegalStateException("Detector already initialized");
    }
    JPEGDecompressor decomp = new JPEGDecompressor(image);
    decomp.init();
    scan = OutGuessUtils.getBestScan(decomp.processImage());
    image = null;
  }

  /**
   * Get the blockiness of a given scan.
   * @param s the scan
   * @return the blockiness
   */
  private int getBlockiness(DecompressedScan s) {
    return 0;
  }

  /**
   * Crop the scan given by 4 columns. Does not alter the scan given, but
   * instead returns a new one.
   * @param s the scan.
   * @return the cropped scan.
   */
  private DecompressedScan crop(DecompressedScan s) {
    return new DecompressedScan(s);
  }

  /**
   * Embed a maximum length message into the scan given.
   * @param s the scan.
   */
  private void embedMax(DecompressedScan s) {

  }

  /**
   * Get an estimate of the length of a message embedded in the picture.
   * @return the estimate.
   */
  public int getEstimate() {
    return 0;
  }
}
