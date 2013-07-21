package com.stegosaurus.stegosaurus;

/**
 * Exception thrown when we attempt an operation on an invalid image file
 * format, such as huffman decoding on a bmp file.
 */
public class WrongImageTypeException extends RuntimeException {
  /**
   * Serial version uid.
   */
  public static final long serialVersionUID = 8711411111010373109L;

   /**
   * CTOR.
   */
  public WrongImageTypeException() {
    super();
  }

  /**
   * CTOR with message.
   * @param msg the message
   */
  public WrongImageTypeException(String msg) {
    super(msg);
  }

  /**
   * CTOR with cause.
   * @param cause
   */
  public WrongImageTypeException(Throwable cause) {
    super(cause);
  }

  /**
   * CTOR with message and cause.
   * @param msg message
   * @param cause cause
   */
  public WrongImageTypeException(String msg, Throwable cause) {
    super(msg, cause);
  }

  /**
   * Full CTOR.
   * @param msg message
   * @param cause cause
   * @param suppress whether to enable supression for this exception
   * @param stackTrace whether to enable a writable stack trace
   */
  public WrongImageTypeException(String msg, Throwable cause,
                                 boolean supress, boolean stackTrace) {
    super(msg, cause, supress, stackTrace);
  }
}
