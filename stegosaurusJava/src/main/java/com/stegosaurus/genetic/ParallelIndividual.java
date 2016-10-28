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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

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
   * A future for the simulation and calculation of a fitness function.
   * If this is set at some point, its return value will be provided whenever
   * calculateFitness() is called, unless a call to simulate() is issued,
   * in which case the future is invalidated.
   */
  private ListenableFuture<Double> fitnessFuture = null;

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
  public synchronized Individual<T> simulate() {
    fitnessFuture = null;
    decorated.simulate();
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized double calculateFitness() {
    if(fitnessFuture != null) {
      try {
        return fitnessFuture.get();
      } catch(InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    } else {
      return decorated.calculateFitness();
    }
  }

  /**
   * Start to run simulate and calculateFitness for this individual, on
   * another thread.
   * The result may be later retreived via the calculateFitness() function.
   * @param executorService the service to use to start up the calculation.
   */
  public synchronized void
  startFitnessCalculation(ListeningExecutorService executorService) {
    fitnessFuture = executorService.submit(new Callable<Double>() {
      public Double call() {
        decorated.simulate();
        return decorated.calculateFitness();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  public void crossover(Individual<T> other) {
    /* The AbstractIndividual class does some funny stuff to its mates when
     * they're also of type AbstractIndividual, and it's theoretically
     * possible for other classes to do much of the same stuff, so we're well
     * served by being as specific as possible here. In other words,
     * we send the decorated object for crossover when we detect that we're
     * crossing over with another ParallelIndividual.
     */
    if(other instanceof ParallelIndividual) {
      decorated.crossover(((ParallelIndividual<T>) other).decorated);
    } else {
      decorated.crossover(other);
    }
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
    return Double.compare(calculateFitness(), other.calculateFitness());
  }
}
