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
public class OutGuessUnHider extends OutGuess {

  /**
   * The length of the message currently being decoded.
   */
  private int len;

  /**
   * CTOR.
   * @param key the key for the pseudo random number generator to use.
   */
  public OutGuessUnHider(String key) {
    super(key);
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
    DecompressedScan scan = getBestScan(decomp.processImage());
    int[] cover = scan.getCoefficients().toArray();
    return unHide(cover);
  }

  /**
   * Decode the status bytes included in the cover image given. Reseed
   * the prng and set the len field appropriately.
   * @param cover the cover image.
   * @return the index to begin looking for hidden data.
   */
  private int decodeStatus(int[] cover) {
    int index = 0;
    BitOutputStream os = new BitOutputStream();
    int i = 0;
    while(i < 48) {
      if(cover[index] == 0 || cover[index] == 1) {
        index++;
        continue;
      }
      os.write(NumUtils.getLSB(cover[index]));
      i++;
      index += getRandom(X);
    }
    byte[] data = os.data();
    os.close();
    len = NumUtils.intFromBytes(Arrays.copyOfRange(data, 0, 4));
    reseedPRNG(Arrays.copyOfRange(data, 4, 6));
    return index;
  }

  /**
   * Decode a message from the carrier image given.
   * @param cover the cover image to decode a message from.
   * @return the message.
   */
  public byte[] unHide(int[] cover) {
    int index = decodeStatus(cover);
    BitOutputStream os = new BitOutputStream();
    for(int i = 0; i < len; i++) {
      int j = 0;
      index += getRandom(getInterval(cover, index, (len - i) * 8));
      while(j < 8) {
        if(cover[index] == 0 || cover[index] == 1) {
          index++;
          continue;
        }
        os.write(NumUtils.getLSB(cover[index]));
        index++;
        j++;
      }
    }
    os.close();
    return os.data();
  }
}
