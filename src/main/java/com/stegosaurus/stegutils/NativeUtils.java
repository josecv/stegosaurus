package com.stegosaurus.stegutils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

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
    /* TODO THIS IS VERY TEMPORARY!! */
    System.load("/home/joe/code/stegosaurus/libstegosaurus.so");
  }
  /**
   * Private CTOR.
   */
  private NativeUtils() { }

  /**
   * A custom JoctetArray, extended to the SWIG-generated one, but keeps
   * length information handy.
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
    byte[] b = IOUtils.toByteArray(in);
    StegJoctetArray retval = new StegJoctetArray(b.length);
    for(int i = 0; i < b.length; i++) {
      retval.setitem(i, b[i]);
    }
    return retval;
  }
}
