package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegostreams.SequentialBitInputStream;
import com.stegosaurus.stegutils.StegUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * The JPEG standard splits a file into chunks delimited by markers which are
 * the 0xFF byte followed by any non-zero byte. Should an actual 0xFF be
 * desired, it is escaped by placing a zero byte after it. Should a 0xFF be
 * followed by one or more 0xFFs, it is not a marker but padding, usually
 * preceding a marker, and should be ignored.
 */
/**
 * Deals with JPG images as carriers.
 * 
 * @author joe
 */
public abstract class JPEGCoder extends ImgCoder {

  /**
   * Code for the start of image marker.
   */
  protected static final byte SOI_MARKER = (byte) 0xD8;

  /**
   * Code for the start of frame marker. Indicates this as a baseline DCT JPG.
   * Gives width, height, number of components, and component subsampling.
   */
  protected static final byte SOF0_MARKER = (byte) 0xC0;

  /**
   * Start of frame marker for extended baseline DCT mode.
   */
  protected static final byte SOF1_MARKER = (byte) 0xC1;

  /**
   * Code for the start of frame marker. Indicates this as a progressive DCT
   * JPG. Same as with SOF0.
   */
  protected static final byte SOF2_MARKER = (byte) 0xC2;

  /**
   * Code for the Huffman tables marker.
   */
  protected static final byte DHT_MARKER = (byte) 0xC4;

  /**
   * Code for the quantization tables marker.
   */
  protected static final byte DQT_MARKER = (byte) 0xDB;

  /**
   * Code for the Define Restart Interval marker. Specifies the intervals
   * between RSTn markers.
   */
  protected static final byte DRI_MARKER = (byte) 0xDD;

  /**
   * Code for the start of scan marker. Starts the image scan, from top to
   * bottom.
   */
  protected static final byte SOS_MARKER = (byte) 0xDA;

  /**
   * Code for a text comment.
   */
  protected static final byte COM_MARKER = (byte) 0xFE;

  /**
   * Code for the end of image marker.
   */
  protected static final byte EOI_MARKER = (byte) 0xD9;

  /**
   * A Buffer with the entire JPG file. Used so that we can look ahead.
   */
  private byte[] buffer;

  /**
   * How many components the image has. Usually three
   */
  private byte numberOfComponents;

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
  protected int segmentIndex;

  /**
   * Processed image data.
   */
  protected byte[] data;

  /**
   * The image data that we are currently working on.
   */
  protected byte[] workingData;

  /**
   * Huffman decoders for the usual components.
   * @TODO: This probably sucks performance-wise.
   */
  protected Map<Integer, HuffmanDecoder> decoders;

  /**
   * The numbers for the chroma subsampling. Being the ID - 1 (so as to be 0
   * indexed) as a row, and then the H on 0, and the V on 1.
   */
  protected byte[][] subsampling;

  /**
   * The total number of coefficients in the image.
   */
  private int coeffCount;

  private Logger log = LoggerFactory.getLogger(JPEGCoder.class);

  /**
   * Return whether the given byte is an RSTn marker. It would be indicated by
   * being 0xDn, where n=0..7.
   * 
   * @param b the byte to test.
   * @return true if it is an RSTn marker.
   */
  protected static boolean isRSTMarker(byte b) {
    byte lsb = (byte) (b & 0x0F);
    byte msb = (byte) (b & 0xF0);
    return 0 <= lsb && lsb <= 7 && msb == 0xD0;
  }

  /**
   * Return whether the given byte is an application specific marker. It would
   * be indicated by being 0xEn.
   * 
   * @param b
   *            the byte to test.
   * @return true if it is an APPn marker.
   */
  protected static boolean isAPPMarker(byte b) {
    return (b & 0xF0) == 0xE0;
  }

  /**
   * Given a JPEG segment, remove the 0x00 bytes that follow any legitimate
   * 0xFF bytes, so that the data might be dealt with.
   * 
   * @param segment
   *            the segment in question
   * @return the segment, with 0x00 bytes removed.
   */
  public static byte[] unescape(byte[] segment) {
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

  /**
   * Initialize the JPGCoder.
   * 
   * @param in the InputStream with the JPEG image.
   */
  public JPEGCoder(InputStream in) throws IOException {
    super(in);
    buffer = new byte[instream.available()];
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = (byte) instream.read();
    }
    segmentIndex = 0;
    decoders = new TreeMap<Integer, HuffmanDecoder>();
    data = new byte[0];
  }

