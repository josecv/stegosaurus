package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import java.nio.ByteOrder;
import java.util.Random;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.NumUtils;

/**
 * Embeds a message into a JPEG Image.
 * Does only one pass, using a key, seed and PM sequence.
 */
public class PM1Embedder {
  /**
   * The random number generator in use here.
   */
  private Random rand;

  private PMSequence sequence;

  /**
   * CTOR.
   * @param rand the random number generator; will be reseeded on embed.
   * @param seq the plus-minus sequence to direct this object's embedding.
   */
  public PM1Embedder(Random rand, PMSequence seq) {
    this.rand = rand;
    sequence = seq;
  }

  /**
   * Embed the message given into the cover image given.
   * @param msg the message.
   * @param cover the cover image.
   * @param key the key to use when embedding the seed.
   * @param seed the seed to reseed the permutation with.
   */
  public JPEGImage embed(byte[] msg, JPEGImage cover, String key, short seed) {
    rand.setSeed(key.hashCode());
    cover.readCoefficients();
    final CoefficientAccessor acc = new CoefficientAccessor(cover);
    final Permutation p = ImagePermuter.buildPermutation(rand, acc);
    p.init();
    ImagePermuter permuter = new ImagePermuter(acc, p);
    byte[] seedBytes = NumUtils.byteArrayFromShort(seed, ByteOrder.BIG_ENDIAN);
    BitInputStream in = new BitInputStream(seedBytes);
    doEmbed(in, acc, permuter);
    in.close();
    rand.setSeed(seed);
    p.init();
    /* XXX */
    short len = (short) msg.length;
    byte[] lenBytes = NumUtils.byteArrayFromShort(len, ByteOrder.BIG_ENDIAN);
    in = new BitInputStream(lenBytes, msg);
    doEmbed(in, acc, permuter);
    in.close();
    return cover.writeNew();
  }

  /**
   * Actually execute the embedding of the message stream given on the
   * permutation and coefficient accesor given.
   * @param in the bit input stream containing the message to embed.
   * @param acc the CoefficientAccessor to embed into
   * @param permuter the image permuter in use.
   */
  private void doEmbed(final BitInputStream in, final CoefficientAccessor acc,
      ImagePermuter permuter) {
    final PMSequence seq = sequence;
    permuter.walk(new TIntIntProcedure() {
      public boolean execute(int index, int val) {
        int m = in.read();
        /*
         * A Negative even coefficient is a one, a negative odd coefficient
         * is a zero, a positive even coefficient is a zero, and a positive
         * even coefficient is a one.
         * Hence the following if statement to determine whether something
         * needs to be changed in the carrier.
         */
        if((val < 0 && -(val % 2) == m) || (val > 0 && (val % 2) != m)) {
          val += (seq.atIndex(index) ? 1 : -1);
          if(val == 0) {
            val = (m == 0 ? -1 : 1);
          }
          acc.setCoefficient(index, val);
        }
        return in.available() != 0;
      }
    });
  }
}
