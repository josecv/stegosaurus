package com.stegosaurus.steganographers;

import gnu.trove.procedure.TIntIntProcedure;

import java.util.Random;

import com.google.inject.Inject;
import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Embeds a message into a JPEG Image.
 * Does only one pass, using a key, seed and PM sequence.
 */
public class PM1Embedder extends PM1Algorithm {
  /**
   * The plus-minus sequence used by this embedder.
   */
  private PMSequence sequence;

  /**
   * CTOR.
   * @param random the random number generator; will be reseeded on embed.
   * @param seq the plus-minus sequence to direct this object's embedding.
   * @param helper an object that can provide us with ByteBuffers.
   */
  protected PM1Embedder(Random random, PMSequence seq, ByteBufferHelper helper) {
    super(random, helper);
    sequence = seq;
  }

  /**
   * Pretend to embed the message given into the cover image given. Do not
   * actually modify anything.
   * @param msg the message.
   * @param cover the cover image.
   * @param key the key to use when embedding the seed.
   * @param seed the seed to reseed the permutation with.
   * @return the number of changes required.
   */
  public int fakeEmbed(byte[] msg, JPEGImage cover, String key, short seed) {
    return embed(msg, cover, key, seed, false);
  }

  /**
   * Embed the message given into the cover image given.
   * @param msg the message.
   * @param cover the cover image.
   * @param key the key to use when embedding the seed.
   * @param seed the seed to reseed the permutation with.
   * @return the new image, containing the message.
   */
  public JPEGImage embed(byte[] msg, JPEGImage cover, String key, short seed) {
    embed(msg, cover, key, seed, true);
    return cover.writeNew();
  }

  /**
   * Embed (or pretend to) the message given into the cover image given.
   * @param msg the message.
   * @param cover the cover image.
   * @param key the key to use when embedding the seed.
   * @param seed the seed to reseed the permutation with.
   * @param real whether to actually do any changing of the image data.
   * @return the number of changes required.
   */
  private int embed(byte[] msg, JPEGImage cover, String key, short seed,
                    boolean real) {
    CoefficientAccessor acc = buildAccessorForImage(cover);
    Permutation p = buildPermutation(acc);
    reseedPermutation(key.hashCode(), p);
    ImagePermuter permuter = new ImagePermuter(acc, p);
    byte[] seedBytes = getClearedBuffer().putShort(seed).array();
    BitInputStream in = new BitInputStream(seedBytes);
    int changed = doEmbed(in, acc, permuter, real);
    reseedPermutation(seed, p);
    /* XXX */
    short len = (short) msg.length;
    byte[] lenBytes = getClearedBuffer().putShort(len).array();
    in.reset(lenBytes, msg);
    changed += doEmbed(in, acc, permuter, real);
    in.close();
    return changed;
  }

  /**
   * Actually execute the embedding of the message stream given on the
   * permutation and coefficient accesor given.
   * @param in the bit input stream containing the message to embed.
   * @param acc the CoefficientAccessor to embed into
   * @param permuter the image permuter in use.
   * @param real whether to actually do any embedding.
   * @return the number of changes required for the embed.
   */
  private int doEmbed(BitInputStream in, CoefficientAccessor acc,
      ImagePermuter permuter, boolean real) {
    EmbedProcedure proc = new EmbedProcedure(in, acc, sequence, real);
    permuter.walk(proc);
    return proc.changes;
  }

  /**
   * The actual callable used to embed bits into an image.
   */
  private static class EmbedProcedure implements TIntIntProcedure {
    /**
     * The message stream.
     */
    private BitInputStream in;

    /**
     * The accessor representing the image.
     */
    private CoefficientAccessor acc;

    /**
     * The sequence used to embed.
     */
    private PMSequence seq;

    /**
     * The number of changes required for the embedding.
     */
    private int changes;

    /**
     * Whether we are doing any changes to the image.
     */
    private boolean real;

    /**
     * CTOR.
     * @param in the message stream.
     * @param acc the accessor for the image.
     * @param seq the plus minus sequence used to embed.
     * @param real whether to actually do any embedding.
     */
    public EmbedProcedure(BitInputStream in, CoefficientAccessor acc,
        PMSequence seq, boolean real) {
      this.in = in;
      this.acc = acc;
      this.seq = seq;
      this.real = real;
    }

    @Override
    public boolean execute(int index, int val) {
      int m = in.read();
      if(m < 0) {
        return false;
      }
      /*
       * A Negative even coefficient is a one, a negative odd coefficient
       * is a zero, a positive even coefficient is a zero, and a positive
       * even coefficient is a one.
       * Hence the following if statement to determine whether something
       * needs to be changed in the carrier.
       */
      if((val < 0 && -(val % 2) == m) || (val > 0 && (val % 2) != m)) {
        changes++;
        if(!real) {
          return true;
        }
        val += (seq.atIndex(index) ? 1 : -1);
        if(val == 0) {
          val = (m == 0 ? -1 : 1);
        }
        acc.setCoefficient(index, val);
      }
      return true;
    }
  }

  /**
   * Builds PM1Embedders.
   */
  public static class Factory {
    /**
     * The ByteBufferHelper to inject into instances.
     */
    private ByteBufferHelper helper;

    /**
     * CTOR; to be invoked by Guava.
     * @param helper the helper to be injected into instances.
     */
    @Inject
    public Factory(ByteBufferHelper helper) {
      this.helper = helper;
    }

   /**
    * Build a new PM1Embedder.
    * @param seq the plus-minus sequence to direct this object's embedding.
    * @param helper an object that can provide us with ByteBuffers.
    */
    public PM1Embedder build(Random r, PMSequence seq) {
      return new PM1Embedder(r, seq, helper);
    }
  }
}
