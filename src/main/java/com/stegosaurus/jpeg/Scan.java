package com.stegosaurus.jpeg;

import java.util.Map;
import java.util.TreeMap;

import com.stegosaurus.huffman.HuffmanDecoder;

/**
 * An image scan within a JPEG file. This data structure contains not only the
 * scan data itself, but also any relevant Huffman tables and subsampling
 * information.
 * Note that it is not necessarily the case that the scan data contained by
 * this structure be in JPEG format. Since manipulation is allowed, arbitrary
 * data may be found within the scan.
 */
public class Scan {
  /**
   * The offset within the JPEG file where this scan's SOS marker is placed.
   */
  private int startOfScan;

  /**
   * The offset within the JPEG file where this scan ends.
   */
  private int endOfScan;

  /**
   * Pixel width of the scan.
   */
  private int width;

  /**
   * Pixel height of the scan.
   */
  private int height;

  /**
   * The number of components declared in the start of frame for this scan.
   */
  private byte frameComponents;

  /**
   * The number of components declared in the start of scan for this scan.
   */
  private byte scanComponents;

  /**
   * The restart interval as of this scan.
   */
  private int restartInterval;

  /**
   * The scan data itself.
   */
  private byte[] data;

  /**
   * The numbers for the chroma subsampling. Being the ID - 1 (so as to be 0
   * indexed) as a row, and then the H on 0, and the V on 1.
   * @TODO Better handling of this.
   */
  private byte[][] subsampling;

  /**
   * Huffman decoders for the usual components.
   * @TODO: This probably sucks performance-wise.
   */
  private Map<Integer, HuffmanDecoder> decoders = new TreeMap<>();

  /**
   * Default CTOR.
   */
  public Scan() { }

  /**
   * Copy CTOR. Construct a new scan taking over everything possible from the
   * one given.
   */
  public Scan(Scan scan) {
    this.data = scan.data;
    this.height = scan.height;
    this.width = scan.width;
    this.startOfScan = scan.startOfScan;
    this.endOfScan = scan.endOfScan;
    this.frameComponents = scan.frameComponents;
    this.subsampling = scan.subsampling.clone();
    this.decoders.putAll(scan.decoders);
  }

  /**
   * Get the huffman decoder for a specific id.
   *
   * @param id the id of the decoder to get
   * @return the decoder
   */
  public HuffmanDecoder getDecoder(int id) {
    return decoders.get(id);
  }

  /**
   * Add a decoder with the id given. If one already exists for that id,
   * it is replaced.
   *
   * @param id the id of the decoder to place.
   * @param decoder the decoder.
   * @return this scan.
   */
  public Scan putDecoder(int id, HuffmanDecoder decoder) {
    decoders.put(id, decoder);
    return this;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   * @return this scan
   */
  public Scan setWidth(int width) {
    this.width = width;
    return this;
  }

  /**
   * Get the number of components as declared in the SOF segment.
   * @return the frameComponents
   */
  public byte getFrameComponents() {
    return frameComponents;
  }

  /**
   * Set the number of components as declared in the SOF segment.
   * @param frameComponents the frameComponents to set
   */
  public Scan setFrameComponents(byte frameComponents) {
    this.frameComponents = frameComponents;
    return this;
  }

  /**
   * Get the number of components as declared in the SOS segment.
   * @return the scanComponents
   */
  public byte getScanComponents() {
    return scanComponents;
  }

  /**
   * Set the number of components as declared in the SOS segment.
   * @param scanComponents the scanComponents to set
   * @return this object
   */
  public Scan setScanComponents(byte scanComponents) {
    this.scanComponents = scanComponents;
    return this;
  }

  /**
   * @return the restartInterval
   */
  public int getRestartInterval() {
    return restartInterval;
  }

  /**
   * @param restartInterval the restartInterval to set
   */
  public Scan setRestartInterval(int restartInterval) {
    this.restartInterval = restartInterval;
    return this;
  }

  /**
   * @return the data
   */
  public byte[] getData() {
    return data;
  }

  /**
   * @param data the data to set
   * @return this scan
   */
  public Scan setData(byte[] data) {
    this.data = data.clone();
    return this;
  }

  /**
   * Get the subsampling ratios. Being the ID - 1 (so as to be 0 indexed) as a
   * row, and then the H on 0, and the V on 1.
   * @return the subsampling
   */
  public byte[][] getSubsampling() {
    return subsampling;
  }

  /**
   * Set the subsampling ratios. Being the ID - 1 (so as to be 0 indexed) as a
   * row, and then the H on 0, and the V on 1.
   * @param subsampling the subsampling to set
   * @return this object
   */
  public Scan setSubsampling(byte[][] subsampling) {
    this.subsampling = subsampling.clone();
    return this;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param height the height to set
   * @return this scan
   */
  public Scan setHeight(int height) {
    this.height = height;
    return this;
  }

  /**
   * Figure out whether restart markers are allowed to appear inside of this
   * scan. This is equivalent to getRestartInterval() != 0.
   * @return whether RST markers are permitted in the scan.
   */
  public boolean isRSTEnabled() {
    return restartInterval != 0;
  }
}
