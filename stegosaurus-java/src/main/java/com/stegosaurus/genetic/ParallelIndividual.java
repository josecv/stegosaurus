package com.stegosaurus.genetic;

/**
 * Wraps around Individual instances to provided concurrent behaviour.
 */
public class ParallelIndividual<T extends Individual<T>>
  implements Individual<T> {

  /**
   * The individual this instance wraps around.
   */
  private final Individual<T> decorated;

  /**
   * CTOR.
   * @param decorated the individual to wrap around.
   */
  public ParallelIndividual(Individual<T> decorated) {
    this.decorated = decorated;
  }

  /**
   * {@inheritDoc}
   */
  public Individual<T> simulate() {
    return decorated.simulate();
  }

  /**
   * {@inheritDoc}
   */
  public double calculateFitness() {
    return decorated.calculateFitness();
  }

  /**
   * {@inheritDoc}
   */
  public void crossover(Individual<T> other) {
    decorated.crossover(other);
  }

  /**
   * {@inheritDoc}
   */
  public Chromosome getChromosome() {
    return decorated.getChromosome();
  }

  /**
   * {@inheritDoc}
   */
  public Individual<T> mutate(double rate) {
    return decorated.mutate(rate);
  }

  /**
   * {@inheritDoc}
   */
  public int compareTo(Individual<T> other) {
    return decorated.compareTo(other);
  }
}
