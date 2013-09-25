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
public class PM1Extractor extends PM1Algorithm {

  /**
   * A BitOutputStream to use for extracting steganographic bits in an image.
   */
  final BitOutputStream os = new BitOutputStream();

  /**
   * CTOR.
   * @param random the random object to use; will be reseeded on extract.
   */
  public PM1Extractor(Random random) {
    super(random);
  }

  /**
   * Extract a message from the carrier image given.
   * @param carrier the carrier.
   * @param key the key.
   * @return the message as a byte array.
   */
  public byte[] extract(JPEGImage carrier, String key) {
    CoefficientAccessor acc = buildAccessorForImage(carrier);
    Permutation p = buildPermutation(acc);
    reseedPermutation(key.hashCode(), p);
    ImagePermuter permuter = new ImagePermuter(acc, p);
    doExtract(permuter, 16);
    short seed = getClearedBuffer().put(os.data()).getShort(0);
    reseedPermutation(seed, p);
    doExtract(permuter, 16);
    int len = getClearedBuffer().put(os.data()).getShort(0);
    doExtract(permuter, len * 8);
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
  private void doExtract(ImagePermuter permuter, final int len) {
    os.reset();
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