#ifndef STEGOSAURUS_JPEG_LIB_EXCEPTION
#define STEGOSAURUS_JPEG_LIB_EXCEPTION
#include <string>

/**
 * An exception to be thrown whenever libjpeg hands out an error.
 * The intent is that this be used in tandem with a custom libjpeg error
 * manager.
 */
class JPEGLibException {
 public:
  /**
   * CTOR.
   * @param message the exception message provided by libjpeg.
   */
  JPEGLibException(const char *message) : msg(message) { }

  /**
   * Get the message.
   */
  const char *what();

 private:
  /**
   * The message.
   */
  std::string msg;
};

#endif