  /**
   * Get the next segment in this jpeg file.
   * 
   * @return the next segment, which begins with a marker.
   * @throws IOException on read error
   */
  public byte[] nextSegment() throws IOException {
    if (segmentIndex == -1) {
      return null;
    }
    int marker = findMarker(segmentIndex);
    byte[] retval = Arrays.copyOfRange(buffer, segmentIndex, marker);
    segmentIndex = marker;
    if (segmentIndex == buffer.length) {
      segmentIndex = -1;
    }
    return retval;
  }

  /**
   * Look at the buffer, starting at the location of the previous marker,
   * until the next marker is found, and return the index where the marker
   * starts.
   * 
   * @param start
   *            the location of the preceding marker.
   * 
   * @return where to find the next marker.
   * @throws IOException
   *             on file read error.
   */
  protected int findMarker(int start) throws IOException {
    int c = start;
    /* FUCK ELEGANCE!! */
    /* TODO: Add elegance */
    if (c + 2 == buffer.length) {
      return buffer.length;
    }
    short i = 0, j = 0;
    while ((i != 0xFF || j == 0xFF || j == 0) && (c + 2 < buffer.length)) {
      i = (short) (buffer[c + 1] & 0xFF);
      j = (short) (buffer[c + 2] & 0xFF);
      c++;
    }
    return c;
  }

  /**
   * Figure out the number of coefficients in the image we're dealing with.
   * This makes use of the subsampling, dimensions and component count we've
   * read from the last SOF0 block, so if the image contains multiple SOF0
   * blocks (which is likely illegal anyways) bad things will happen.
   *
   * @return this object
   */
  private JPEGCoder calculateCoeffCount() {
    /* This was taken pretty much verbatim from Westfeld's source code.
     * It can likely be simplified to a loopless algorithm that works with a
     * couple of multiplications but I'm lazy */
    coeffCount = 0;
    /* Loop over the number of blocks across and the number of blocks down */
    for(int h = 0; h < (height / 8 + 1); h++) {
      for(int w = 0; w < (width / 8 + 1); w++) {
        /* Loop over the components (L, Cb, Cr) */
        for(int c = 0; c < numberOfComponents; c++) {
          /* Loop over the blocks themselves in the order of h then v */
          for(int i = 0; i < subsampling[c][0]; i++) {
            for(int j = 0; j < subsampling[c][1]; j++) {
              /* Finally add up the coefficients */
              coeffCount += 64;
            }
          }
        }
      }
    }
    return this;
  }

