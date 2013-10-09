package com.stegosaurus.steganographers.genetic;

/**
 * A more sophisticated dummy individual whose fitness is a floating point
 * representation of its chromosome.
 * Useful for complex tests that need to be able to differentiate between
 * good and bad solutions in a meaningful way.
 * Its Chromosomes should be 64 genes long, so as to make them easily
 * convertible to doubles.
 * Note however that the fitness is not necessarily equal to the
 * chromosome's asDouble() value, given that the former must be between
 * 0 and 1, and the latter has no such constraint.
 * The precise algorithm for the fitness function is:
 * <code>
 *    a = abs(chromosome.asDouble())
 *    if a &lt;= 1
 *      return a
 *    else
 *      log = floor(log10(a))
 *      return (a / (10 ^ log)) / 10
 * </code>
 * This algorithm lumps the majority of values around .1 to .9, but allows for
 * some very tiny values.
 */
public class DirectFitnessIndividual
  extends AbstractIndividual<DirectFitnessIndividual> {

  /**
   * CTOR.
   * @param chromosome the chromosome for this individual.
   */
  public DirectFitnessIndividual(Chromosome chromosome) {
    super(chromosome);
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
    double retval = Math.abs(chromosome.asDouble());
    if(retval <= 1) {
      return retval;
    }
    int log = (int) Math.floor(Math.log10(retval));
    return (retval / Math.pow(10, log)) / 10;
  }
}
