/**
 * Stegosaurus: JPEG Steganography
 * Copyright (C) 2016 Jose Cortes-Varela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stegosaurus.genetic;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Random;

import com.google.common.primitives.Bytes;
import com.stegosaurus.steganographers.pm1.PMSequence;

/**
 * A chromosme for some genetic algorithm. Consists of a sequence of boolean
 * genes that may be flipped on or off.
 */
public class Chromosome implements PMSequence {

  /**
   * The bit set used to represent the chromosome.
   * Every bit in the set is a gene in the chromosome.
   */
  private BitSet set;

  /**
   * The random number generator in use.
   */
  private Random random;

  /**
   * The number of genes in the chromosome.
   */
  private int size;

  /**
   * Construct a chromosome.
   * @param size the number of genes in the chromosome.
   * @param random the random number generator this chromosome should use.
   */
  public Chromosome(int size, Random random) {
    set = new BitSet(size);
    this.size = size;
    this.random = random;
  }

  /**
   * Randomize the genes in this chromosome.
   * @return this object.
   */
  public Chromosome randomize() {
    for(int i = 0; i < size; i++) {
      set.set(i, random.nextBoolean());
    }
    return this;
  }

  /**
   * Mutate this chromosome.
   * Equivalent to randomly flipping some of its genes.
   * @param rate the probability that a specific gene will be changed
   * @return this object.
   */
  public Chromosome mutate(double rate) {
    for(int i = 0; i < size; i++) {
      double rand = random.nextDouble();
      if(rand < rate) {
        set.flip(i);
      }
    }
    return this;
  }

  /**
   * Cross two chromosomes over, choosing a random index and using it as a
   * centre point for a gene interchange.
   * This is equivalent to crossover(first, second, randomIndex) where
   * randomIndex is a random integer obtained from the first chromosome's
   * random number generator.
   * @param first the first chromosome.
   * @param second the second chromosome.
   */
  public static void crossover(Chromosome first, Chromosome second) {
    crossover(first, second, first.random.nextInt(first.size));
  }

  /**
   * Cross two chromosomes over, using the index given as a centre point
   * for a gene interchange.
   * @param first the first chromosome.
   * @param second the second chromosome.
   * @param index the index.
   * @throws IllegalArgumentException if the chromosomes are of unequal length
   * @throws IndexOutOfBoundsException if the index given is out of range
   */
  public static void crossover(Chromosome first, Chromosome second,
    int index) {
    if(first.size != second.size) {
      throw new IllegalArgumentException("Chromosomes of unequal length");
    }
    if(index >= first.size) {
      throw new
        IndexOutOfBoundsException(index + " out of range for chromosomes");
    }
    BitSet[] sets = { first.set, second.set };
    for(int i = 0; i < 2; i++) {
      sets[i] = sets[i].get(0, first.size);
      sets[i].clear(0, index);
    }
    first.set.clear(index, first.size);
    first.set.or(sets[1]);
    second.set.clear(index, second.size);
    second.set.or(sets[0]);
  }

  /**
   * Get the gene at the index given.
   * @param index the index
   * @return the gene (true or false).
   */
  public boolean atIndex(int index) {
    if(index >= size) {
      throw new IndexOutOfBoundsException("Index " + index + " is too large");
    }
    return set.get(index);
  }

  /**
   * Get a representation of this Chromosome as a double precision floating
   * point number.
   * Note that in the event of the chromosome not being large enough, this
   * will fill up the missing genes with 0.
   * Similarly, in the event of the chromosome being too large, the extra
   * genes will be ignored.
   * WARNING: May return Infinity or NaN. Caveat salutator.
   * @return a representation of the chromosome as a double.
   */
  public double asDouble() {
    return ByteBuffer.wrap(asByteArray(Double.SIZE / Byte.SIZE)).getDouble();
  }

  /**
   * Get a representation of this Chromosome as a short.
   * Note that in the event of the chromosome not being large enough,
   * the missing genes will be filled with 0.
   * Similarly, in the event of the chromosome being too large, the extra
   * genes will be ignored.
   * @return a representation of this chromosome as a short.
   */
  public short asShort() {
    return ByteBuffer.wrap(asByteArray(Short.SIZE / Byte.SIZE)).getShort();
  }

  /**
   * Get a representation of this chromosome as a byte array of at least
   * the size given.
   * Any missing genes will be treated as 0s.
   * @param minSize the lower bound on the size of the desired array.
   */
  private byte[] asByteArray(int minSize) {
    byte[] retval = set.toByteArray();
    if(retval.length < minSize) {
      retval = Bytes.concat(retval, new byte[minSize - retval.length]);
    }
    return retval;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    }
    if(obj == null || !(obj instanceof Chromosome)) {
      return false;
    }
    return ((Chromosome) obj).set.equals(set);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return set.hashCode();
  }
}
