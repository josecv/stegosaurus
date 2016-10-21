package com.stegosaurus.steganographers.pm1;

import gnu.trove.procedure.TIntIntProcedure;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.Extractor;
import com.stegosaurus.steganographers.ImagePermuter;
import com.stegosaurus.stegostreams.BitOutputStream;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Extracts messages from carrier images.
 */
public class PM1Extractor extends PM1Algorithm implements Extractor {

  /**
   * A BitOutputStream to use for extracting steganographic bits in an image.
   */
  private final BitOutputStream os = new BitOutputStream();

  /**
   * A factory to construct image permuters.
   */
  private ImagePermuter.Factory permFactory;

  /**
   * CTOR.
   * @param helper an object that can provide us with ByteBuffers.
   * @param permFactory a factory to construct image permuters.
   * @param permutationProvider an object that can give us permutations.
   */
  protected PM1Extractor(ByteBufferHelper helper,
                         ImagePermuter.Factory permFactory) {
    super(helper);
    this.permFactory = permFactory;
  }

  /**
   * Extract a message from the carrier image given.
   * @param carrier the carrier.
   * @param key the key.
   * @return the message as a byte array.
   */
  public byte[] extract(JPEGImage carrier, String key) {
    CoefficientAccessor acc = getAccessorForImage(carrier);
    ImagePermuter permuter = permFactory.build(acc, key);
    doExtract(permuter, Short.SIZE);
    short seed = getClearedBuffer().put(os.data()).getShort(0);
    permuter.setSeed(seed);
    doExtract(permuter, Short.SIZE);
    int len = getClearedBuffer().put(os.data()).getShort(0);
    doExtract(permuter, len * Byte.SIZE);
    return os.data();
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
        os.write((value < 0 ? ~value : value) & 1);
        count++;
        return count < len;
      }
    });
  }
}
