package com.stegosaurus.jpeg;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.stegosaurus.WrongImageTypeException;
import com.stegosaurus.stegutils.NumUtils;

/**
 * Any class that operates on JPEG image data as read directly from the file
 * is a JPEG processor. This ABC is charged with operations common to all
 * processors such as the reading of Huffman tables or of subsampling
 * information.
 * TODO Array operations are super silly in here, and likely not efficient
 * TODO A smarter way to denote that we're done (ie no nulls!)
 */
public abstract class JPEGProcessor {
  /**
   * The scans that have been processed by this object.
   */
  private List<Scan> scans = new ArrayList<>();

  /**
   * The entirety of the JPEG file in a single buffer, allowing us to look
   * ahead of whichever byte is currently being looked at.
   */
  private byte[] buffer = null;

  /**
   * JPEG image data that has already been processed.
   */
  private TByteList processed;

  /**
   * The index of the last returned segment.
   */
  private int segmentIndex = 0;

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
    init(bytes);
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
    init(IOUtils.toByteArray(image));
  }

  /**
   * Actually process the image scan given.
   *
   * @param scan the scan to process
   * @return the processed scan.
   */
  protected abstract Scan process(Scan scan);

  /**
   * Initialize the processor for use with the given image data.
   *
   * @param bytes the JPEG file data that this processor will be dealing with.
   */
  private void init(byte[] bytes) {
    buffer = bytes.clone();
    /* There is, of course, no guarantee that the total amount of processed
     * bytes won't exceed the size of the input, but it's as good a start
     * as any.
     */
    processed = new TByteArrayList(buffer.length);
    byte[] segment = nextSegment();
    if(segment[0] != (byte) 0xFF || segment[1] != JPEGConstants.SOI_MARKER) {
      throw new WrongImageTypeException("File not structured like a JPEG file");
    }
    addToProcessed(segment);
  }

  /**
   * Get the next segment in this jpeg file, and set the new segmentIndex.
   * Notice that this does not stop at RST markers, since those do not
   * denote segments.
   *
   * @return the next segment, which begins with a marker.
   */
  private byte[] nextSegment() {
    byte[] retval = nextSegment(segmentIndex, buffer);
    if(retval != null) {
      segmentIndex += retval.length;
    }
    return retval;
  }

  /**
   * Get the next segment in the buffer given.
   * Notice that this does not stop at RST markers, since those do not
   * denote segments.
   * @param start the index to start looking at
   * @param buffer the buffer
   * @return the next segment, which begins with a marker.
   */
  public static byte[] nextSegment(int start, byte[] buffer) {
    if (start == buffer.length) {
      return null;
    }
    int marker = findMarker(start, buffer);
    /* If we ran into an RST marker instead of one that actually denotes
     * a segment, we skip it. We keep going until we find something
     * we're happy with
     */
    while(marker < buffer.length &&
          JPEGConstants.isRSTMarker(buffer[marker + 1])) {
      marker = findMarker(marker, buffer);
    }
    return ArrayUtils.subarray(buffer, start, marker);
  }


  /**
   * Add the given byte array to the array of already processed data.
   *
   * @param add the bytes to add
   */
  private void addToProcessed(byte[] add) {
    processed.addAll(add);
  }

  /**
   * Read a start of frame segment's info into the scan given.
   * @param scan the scan to populate
   * @param segment the SOF0 segment to fetch info from.
   */
  private void startOfFrame(Scan scan, byte[] segment) {
    byte numberOfComponents = segment[9];
    scan.setWidth(NumUtils.intFromBytes(segment, 5, 7))
        .setHeight(NumUtils.intFromBytes(segment, 7, 9))
        .setFrameComponents(numberOfComponents);
    byte[][] subsampling = new byte[numberOfComponents][];
    for (int i = 0; i < numberOfComponents; i++) {
      subsampling[i] = new byte[2];
      /* Subsampling information for every component is stored in a single
       * byte, where the four most significant bits are the horizontal info,
       * and the four least significant bits are the vertical info */
      subsampling[i][0] = (byte) (segment[11 + 3 * i] >> 4);
      subsampling[i][1] = (byte) (segment[11 + 3 * i] & 0x0F);
    }
    scan.setSubsampling(subsampling);
  }

  /**
   * Read the info contained in a DHT segment into the Scan data structure
   * given.
   * @param scan the scan to populate
   * @param segment the segment to read from
   */
  private void defineHuffmanTable(Scan scan, byte[] segment) {
    /* TODO There may be more than one huffman table here. */
    int id = segment[4];
    HuffmanDecoder decoder = new JPEGHuffmanDecoder(
      ArrayUtils.subarray(segment, 5, segment.length));
    scan.putDecoder(id, decoder);
  }

  /**
   * Read the info contained in a DRT segment into the scan given.
   * @param scan the scan to populate
   * @param segment the segment to read from
   */
  private void defineRestartInterval(Scan scan, byte[] segment) {
    scan.setRestartInterval(NumUtils.intFromBytes(segment, 4, 6));
  }

  /**
   * Actually read the scan data and its headers into the Scan object given.
   * @param scan the scan to populate
   * @param segment the scan data to read from
   */
  private void loadScanData(Scan scan, byte[] segment) {
    byte scanComponents = segment[4];
    scan.setScanComponents(scanComponents);
    /* We next want to load up relevant component information */
    for(int i = 0; i < scanComponents; i++) {
      scan.putTableId(segment[5 + (i * 2)], segment[6 + (i * 2)]);
    }
    /* To figure out where the acutal data starts, we have to take into
     * account the 2 marker bytes, the 2 length bytes, the component count
     * byte, and the three final bytes (a total of 8) as well as two bytes
     * per component */
    int dataStart = 8 + (2 * scanComponents);
    /* We need to add the stuff before the actual scan (ie the marker and
     * component info) */
    addToProcessed(ArrayUtils.subarray(segment, 0, dataStart));
    byte[] data = ArrayUtils.subarray(segment, dataStart, segment.length);
    scan.setData(data);
  }

  /**
   * Read an image scan with associated data, and return it without applying
   * any processing to it.
   * @return the image scan.
   */
  private Scan readScan() {
    Scan scan = new Scan();
    if(scans.size() > 0) {
      scan = new Scan(scans.get(scans.size() - 1));
    }
    byte[] segment = nextSegment();
    while(segment != null && segment[1] != JPEGConstants.SOS_MARKER) {
      addToProcessed(segment);
      switch(segment[1]) {
        case JPEGConstants.SOF0_MARKER:
          startOfFrame(scan, segment);
          break;
        case JPEGConstants.DHT_MARKER:
          defineHuffmanTable(scan, segment);
          break;
        case JPEGConstants.DRI_MARKER:
          defineRestartInterval(scan, segment);
          break;
      }
      segment = nextSegment();
    }
    if(segment == null) {
      return null;
    }
    loadScanData(scan, segment);
    return scan;
  }

  /**
   * Add the scan given to the list of known scans, as well as to the
   * processed image data.
   * @param scan the image scan to add.
   * @return the same scan.
   */
  private Scan addScan(Scan scan) {
    addToProcessed(scan.getData());
    scans.add(scan);
    return scan;
  }

  /**
   * Return the next scan in the image, already processed. It is also added
   * to the list of scans.
   * @return the processed scan
   */
  public Scan nextScan() {
    Scan scan = readScan();
    /* All done */
    if(scan == null) {
      return scan;
    }
    return addScan(process(scan));
  }

  /**
   * Get the nth processed scan.
   * @param n the scan to get
   * @return the nth scan
   */
  public Scan getScan(int n) {
    return scans.get(n);
  }

  /**
   * Return all the scans that have been processed.
   * @return the list of scans.
   */
  public List<Scan> getScans() {
    return new ArrayList<Scan>(scans);
  }

  /**
   * Process the remainder of the image.
   * @return all the scans in the image, after processing.
   */
  public List<Scan> processImage() {
    Scan scan = nextScan();
    while(scan != null) {
      scan = nextScan();
    }
    return getScans();
  }

  /**
   * Get the already processed bytes.
   * @return the processed bytes
   */
  public byte[] getProcessed() {
    return processed.toArray();
  }

  /**
   * Look at the buffer given, starting at the location given, until the
   * next marker is found, and return the index where said marker starts.
   * If no marker is to be found, return the length of the buffer.
   *
   * @param start the location of the preceding marker.
   * @param buffer the buffer to look at
   *
   * @return where to find the next marker.
   */
  public static int findMarker(int start, byte[] buffer) {
    int c = start;
    /* Prevent an overflow */
    if (c + 2 == buffer.length) {
      return buffer.length;
    }
    /* We're only interested in actual markers, not padding and/or legitimate
     * FF bytes, so we ensure that the byte in front of any FF is not
     * a 00 or another FF */
    short i = 0, j = 0;
    while ((i != 0xFF || j == 0xFF || j == 0) && (c + 2 < buffer.length)) {
      i = (short) (buffer[c + 1] & 0xFF);
      j = (short) (buffer[c + 2] & 0xFF);
      c++;
    }
    if((i != 0xFF || j == 0xFF || j == 0) && (c + 2 == buffer.length)) {
      return buffer.length;
    }
    return c;
  }

  /**
   * Given a piece of JPEG data, add 0x00 bytes in front of any 0xFF bytes.
   * For this to work without corrupting any markers, the data given should
   * be guaranteed to be marker-free, obviously. The data given is not
   * mutated, but copied.
   * @param segment the data to work with.
   * @return the segment, with 0x00 bytes in front of 0xFF bytes
   */
  public static byte[] escape(byte[] segment) {
    TByteList list = new TByteArrayList(segment);
    for(int i = list.size() - 1; i >= 0; i--) {
      if(list.get(i) == (byte) 0xFF) {
        list.insert(i + 1, (byte) 0);
      }
    }
    return list.toArray();
  }

  /**
   * Given a piece of JPEG data, remove the 0x00 bytes that follow any 0xFF
   * bytes. The segment given is not mutated at all.
   *
   * @param segment the segment in question
   * @return the segment, with 0x00 bytes removed.
   */
  public static byte[] unescape(byte[] segment) {
    TByteList s = new TByteArrayList(segment);
    int i;
    for (i = s.size() - 1; i > 0; i--) {
      if (s.get(i) == 0 && s.get(i - 1) == (byte) 0xFF) {
        s.removeAt(i);
      }
    }
    return s.toArray();
  }
}
