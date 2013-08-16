package com.stegosaurus.steganographers.coders.outguess;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.stegosaurus.jpeg.DecompressedScan;
import com.stegosaurus.jpeg.JPEGDecompressor;
import com.stegosaurus.stegostreams.BitOutputStream;
import com.stegosaurus.stegutils.NumUtils;

/**
 * Reveals messages hidden using the outguess algorithm.
 */
public class OutGuessUnHider {
  /**
   * The length of the message currently being decoded.
   */
  private int len;

  /**
   * The stego key.
   */
  private String key;

  /**
   * The iterator for the cover image.
   */
  private JPEGIterator iter;

  /**
   * The stego image containing the message.
   */
  private int[] cover;

  /**
   * CTOR.
   * @param key the key for the pseudo random number generator to use.
   */
  public OutGuessUnHider(String key) {
    this.key = key;
  }

  /**
   * Decode a message from the carrier image given.
   * @param image the cover image to decode a message from.
   * @return the message.
   * @throws IOException on read failure from the carrier.
   */
  public byte[] unHide(InputStream image) throws IOException {
    JPEGDecompressor decomp = new JPEGDecompressor(image);
    decomp.init();
    DecompressedScan scan = OutGuessUtils.getBestScan(decomp.processImage());
    return unHide(scan.getCoefficients().toArray());
  }

  /**
   * Decode n bits from the cover given and write them to the output stream
   * provided.
   * @param os the output stream
   * @param cover the cover image
   * @param n the number of bits to decode.
   * @param status whether we are decoding status bytes.
   * @return the last read index.
   */
  private int decodeToBitOutputStream(BitOutputStream os, int n,
      boolean status) {
    int i = 0;
    int index = -1;
    while(i < n) {
      if(status) {
        index = iter.nextStatus();
      } else {
        index = iter.next();
      }
      while(cover[index] == 0 || cover[index] == 1) {
        index = iter.skipIndex();
      }
      os.write(NumUtils.getLSB(cover[index]));
      i++;
    }
    return index;
  }

  /**
   * Decode the status bytes included in the cover image given. Reseed
   * the prng and set the len field appropriately.
   * @param cover the cover image.
   * @return the index to begin looking for hidden data.
   */
  private int decodeStatus() {
    BitOutputStream os = new BitOutputStream();
    int index = decodeToBitOutputStream(os, 48, true);
    byte[] data = os.data();
    os.close();
    len = NumUtils.intFromBytes(Arrays.copyOfRange(data, 0, 4));
    short seed = (short) NumUtils.intFromBytes(data, 4, 6);
    iter.reseed(seed, len);
    return index;
  }

  /**
   * Decode a message from the carrier image given.
   * @param cover the cover image to decode a message from.
   * @return the message.
   */
  public byte[] unHide(int[] cover) {
    iter = new RandomJPEGIterator(key.hashCode(), 6, cover.length, 0);
    this.cover = cover.clone();
    decodeStatus();
    BitOutputStream os = new BitOutputStream();
    decodeToBitOutputStream(os, len * 8, false);
    os.close();
    return os.data();
  }
}
