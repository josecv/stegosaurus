package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;

import com.stegosaurus.steganographers.coders.BMPCoder;
import com.stegosaurus.steganographers.coders.UnHider;
import com.stegosaurus.stegostreams.BitOutputStream;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Remove payloads from BMP carriers.
 * 
 * @author joe
 */
public class BMPUnHider extends BMPCoder implements UnHider {

  /**
   * The payload to be returned on closing the coder.
   */
  private byte[] payload;

  /**
   * Initialize a new BMPUnHider with the given carrier.
   * 
   * @param in
   *            an input stream with the carrier file.
   * @throws Exception
   */
  public BMPUnHider(InputStream in) throws Exception {
    super(in);
    payload = new byte[0];
  }

  /**
   * Find the number of bytes given in the carrier and return them.
   * 
   * @param count
   *            the number of bytes to take out.
   * @return count bytes from the payload.
   */
  @Override
  public byte[] UnHide(int count) throws IOException {
    byte[] retval;
    BitOutputStream ostream = new BitOutputStream();
    for (int i = 0; i < count * 8; i++) {
      ostream.write(imgdata[nextPixel()] & 1);
    }
    retval = ostream.data();
    ostream.close();
    payload = ArrayUtils.addAll(payload, retval);
    return retval;
  }

  /**
   * Close the un hider and return what has been decoded of the payload.
   * 
   * @return the payload.
   */
  @Override
  public byte[] close() throws Exception {
    instream.close();
    return payload;
  }
}
