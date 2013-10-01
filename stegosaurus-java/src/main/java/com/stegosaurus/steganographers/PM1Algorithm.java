package com.stegosaurus.steganographers;

import java.nio.ByteBuffer;
import java.util.Random;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.Permutation;
import com.stegosaurus.stegutils.ByteBufferHelper;

/**
 * Any algorithm that makes use of a permutation and a plus minus one sequence
 * may be described as a PM1 algorithm.
 * This class provides some common functionality for such algorithms.
 */
public abstract class PM1Algorithm {
  /**
   * The Random object in use.
   */
  private Random random;

  /**
   * The buffer helper we use to get byte buffers.
   */
  private ByteBufferHelper buffers;

  /**
   * CTOR.
   * @param random the random object this object will use. Will be reseeded.
   * @param buffers an object that can provide us with ByteBuffers.
   */
  protected PM1Algorithm(Random random, ByteBufferHelper buffers) {
    this.random = random;
    this.buffers = buffers;
  }

  /**
   * Get a byte buffer from the ByteBufferHelper this class has on hand.
   * @param size the size of the required buffer.
   * @return the byte buffer.
   */
  protected ByteBuffer getClearedBuffer(int size) {
    return buffers.getClearedBuffer(size);
  }

  /**
   * Get a 2-byte long byte buffer from the ByteBufferHelper this class
   * has on hand.
   * @return the byte buffer.
   */
  protected ByteBuffer getClearedBuffer() {
    return getClearedBuffer(2);
  }

  /**
   * Construct a new Permutation using this instance's Random object,
   * corresponding to the CoefficientAccessor given.
   * @param acc the coefficient accessor.
   * @return the permutation.
   */
  protected Permutation buildPermutation(CoefficientAccessor acc) {
    return ImagePermuter.buildPermutation(random, acc);
  }

  /**
   * Reseed the internal Random object, and re-initialize the permutation
   * given.
   * @param seed the seed.
   * @param permutation the permutation.
   */
  protected void reseedPermutation(long seed, Permutation permutation) {
    random.setSeed(seed);
    permutation.init();
  }

  /**
   * Build a coefficient accessor for the image given, after reading said
   * image's coefficients in.
   * @param image the image.
   * @return the built accessor.
   */
  protected CoefficientAccessor buildAccessorForImage(JPEGImage image) {
    image.readCoefficients();
    return new CoefficientAccessor(image);
  }
}
