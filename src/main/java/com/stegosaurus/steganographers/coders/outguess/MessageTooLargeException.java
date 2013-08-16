package com.stegosaurus.steganographers.coders.outguess;

/**
 * Thrown in the event that the message given to outguess was too large.
 */
public class MessageTooLargeException extends RuntimeException {
  /**
   * A random serial version uid.
   */
  static final long serialVersionUID = 248815051276771506L;

  /**
   * The maximum length that would have been allowed.
   */
  private int maxLength;

  /**
   * Default CTOR.
   */
  public MessageTooLargeException() {
    super();
  }

  /**
   * Construct with a max length and a default message.
   * @param max the maximum length that would have been allowed.
   */
  public MessageTooLargeException(int max) {
    super("Message length exceeds maximum of " + max);
    this.maxLength = max;
  }

  /**
   * Construct with a max length and an exception message.
   * @param max the maximum length.
   * @param msg the message for this exception.
   */
  public MessageTooLargeException(int max, String msg) {
    super(msg);
    this.maxLength = max;
  }

  /**
   * Get the maximum length allowed for a message.
   * @return the maxLength
   */
  public int getMaxLength() {
    return maxLength;
  }
}
