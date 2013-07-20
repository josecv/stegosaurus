package com.stegosaurus.jpeg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;

import com.stegosaurus.huffman.HuffmanDecoder;

/**
 * Any class that operates on JPEG image data as read directly from the file
 * is a JPEG processor. This ABC is charged with operations common to all
 * processors such as the reading of Huffman tables or of subsampling
 * information.
 * TODO: Optimize the way processed data is represented internally.
 */
public abstract class JPEGProcessor {
  /**
   * Ranges indicating where image scan data may be found within our currently
   * processed data.
   * The ranges are indices in the array of processed bytes, with an inclusive
   * start and an exclusive end.
   */
  private List<Range<Integer>> scans = new ArrayList<>();

  /**
   * The entirety of the JPEG file in a single buffer, allowing us to look
   * ahead of whichever byte is currently being looked at.
   */
  private byte[] buffer = null;

  /**
   * JPEG image data that has already been processed.
   */
  private byte[] processed;

  /**
   * Pixel width of the image.
   */
  private int width;

  /**
   * Pixel height of the image.
   */
  private int height;

  /**
   * The index of the last returned segment.
   */
  private int segmentIndex;

  /**
   * Huffman decoders for the usual components.
   * @TODO: This probably sucks performance-wise.
   */
  private Map<Integer, HuffmanDecoder> decoders = new TreeMap<>();

  /**
   * The numbers for the chroma subsampling. Being the ID - 1 (so as to be 0
   * indexed) as a row, and then the H on 0, and the V on 1.
   */
  private byte[][] subsampling;

  /**
   * The input stream that will be providing the image.
   */
  private InputStream image;

  /**
   * Construct a new JPEGProcessor to work with the image given. Note that
   * this does not fully initialize the processor. The init() method should be
   * invoked before it is ready to use.
   *
   * @param image the image to work with.
   */
  public JPEGProcessor(InputStream image) {
    this.image = image;
  }

  /**
   * Construct a new JPEGProcessor to work with the image given. If this CTOR
   * is used, a call to init() will not be necessary (and will result in an
   * exception).
   *
   * @param bytes the image to work with, as a byte array.
   */
  public JPEGProcessor(byte[] bytes) {
    if(bytes == null) {
      throw new IllegalArgumentException("Image data should not be null");
    }
    this.buffer = bytes.clone();
  }

  /**
   * Initialize the processor for use. This is a relatively expensive
   * operation and as such is kept separate from construction.
   * Note that you should close the image stream after calling this method,
   * as it will be consumed entirely.
   *
   * @throws IOException on read error from the image stream
   */
  public void init() throws IOException {
    if(buffer != null) {
      throw new IllegalStateException("This processor has been initialized");
    }
    buffer = IOUtils.toByteArray(image);
  }

  /**
   * Actually process the image scan given. Operates only on the scan, not any
   * other data in the SOS frame.
   */
  protected abstract byte[] process(byte[] scan);

  /**
   * Add the given byte array to the array of already processed data.
   *
   * @param add the bytes to add
   */
  private void addToProcessed(byte[] add) {
    processed = ArrayUtils.addAll(processed, add);
  }

  /**
   * Get the number of bytes that have been processed so far.
   * @return the number of processed bytes
   */
  private int numberOfBytesProcessed() {
    return processed.length;
  }

  /**
   * Read sections until the next section to read is an image scan, populating
   * relevant data structures.
   */
  private void readToScan() {

  }

  /**
   * Read an image scan and return it without applying any processing to it.
   * @return the image scan as a byte array.
   */
  private byte[] readScan() {
    return null;
  }

  /**
   * Add the scan given to the list of known scans, as well as to the
   * processed image data.
   * @param scan the image scan to add.
   * @return the same scan.
   */
  private byte[] addScan(byte[] scan) {
    int start = numberOfBytesProcessed();
    int end = start + scan.length;
    scans.add(Range.between(start, end));
    addToProcessed(scan);
    return scan;
  }

  /**
   * Return the next scan in the image, already processed.
   * @return the processed scan
   */
  public byte[] nextScan() {
    readToScan();
    byte[] scan = process(readScan());
    return addScan(scan);
  }
}
