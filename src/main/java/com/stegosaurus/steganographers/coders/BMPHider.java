package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.stegostreams.BitInputStream;

/**
 * Hides payload data in a BMP carrier.
 * 
 * @author joe
 */
public class BMPHider extends BMPCoder implements Hider {

  /**
   * Construct the BMP Hider to deal with the input stream given.
   * 
   * @param in
   * @throws Exception
   */
  public BMPHider(InputStream in) throws IOException {
    super(in);
  }

  /**
   * Close this Hider and return the entire carrier as a byte stream.
   * 
   * @return the carrier file (with the hidden payload, evidently).
   */
  @Override
  public byte[] close() throws IOException {
    int bytesRead = getBytesRead();
    instream.read(imgdata, bytesRead, getDataSize() - bytesRead);
    instream.close();
    return ArrayUtils.addAll(ArrayUtils.addAll(getHeader(), getDib()),
      imgdata);
  }

  /**
   * Hide the number of bits given from the data stream in the carrier.
   * 
   * @param datastream
   *            the input stream to take the payload from.
   * @param count
   *            the number of bits to take from the stream.
   */
  @Override
  public void hide(BitInputStream datastream, int count) throws IOException {
    for (int i = 0; i < count; i++) {
      int off = nextPixel();
      /* Actually place the bit in the lsb of the pixel */
      imgdata[off] = (byte) hideInLSB(datastream.read(), imgdata[off]);
    }
  }
}
