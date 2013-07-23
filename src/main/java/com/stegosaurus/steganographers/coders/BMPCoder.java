package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.stegosaurus.stegutils.NumUtils;

import com.stegosaurus.steganographers.coders.ImgCoder;

/**
 * A BMP Hider or Unhider.
 * 
 * @author joe
 */
public abstract class BMPCoder extends ImgCoder {

  /**
   * Dib header.
   */
  private byte[] dib;

  /**
   * The number of bytes that go into each pixel.
   */
  private int pixelSize;

  /**
   * The size of the data portion of the BMP file, in bytes.
   */
  private int dataSize;

  /**
   * The number of bytes that have been read so far.
   */
  private int bytesRead;

  /**
   * The Width of the image.
   */
  private int width;

  /**
   * A buffer into which the image's data is read into every time a new pixel
   * is read.
   */
  protected byte[] imgdata;

  /**
   * The image's header.
   */
  private byte[] header;

  /**
   * Initialize the BMPCoder to take care of the given carrier.
   * 
   * @param in
   *            the carrier.
   * @throws Exception
   */
  public BMPCoder(InputStream in) throws IOException {
    super(in);
    header = readHeader();
    bytesRead = 0;
    /* Where the actual data begins */
    int offset = NumUtils.intFromBytesLE(Arrays.copyOfRange(header, 10, 14), 4);
    /* The size of the dib header */
    int dibSize = offset - 14;
    dib = new byte[dibSize];
    instream.read(dib);
    /* How many bytes are in each pixel? */
    pixelSize = NumUtils.intFromBytesLE(Arrays.copyOfRange(dib, 14, 16), 2) / 8;
    width = NumUtils.intFromBytesLE(Arrays.copyOfRange(dib, 4, 8), 4);
    dataSize = instream.available();
    imgdata = new byte[dataSize];
  }

  /**
   * Read and return the image header.
   * 
   * @return the BMP's header.
   * @throws Exception
   */
  private byte[] readHeader() throws IOException {
    byte[] retval = new byte[14];
    instream.read(retval);
    return retval;
  }

  /**
   * Read the next pixel of the image into imgdata, and return the offset in
   * which its LSB may be found.
   * 
   * @return int the offset in which to find the just read pixel's LSB.
   */
  protected int nextPixel() throws IOException {
    /*
     * Do we have to account for an offset?
     */
    if (bytesRead % width == 0 && width % 4 != 0) {
      /*
       * Essentially we want to find the next multiple of 4 and then
       * substract the width from it so as to know how many bytes to skip
       */
      int skip = ((width / 4) + 1) * 4 - width;
      instream.read(imgdata, bytesRead, skip);
      bytesRead += skip;
    }
    instream.read(imgdata, bytesRead, pixelSize);
    bytesRead += pixelSize;
    return bytesRead - pixelSize;
  }

  /**
   * Get the dib header for this image.
   * @return the dib header
   */
  protected byte[] getDib() {
    return dib;
  }

  /**
   * Get the size of the data portion of the BMP file, in bytes.
   * @return the dataSize
   */
  protected int getDataSize() {
    return dataSize;
  }

  /**
   * Get the number of bytes read
   * @return the bytes read
   */
  protected int getBytesRead() {
    return bytesRead;
  }

  /**
   * Get the image's header.
   * @return the header
   */
  protected byte[] getHeader() {
    return header;
  }
}
