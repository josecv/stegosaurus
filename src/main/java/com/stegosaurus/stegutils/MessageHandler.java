package com.stegosaurus.stegutils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Worries about the logic of dealing with the user's messages.
 */
public class MessageHandler {
  /**
   * The message this object manages.
   */
  private String msg;

  /**
   * Start a new message handler to worry about msg.
   * @param msg the message to concern this handler with.
   */
  public MessageHandler(String msg) {
    this.msg = msg;
  }
    
  /**
   * Get the message handled by this object as a byte array, preceded by big
   * endian length information (4 bytes).
   * @return the array of bytes representing the message.
   */
  public byte[] asByteArray() {
    int[] l = { msg.length() };
    return ArrayUtils.addAll(NumUtils.byteArrayFromIntArray(l),
                             msg.getBytes());
  }
}
