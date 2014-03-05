package com.stegosaurus.steganographers;

import com.stegosaurus.cpp.JPEGImage;

/**
 * A very simple data structure that encapsulates the parameters of embedding
 * a message into a JPEG image.
 */
public class EmbedRequest {
  /**
   * The cover image that will receive the embedding.
   */
  private JPEGImage cover;

  /**
   * The message to embed into the image.
   */
  private byte[] message;

  /**
   * The key to use for the embedding.
   */
  private String key;

  /**
   * CTOR.
   * @param cover the cover image that will receive the embedding.
   * @param message the message to embed into the image.
   * @param key the key to use.
   */
  public EmbedRequest(JPEGImage cover, byte[] message, String key) {
    this.cover = cover;
    this.message = message;
    this.key = key;
  }

  /**
   * Copy contructor; will copy the image in the other request, using the
   * writeNew() method.
   * @param other the image to copy.
   */
  public EmbedRequest(EmbedRequest other) {
    this.message = other.message;
    this.key = other.key;
    this.cover = other.cover.writeNew();
  }

  /**
   * Get the cover image for this object.
   * @return the cover
   */
  public JPEGImage getCover() {
    return cover;
  }

  /**
   * Get the message for this object.
   * @return the message
   */
  public byte[] getMessage() {
    return message;
  }

  /**
   * Get the key for this object.
   * @return the key
   */
  public String getKey() {
    return key;
  }
}
