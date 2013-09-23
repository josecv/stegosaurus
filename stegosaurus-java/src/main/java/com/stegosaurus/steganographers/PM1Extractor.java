package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import java.util.Random;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.stegostreams.BitOutputStream;

/**
 * Extracts messages from carrier images.
 */
public class PM1Extractor {
  /**
   * The random object.
   */
  private Random random;

  /**
   * CTOR.
   * @param random the random object to use; will be reseeded on extract.
   */
  public PM1Extractor(Random random) {
    this.random = random;
  }

  /**
   * Extract a message from the carrier image given.
   * @param carrier the carrier.
   * @param key the key.
   * @return the message as a byte array.
   */
  public byte[] extract(JPEGImage carrier, String key) {
    random.setSeed(key.hashCode());
    carrier.readCoefficients();
    CoefficientAccessor acc = new CoefficientAccessor(carrier);
    Permutation p = ImagePermuter.buildPermutation(random, acc);
    p.init();
    ImagePermuter permuter = new ImagePermuter(acc, p);
    BitOutputStream os = new BitOutputStream();
    doExtract(permuter, os, 16);
    byte[] seedBytes = os.data();
    os.close();
    short seed = (short) ((seedBytes[0] << 8) | (seedBytes[1] & 0xFF));
    random.setSeed(seed);
    p.init();
    os = new BitOutputStream();
    doExtract(permuter, os, 16);
    byte[] lenBytes = os.data();
    int len = ((lenBytes[0] << 8) | lenBytes[1] & 0xFF);
    os.close();
    os = new BitOutputStream();
    doExtract(permuter, os, len * 8);
    byte[] data = os.data();
    os.close();
    return data;
  }

  /**
   * Actually do the heavy lifting of extracting a message from an image
   * permutation given.
   * @param permuter the ImagePermuter object.
   * @param os the bit output stream to place the message bits into.
   * @param len the number of bits to extract.
   */
  private void doExtract(ImagePermuter permuter, final BitOutputStream os,
      final int len) {
    permuter.walk(new TIntIntProcedure() {
      private int count;
      public boolean execute(int index, int value) {
        if(value < 0) {
          os.write((~value) & 1);
        } else {
          os.write(value % 2);
        }
        count++;
        return count < len;
      }
    });
  }
}
