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

/**
 * Serves to wrap around the Individual interface, providing some common 
 * operations to implementing classes.
 * These include keeping track of the fitness to know when it should be
 * recalculated, and ensuring that nobody tries to calculate it without
 * first having run the simulation.
 * In addition, this class ensures that no simulation is run when none is
 * needed (i.e. no crossover or mutation has ocurred).
 * Note: this class implements natural ordering inconsistent with equals()
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
  public final synchronized double calculateFitness() {
    if(needsSimulation) {
      throw new IllegalStateException("Simulation needs to be run.");
    }
    if(fitness < 0) {
      fitness = calculateFitnessImpl();
    }
    if(fitness < 0 || fitness > 1) {
      throw new IllegalStateException("Invalid fitness returned by type " +
        getClass());
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
  public synchronized final Individual<T> simulate() {
    if(needsSimulation) {
      simulateImpl();
      needsSimulation = false;
      fitness = -1.0;
    }
    return this;
  }

  /**
   * Actually run the simulation.
   */
  protected abstract void simulateImpl();

  /**
   * Force a simulation to take place the next time simulate() is called,
   * regardless of whether this class considers it necessary.
   * Note that this method does not run a simulation! It merely ensures
   * that the next call to simulate() will.
   */
  protected void forceNextSimulation() {
    needsSimulation = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void crossover(Individual<T> other) {
    needsSimulation = true;
    if(other instanceof AbstractIndividual) {
      ((AbstractIndividual<T>) other).needsSimulation = true;
    }
    crossoverImpl(other);
  }

  /**
   * Actually cross this individual over with the one given.
   * @param other the individual to cross this one with.
   */
  protected void crossoverImpl(Individual<T> other) {
    Chromosome.crossover(chromosome, other.getChromosome());
  }

  /**
   * Compares this individual to another, returning a positive number, a 0,
   * or a negative number if it is greater than, equal, or less than the
   * other, respectively.
   * NOTE: THIS ORDERING IS INCONSISTENT WITH equals().
   * @param other the other individual
   */
  @Override
  public int compareTo(Individual<T> other) {
    return Double.compare(calculateFitness(), other.calculateFitness());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Individual<T> mutate(double rate) {
    chromosome.mutate(rate);
    needsSimulation = true;
    return this;
  }
}
