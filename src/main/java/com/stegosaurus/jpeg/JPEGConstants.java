package com.stegosaurus.jpeg;

/**
 * Defines constants for JPEG decoding/encoding. Notably, this includes the
 * markers that denote specific sections of a file
 */
public final class JPEGConstants {
  private JPEGConstants() { }

  /**
   * Code for the start of image marker.
   */
  public static final byte SOI_MARKER = (byte) 0xD8;

  /**
   * Code for the start of frame marker. Indicates this as a baseline DCT JPG.
   * Gives width, height, number of components, and component subsampling.
   */
  public static final byte SOF0_MARKER = (byte) 0xC0;

  /**
   * Start of frame marker for extended baseline DCT mode.
   */
  public static final byte SOF1_MARKER = (byte) 0xC1;

  /**
   * Code for the start of frame marker. Indicates this as a progressive DCT
   * JPG. Same as with SOF0.
   */
  public static final byte SOF2_MARKER = (byte) 0xC2;

  /**
   * Code for the Huffman tables marker.
   */
  public static final byte DHT_MARKER = (byte) 0xC4;

  /**
   * Code for the quantization tables marker.
   */
  public static final byte DQT_MARKER = (byte) 0xDB;

  /**
   * Code for the Define Restart Interval marker. Specifies the intervals
   * between RSTn markers.
   */
  public static final byte DRI_MARKER = (byte) 0xDD;

  /**
   * Code for the start of scan marker. Starts the image scan, from top to
   * bottom.
   */
  public static final byte SOS_MARKER = (byte) 0xDA;

  /**
   * Code for a text comment.
   */
  public static final byte COM_MARKER = (byte) 0xFE;

  /**
   * Code for the end of image marker.
   */
  public static final byte EOI_MARKER = (byte) 0xD9;

  /**
   * Return whether the given byte is an RSTn marker. It would be indicated by
   * being 0xDn, where n=0..7.
   * 
   * @param b the byte to test.
   * @return true if it is an RSTn marker.
   */
  public static boolean isRSTMarker(byte b) {
    byte lsb = (byte) (b & 0x0F);
    byte msb = (byte) (b & 0xF0);
    return 0 <= lsb && lsb <= 7 && msb == 0xD0;
  }
}
