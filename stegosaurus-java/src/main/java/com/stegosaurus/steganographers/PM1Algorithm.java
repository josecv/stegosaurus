package com.stegosaurus.steganographers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import com.stegosaurus.cpp.CoefficientAccessor;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.crypt.Permutation;

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
   * A byte buffer to be used for manipulating byte arrays and other
   * such structures. Allocated to 2 bytes.
   * Note that its byte order is explicitly set to BIG_ENDIAN, because the
   * default is platform-specific.
   */
  private ByteBuffer byteBuffer = ByteBuffer.allocate(2)
    .order(ByteOrder.BIG_ENDIAN);

  /**
   * CTOR.
   * @param random the random object this object will use. Will be reseeded.
   */
  public PM1Algorithm(Random random) {
    this.random = random;
  }

  /**
   * Get a 2 byte long, empty, byte buffer.
   * Note that it's always the same one, so don't call this method twice
   * if you want to preserve the buffer.
   * @return the byte buffer.
   */
  protected ByteBuffer getClearedBuffer() {
    byteBuffer.clear();
    return byteBuffer;
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
