package com.stegosaurus.steganographers.genetic;

/**
 * A Dummy individual, useful for testing.
 * The fitness returns 1 / n, where n is the number of times it's been
 * calculated.
 */
public class DummyIndividual extends AbstractIndividual<DummyIndividual> {

  /**
   * The number of times the fitness has been calculated.
   */
  private double calcCount = 0.0;

  /**
   * CTOR.
   * @param c the chromosome.
   */
  public DummyIndividual(Chromosome c) {
    super(c);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void crossoverImpl(Individual<DummyIndividual> other) {
    Chromosome.crossover(chromosome, other.getChromosome());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void simulateImpl() {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected double calculateFitnessImpl() {
    calcCount++;
    return 1 / calcCount;
  }
}
