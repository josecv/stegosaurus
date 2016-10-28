/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stegosaurus.steganographers.pm1;

import com.google.inject.Inject;
import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.ImagePermuter;
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
public class PM1EmbedderImpl extends PM1Algorithm implements PM1Embedder {
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
  protected PM1EmbedderImpl(PMSequence seq, ByteBufferHelper helper,
                            ImagePermuter.Factory permuterFactory) {
    super(helper);
    this.permuterFactory = permuterFactory;
    sequence = seq;
  }

  @Override
  public int fakeEmbed(EmbedRequest request, short seed) {
    return embed(request, seed, false);
  }

  @Override
  public JPEGImage embed(EmbedRequest request, short seed) {
    embed(request, seed, true);
    return request.getCover().writeNew();
  }

  @Override
  public JPEGImage embed(EmbedRequest request) {
    return embed(request, (short) 0);
  }

  @Override
  public long getMaximumMessageSize() {
    return Short.MAX_VALUE;
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
    ImagePermuter permuter = permuterFactory.build(acc, key);
    byte[] seedBytes = getClearedBuffer().putShort(seed).array();
    BitInputStream in = new BitInputStream(seedBytes);
    int changed = doEmbed(in, acc, permuter, real);
    permuter.setSeed(seed);
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
    EmbedProcedure proc = EmbedProcedure.build(in, acc, sequence, real);
    permuter.walk(proc);
    return proc.getChanges();
  }
}
