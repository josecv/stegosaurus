package com.stegosaurus.steganographers.pm1;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedRequest;

/**
 * Embeds a message into a JPEG Image, using a plus-minus sequence given.
 */
public interface PM1Embedder extends Embedder {
  /**
   * Pretend to fulfill the embed request given, by faking embedding
   * its message into its cover. Do not actually modify anything.
   * @param request the embed request.
   * @param seed the seed to reseed the permutation with.
   * @return the number of changes required.
   */
  public int fakeEmbed(EmbedRequest request, short seed);

  /**
   * Fulfill an embed request, by embedding a message into a cover.
   * @param request the embed request to fulfill.
   * @param seed the seed to reseed the permutation with.
   * @return the new image, containing the message.
   */
  public JPEGImage embed(EmbedRequest request, short seed);
}
