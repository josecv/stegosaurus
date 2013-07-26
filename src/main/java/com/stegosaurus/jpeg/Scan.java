package com.stegosaurus.jpeg;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.stegosaurus.huffman.HuffmanDecoder;

/**
 * An image scan within a JPEG file. This data structure contains not only the
 * scan data itself, but also any relevant Huffman tables and subsampling
 * information.
 * In addition, it is possible to iterate over the scan. This is akin to
 * spliting it up by reset markers and iterating over the discrete parts.
 * Note that it is not necessarily the case that the scan data contained by
 * this structure be in JPEG format. Since manipulation is allowed, arbitrary
 * data may be found within the scan.
 */
public class Scan implements Iterable<byte[]> {
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
   * indexed) as a row, and then the horizontal on 0, and the vertical on 1.
   * @TODO Better handling of this.
   */
  private byte[][] subsampling;

  /**
   * Huffman decoders for the usual components.
   * @TODO: This probably sucks performance-wise.
   */
  private Map<Integer, HuffmanDecoder> decoders = new TreeMap<>();

  /**
   * A map going from a component's id to its huffman table information.
   */
  private Map<Integer, Integer> componentTables = new TreeMap<>();

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
    this.componentTables.putAll(scan.componentTables);
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
   * Get huffman table info for the component with the id given.
   * @param id the id of the component to fetch info for.
   * @return the huffman table info for the component given.
   */
  public int getTableId(int componentId) {
    return componentTables.get(componentId);
  }

  /**
   * Associate decoder info with the component given.
   * @param componentId the component to associate the huffman table info to
   * @param tableId the huffman table info to associate
   * @return this object.
   */
  public Scan putTableId(int componentId, int tableId) {
    componentTables.put(componentId, tableId);
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
   * row, and then the horizontal on 0, and the vertical on 1.
   * @return the subsampling
   */
  public byte[][] getSubsampling() {
    return subsampling;
  }

  /**
   * Set the subsampling ratios. Being the ID - 1 (so as to be 0 indexed) as a
   * row, and then the horizontal on 0, and the vertical on 1.
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

  /**
   * Get the maximum sampling frequency of any component in the direction
   * given.
   * @param direction the direction, being 0 for horizontal, 1 for vertical
   * @return the maximium sampling frequency
   */
  private byte getSamplingMax(int direction) {
    byte max = subsampling[0][direction];
    for(int i = 0; i < frameComponents; i++) {
      if(max < subsampling[i][direction]) {
        max = subsampling[i][direction];
      }
    }
    return max;
  }

  /**
   * Get the maximum horizontal sampling frequency out of those of the components
   * known to this scan.
   * @return the maximium horizontal sampling frequency
   */
  public byte getHSamplingMax() {
    return getSamplingMax(0);
  }

  /**
   * Get the maximum vertical sampling frequency out of those of the components
   * known to this scan.
   * @return the maximum vertical sampling frequency.
   */
  public byte getVSamplingMax() {
    return getSamplingMax(1);
  }

  /**
   * Return the number of MCUs that this scan contains, horizontally.
   * @return the number of MCUs in the x direction.
   */
  public int getMCUx() {
    return (width + 8 * getHSamplingMax() - 1) / (8 * getHSamplingMax());
  }

  /**
   * Return the number of MCUs that this scan contains, vertically.
   * @return the number of MCUs in the y direction.
   */
  public int getMCUy() {
    return (height + 8 * getVSamplingMax() - 1) / (8 * getVSamplingMax());
  }

  /**
   * Get the total number of coefficients within this scan.
   * @return the total number of coefficients in this scan.
   */
  public int getCoefficientCount() {
    int retval = 64 * getMCUx() * getMCUy();
    for(int i = 0; i < subsampling.length; i++) {
      retval *= subsampling[i][0] * subsampling[i][1];
    }
    return retval;
  }

  /**
   * Return an iterator over this scan's RST marker separated sections.
   */
  @Override
  public Iterator<byte[]> iterator() {
    return new ScanIterator(data);
  }
}