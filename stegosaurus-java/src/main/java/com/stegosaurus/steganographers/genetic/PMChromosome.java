package com.stegosaurus.steganographers.genetic;

import java.util.BitSet;
import java.util.Random;

import com.stegosaurus.steganographers.PMSequence;

/**
 * A chromosme for the genetic PM1 algorithm.
 */
public class PMChromosome implements PMSequence {

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
  public PMChromosome(int size, Random random) {
    set = new BitSet(size);
    this.size = size;
    this.random = random;
  }

  /**
   * Randomize the genes in this chromosome.
   * @return this object.
   */
  public PMChromosome randomize() {
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
  public PMChromosome mutate(double rate) {
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
  public static void crossover(PMChromosome first, PMChromosome second) {
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
  public static void crossover(PMChromosome first, PMChromosome second,
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
   * {@inheritDoc}
   */
  public boolean atIndex(int index) {
    return set.get(index);
  }
}
