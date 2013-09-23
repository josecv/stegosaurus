package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
   * A byte buffer to be used for manipulating byte arrays and other
   * such structures. Allocated to 2 bytes.
   * Note that we must explicitly set the byte order, because the default
   * is platform-specific.
   */
  private ByteBuffer byteBuffer = ByteBuffer.allocate(2)
    .order(ByteOrder.BIG_ENDIAN);

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
    short seed = byteBuffer.put(os.data()).getShort(0);
    os.close();
    random.setSeed(seed);
    p.init();
    os = new BitOutputStream();
    doExtract(permuter, os, 16);
    byteBuffer.clear();
    int len = byteBuffer.put(os.data()).getShort(0);
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
