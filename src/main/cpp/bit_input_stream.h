/*
 * Produces, bit by bit, the byte array given. Operates in Big Endian, which
 * is to say the most significant bit of the 0th byte is returned, followed
 * by the second to most significant bit of the 0th byte, and so on.
 * TODO Some bounds checking!
 * TODO Make this more idiomatic. Right now it's a straight up translation.
 */
class BitInputStream {
 public:
  /**
   * Initialize the input stream with the input given.
   * @param input the array of bytes whose bits we will return one by one.
   * @param length the length of the array.
   */
  BitInputStream(const char* input, int length);
  /**
   * Get the next bit (0 or 1) from the byte array.
   * @return the next bit.
   */
  char read();
  /**
   * Get the number of available bits that can be read without blocking, or 0
   * if there is nothing left to read.
   * @return the number of bits that can be read.
   */
  int available();
 private:
  /**
   * The current index.
   */
  int index;
  /**
   * The number of bytes in the buffer.
   */
  int length;
  /**
   * The data buffer.
   */
  const char *data;
};
