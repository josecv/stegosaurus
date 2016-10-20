package com.stegosaurus.stegutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;
import com.stegosaurus.cpp.JoctetArray;

/**
 * Smooths out some of the rougher aspects of the glue between the native
 * libstegosaurus and the Java portions of Stegosaurus; also charged with
 * actually loading the library.
 * For the most part, this involves creating and populating arrays and
 * other such things.
 */
public final class NativeUtils {
  static {
    System.loadLibrary("stegosaurus");
  }
  /**
   * Private CTOR.
   */
  private NativeUtils() { }

  /**
   * A custom JoctetArray, extended to the SWIG-generated one, but keeps
   * length information handy; note that it is not garbage collected.
   * Note that the JPEGImage class frees any joctet arrays it's given, so
   * you should only manually delete this when using it for another purpose.
   */
  public static class StegJoctetArray extends JoctetArray {
    /**
     * The length of the array.
     */
    private int length;
    /**
     * CTOR.
     * @param nelements the size of the array.
     */
    public StegJoctetArray(int nelements) {
      super(nelements);
      swigCMemOwn = false;
      length = nelements;
    }
    /**
     * Get the length.
     * @return the length
     */
    public int length() {
      return length;
    }
  }

  /**
   * Read an input stream into a JoctetArray and return it.
   * @param in the input stream to read.
   * @return the StegJoctetArray corresponding to it.
   * @throws IOException on read error.
   */
  public static StegJoctetArray readInputStream(InputStream in)
    throws IOException {
    /* TODO This is slower than just reading the stream in by hand.
     * Unfortunately the correct way of reading an entire InputStream
     * is highly elusive, and growing these JoctetArrays is _tough_.
     */
    byte[] b = ByteStreams.toByteArray(in);
    StegJoctetArray retval = new StegJoctetArray(b.length);
    for(int i = 0; i < b.length; i++) {
      retval.setitem(i, b[i]);
    }
    return retval;
  }

  /**
   * Write the octet array given into the output stream given.
   * There's no bounds checking: your length better really be leq the length of
   * the array.
   *
   * @param out the output stream
   * @param array the octet array
   * @param len how many bytes to write
   */
  public static void writeOctetArray(OutputStream out, JoctetArray array,
                                     int len) throws IOException {
    for (int i = 0; i < len; i++) {
      out.write(array.getitem(i));
    }
  }
}
