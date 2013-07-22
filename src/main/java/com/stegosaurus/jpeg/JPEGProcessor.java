package com.stegosaurus.jpeg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.stegosaurus.WrongImageTypeException;

/**
 * Any class that operates on JPEG image data as read directly from the file
 * is a JPEG processor. This ABC is charged with operations common to all
 * processors such as the reading of Huffman tables or of subsampling
 * information.
 * TODO: Optimize the way processed data is represented internally.
 * TODO: Array operations are super silly in here, and likely not efficient
 */
public abstract class JPEGProcessor {
  /**
   * Ranges indicating where image scan data may be found within our currently
   * processed data.
   * The ranges are indices in the array of processed bytes, with an inclusive
   * start and an exclusive end.
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
  private byte[] processed;

  /**
   * The index of the last returned segment.
   */
  private int segmentIndex = -1;

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
    byte[] segment = nextSegment();
    if(segment[0] != 0xFF || segment[1] != 0xD8) {
      throw new WrongImageTypeException("File not structured like a JPEG file");
    }
  }

  /**
   * Get the next segment in this jpeg file. Notice that this does not stop
   * at RST markers, since those do not denote segments.
   *
   * @return the next segment, which begins with a marker.
   */
  private byte[] nextSegment() {
    if (segmentIndex == buffer.length) {
      return null;
    }
    int marker = findMarker(segmentIndex);
    while(JPEGConstants.isRSTMarker(buffer[marker + 1])) {
      marker = findMarker(marker);
    }
    byte[] retval = Arrays.copyOfRange(buffer, segmentIndex, marker);
    segmentIndex = marker;
    return retval;
  }

  /**
   * Look at the buffer, starting at the location given, until the next marker
   * is found, and return the index where said marker starts.
   *
   * @param start the location of the preceding marker.
   *
   * @return where to find the next marker.
   */
  private int findMarker(int start) {
    return findMarker(start, this.buffer);
  }


  /**
   * Add the given byte array to the array of already processed data.
   *
   * @param add the bytes to add
   */
  private void addToProcessed(byte[] add) {
    processed = ArrayUtils.addAll(processed, add);
  }

  /**
   * Read a start of frame segment's info into the scan given.
   * @param scan the scan to populate
   * @param segment the SOF0 segment to fetch info from.
   */
  private void startOfFrame(Scan scan, byte[] segment) {
    byte numberOfComponents = segment[9];
    scan.setWidth((segment[5] >> 8) + segment[6])
        .setHeight((segment[7] >> 8) + segment[8])
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
  }

  /**
   * Read the info contained in a DHT segment into the Scan data structure
   * given.
   * @param scan the scan to populate
   * @param segment the segment to read from
   */
  private void defineHuffmanTable(Scan scan, byte[] segment) {
    /* TODO: There may be more than one huffman table here. */
    int id = segment[4];
    HuffmanDecoder decoder = new JPEGHuffmanDecoder(Arrays.copyOfRange(segment,
          5, segment.length));
    scan.putDecoder(id, decoder);
  }

  /**
   * Read the info contained in a DRT segment into the scan given.
   * @param scan the scan to populate
   * @param segment the segment to read from
   */
  private void defineRestartInterval(Scan scan, byte[] segment) {
    scan.setRestartInterval((segment[4] >> 8) + segment[5]);
  }

  /**
   * Actually read the scan data and its headers into the Scan object given.
   * @param scan the scan to populate
   * @param segment the scan data to read from
   */
  private void loadScanData(Scan scan, byte[] segment) {
    byte scanComponents = segment[4];
    scan.setScanComponents(scanComponents);
    /* To figure out where the acutal data starts, we have to take into
     * account the 2 marker bytes, the 2 length bytes, the component count
     * byte, and the three final bytes (a total of 8) as well as two bytes
     * per component */
    int dataStart = 8 + (2 * scanComponents);
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
   * Return the next scan in the image, already processed.
   * @return the processed scan
   */
  public Scan nextScan() {
    Scan scan = process(readScan());
    return addScan(scan);
  }

  /**
   * Look at the buffer given, starting at the location given, until the
   * next marker is found, and return the index where said marker starts.
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
    return c;
  }

  /**
   * Given a piece of JPEG data, remove the 0x00 bytes that follow any 0xFF
   * bytes.
   * 
   * @param segment the segment in question
   * @return the segment, with 0x00 bytes removed.
   */
  public static byte[] unescape(byte[] segment) {
    /* TODO This doesn't look terribly efficient */
    ArrayList<Byte> s = new ArrayList<Byte>(Arrays.asList(ArrayUtils
        .toObject(segment)));
    int i;
    for (i = s.size() - 1; i > 0; i--) {
      if (s.get(i) == 0 && s.get(i - 1) == (byte) 0xFF) {
        s.remove(i);
      }
    }
    return ArrayUtils.toPrimitive(s.toArray(new Byte[s.size()]));
  }
}
