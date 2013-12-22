package com.stegosaurus.steganographers;


import com.google.inject.Inject;
import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Embeds a message into a JPEG Image, using a plus-minus sequence given.
 * <p>The overall process of embedding is:<ul>
 *    <li>create a permutation using the key given.</li>
 *    <li>use the permutation to embed a given 16-bit seed into the image</li>
 *    <li>create a new permutation from that seed</li>
 *    <li>use that new permutation to embed the message into the image</li>
 * </ul></p>
 * <p>The plus-minus sequence is tied to an instance, but other than that
 * multiple embeddings can be performed with different EmbedRequests and
 * seeds</p>
 */
public class PM1Embedder extends PM1Algorithm {
  /**
   * The plus-minus sequence used by this embedder.
   */
  private PMSequence sequence;

  /**
   * A factory to construct image permuters.
   */
  private ImagePermuter.Factory permuterFactory;

  /**
   * CTOR.
   * @param seq the plus-minus sequence to direct this object's embedding.
   * @param helper an object that can provide us with ByteBuffers.
   * @param permutationProvider an object to provide us with Permutations.
   */
  protected PM1Embedder(PMSequence seq,
                        ByteBufferHelper helper,
                        ImagePermuter.Factory permuterFactory) {
    super(helper);
    this.permuterFactory = permuterFactory;
    sequence = seq;
  }

  /**
   * Pretend to fulfill the embed request given, by faking embedding
   * its message into its cover. Do not actually modify anything.
   * @param request the embed request.
   * @param seed the seed to reseed the permutation with.
   * @return the number of changes required.
   */
  public int fakeEmbed(EmbedRequest request, short seed) {
    return embed(request, seed, false);
  }

  /**
   * Fulfill an embed request, by embedding a message into a cover.
   * @param request the embed request to fulfill.
   * @param seed the seed to reseed the permutation with.
   * @return the new image, containing the message.
   */
  public JPEGImage embed(EmbedRequest request, short seed) {
    embed(request, seed, true);
    return request.getCover().writeNew();
  }

  /**
   * Embed (or pretend to) the request's message into its cover image.
   * @param request the embed request.
   * @param seed the seed to reseed the permutation with.
   * @param real whether to actually do any changing of the image data.
   * @return the number of changes required.
   */
  private int embed(EmbedRequest request, short seed,
                    boolean real) {
    JPEGImage cover = request.getCover();
    String key = request.getKey();
    byte[] msg = request.getMessage();
    CoefficientAccessor acc = getAccessorForImage(cover);
    ImagePermuter permuter = permuterFactory.build(acc, key.hashCode());
    byte[] seedBytes = getClearedBuffer().putShort(seed).array();
    BitInputStream in = new BitInputStream(seedBytes);
    int changed = doEmbed(in, acc, permuter, real);
    permuter.setSeed(seed);
    /* TODO THIS IS AWFUL! */
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
    return proc.getChanges();
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
     * The ImagePermuter factory that will be injected into created instances.
     */
    private ImagePermuter.Factory permuterFactory;

    /**
     * CTOR; to be invoked by Guava.
     * @param helper the helper to be injected into instances.
     * @param permuterFactory the image permuter factory to inject to objects.
     * @param provider the PermutationProvider to hand out to created objects.
     */
    @Inject
    public Factory(ByteBufferHelper helper,
                   ImagePermuter.Factory permuterFactory) {
      this.permuterFactory = permuterFactory;
      this.helper = helper;
    }

   /**
    * Build a new PM1Embedder.
    * @param seq the plus-minus sequence to direct this object's embedding.
    * @param helper an object that can provide us with ByteBuffers.
    */
    public PM1Embedder build(PMSequence seq) {
      return new PM1Embedder(seq, helper, permuterFactory);
    }
  }
}
