package com.stegosaurus.steganographers.genetic;

/**
 * Serves to wrap around the Individual interface, providing some common 
 * operations to implementing classes.
 * These include keeping track of the fitness to know when it should be
 * recalculated, and ensuring that nobody tries to calculate it without
 * first having run the simulation.
 */
public abstract class AbstractIndividual<T extends AbstractIndividual<T>>
  implements Individual<T> {

  /**
   * The fitness. Whenever set to -1.0, should be considered as unset.
   */
  private double fitness = -1.0;

  /**
   * The chromosome.
   */
  protected Chromosome chromosome;

  /**
   * Whether we're unable to get the fitness until simulate has been called.
   */
  private boolean needsSimulation = true;

  /**
   * CTOR.
   * @param chromosome this individual's chromosome.
   */
  protected AbstractIndividual(Chromosome chromosome) {
    this.chromosome = chromosome;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Chromosome getChromosome() {
    return chromosome;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final double calculateFitness() {
    if(needsSimulation) {
      throw new IllegalStateException("Simulation needs to be run.");
    }
    if(fitness < 0) {
      fitness = calculateFitnessImpl();
    }
    return fitness;
  }

  /**
   * Actually calculate the fitness; called when we know that we need it
   * (ie right after running the simulation).
   * @return the fitness.
   */
  protected abstract double calculateFitnessImpl();

  /**
   * {@inheritDoc}
   */
  @Override
  public final void simulate() {
    simulateImpl();
    needsSimulation = false;
    fitness = -1.0;
  }

  /**
   * Actually run the simulation.
   */
  protected abstract void simulateImpl();

  /**
   * {@inheritDoc}
   */
  @Override
  public final void crossover(Individual<T> other) {
    needsSimulation = true;
    /* This is terrible style, but is guaranteed to work, unless the caller
     * ignores the generic parameter entirely, or typecasts in a crazy manner
     */
    ((AbstractIndividual<T>) other).needsSimulation = true;
    crossoverImpl(other);
  }

  /**
   * Actually cross this individual over with the one given.
   * @param other the individual to cross this one with.
   */
  protected abstract void crossoverImpl(Individual<T> other);
}