  /* TODO: Huffman decoding capabilities to go into their own class */
  private byte[] decode(byte[] segment) throws IOException {
    log.info("Starting huffman decoding");
    byte[] decoded = new byte[10000000];
    byte component = 0;
    int total = 0;
    /* The previous dc coeff to be read */
    int lastDc = 0;
    BitInputStream stream = new SequentialBitInputStream(segment);
    bigloop : while(stream.available() > 0) {
      for(int n = 0; n < numberOfComponents; n++) {
        /* TODO: WTF Happens with vertical subsampling?? */
        for(int j = 0; j < subsampling[n][0] + subsampling[n][1]; j++) {
          /* Bits [0-3] are the number, itself ranging from 0 to 3, and bit
           * 4 is the component. Bits [5-7] are unused. */
          int id = ((n == 0 ? n : 1) << 4) + component;
          if(!decoders.containsKey(id)) {
            throw new IllegalStateException("Huffman decoder for key " + id +
              " is nowhere to be found");
          }
          HuffmanDecoder decoder = decoders.get(id);
          /* Decode a DC coeff.
           * TODO: Pull out */
          if(component == 0) {
            log.info("decoding dc coefficient");
            /* Get the length of the rawdiff */
            int len = decoder.decodeNext(stream) & 0xFF;
            /* And now for the DC itself */
            byte[] dcBytes = new byte[len];
            stream.read(dcBytes, 0, len);
            int diff;
            /* For whatever reason, the way this works is that we get the raw
             * diff, here loaded into diffBytes, and if it starts with a 1,
             * that becomes the diff. If it doesn't, we have that:
             * diff = -(~rawdiff)
             * Which is utterly strange and cannot be done in the natural
             * fashion, but has to be done bit by bit. If we were to take
             * diff = rawdiff and then perform the ~, Java would flip a whole
             * bunch of bits that weren't in rawdiff originally, resulting in
             * an entirely different number */
            if(len == 0) {
              diff = 0;
            } else if(dcBytes[0] != 1) {
              for(int i = 0; i < len; i++) {
                dcBytes[i] = (byte) ~dcBytes[i];
              }
              diff = -(StegUtils.intFromBits(dcBytes, dcBytes.length));
            } else {
              diff = StegUtils.intFromBits(dcBytes, dcBytes.length);
            }
            int dc = lastDc + diff;
            /* XXX FIGURE OUT WHAT THE FUCK IS UP WITH BYTE/INT/ETC ASAP! */
            decoded[total] = (byte) dc;
            total++;
            lastDc = dc;
            component = 1;
          } else {
            log.info("starting runlength decoding of 63 AC coefficients");
            component = 0;
            byte ac = 0;
            while(ac < 63) {
              byte symbol1 = decoder.decodeNext(stream);
              int runlength = symbol1 & 0x0F;
              int size = symbol1 & 0xF0;
              if(runlength == 0 && size == 0) {
                /* EOB */
                int missing = 63 - ac;
                ac += missing;
                total += missing;
              } else {
                byte amplitude;
                try {
                  amplitude = decoder.decodeNext(stream);
                } catch(NullPointerException npe) {
                  continue bigloop;
                }
                if(runlength == 15 && size == 0 && amplitude == 0) {
                  ac += 16;
                  total += 16;
                } else {
                  ac += runlength + 1;
                  total += runlength + 1;
                  decoded[total] = amplitude;
                }
              }
            }
          }
        }
      }
    }
    log.info("Huffman decoding finished");
    assert stream.available() == 0 : "Missing bits...";
    return decoded;
  }

  public JPEGCoder loadScan() throws IOException {
    /* TODO: Tidy */
    /* The marker bytes */
    int header = 2;
    int totalLen = (workingData[0] << 8) + workingData[1];
    header += 2;
    byte components = workingData[2];
    assert this.numberOfComponents == components;
    header += 1;
    header += components * 2;
    header += 3;
    data = ArrayUtils.addAll(data,
      ArrayUtils.subarray(workingData, 0, header));
    /* Shave off the headers */
    workingData = ArrayUtils.subarray(workingData, header, workingData.length);
    workingData = decode(workingData);
    data = ArrayUtils.addAll(workingData);
    return this;
  }

  /**
   * Read sequences until we come across image data. Place relevant data such
   * as Huffman tables in appropriate fields, as well as in the data field.
   * Place the image data in the workingData field, but not in the data
   * field. Appropriate closing of the working set, particularly the placing
   * of the image data in the data field is left to children classes.
   * 
   * @return this jpeg coder.
   * @throws IOException on read errors.
   */
  protected JPEGCoder loadWorkingSet() throws IOException {
    byte[] segment = nextSegment();
    while (segment != null && segment[1] != SOS_MARKER
           && !isRSTMarker(segment[1])) {
      switch (segment[1]) {
        case SOF0_MARKER:
          numberOfComponents = segment[9];
          width = (segment[5] >> 8) + segment[6];
          height = (segment[7] >> 8) + segment[8];
          subsampling = new byte[numberOfComponents][];
          for (int i = 0; i < numberOfComponents; i++) {
            subsampling[i] = new byte[2];
            subsampling[i][0] = (byte) (segment[11 + 3 * i] >> 4);
            subsampling[i][1] = (byte) (segment[11 + 3 * i] & 0x0F);
          }
          calculateCoeffCount();
          break;
        case DHT_MARKER:
          /* TODO: There may be a bunch of huffman tables here. */
          int id = segment[4];
          HuffmanDecoder decoder =
              new JPEGHuffmanDecoder(Arrays.copyOfRange(segment, 5,
                segment.length));
          log.info("Added new decoder for id " + id + " : " + decoder);
          decoders.put(id, decoder);
          break;
        default:
          break;
      }
      data = ArrayUtils.addAll(data, segment);
      segment = nextSegment();
    }
    /* TODO: Wait, what if we _do_ encounter an RST marker? */
    workingData = segment;
    return this;
  }
}
