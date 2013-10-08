package com.stegosaurus.steganographers.genetic;

/**
 * Builds instances of the Individual class.
 * @param <T> the type of the built instances.
 */
public interface IndividualFactory<T extends Individual<T>> {
  /**
   * Construct a new Individual, with the chromosome given.
   */
  T build(Chromosome c);
}
