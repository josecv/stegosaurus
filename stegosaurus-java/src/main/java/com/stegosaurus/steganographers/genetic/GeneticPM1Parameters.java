package com.stegosaurus.steganographers.genetic;

import com.stegosaurus.genetic.GAParameters;

/**
 * The parameters for the GAs in use within the class GeneticPM1.
 * Mostly split into two: the parameters for the seed optimization, and
 * those for the sequence optimization.
 */
class GeneticPM1Parameters {
  /**
   * The population size of the seed-optimizing GA.
   */
  public static final int S_POP_SIZE = 50;

  /**
   * The number of generations of the seed-optimizing GA to run.
   */
  public static final int S_NUMBER_OF_GENERATIONS = 50;

  /**
   * The elitism rate of the seed-optimizing GA.
   */
  public static final double S_ELITISM_RATE = 0.4;

  /**
   * The mutation rate of the seed-optimizing GA.
   */
  public static final double S_MUTATION_RATE = 0.3;

  /**
   * The gradient factor for rank selection in the seed-optimizing GA.
   */
  public static final double S_SELECTION_GRADIENT = 10;

  /**
   * The parameters structure for the seed-optimizing GA.
   */
  public static final GAParameters S_PARAMS = new GAParameters(S_POP_SIZE,
      Short.SIZE, S_ELITISM_RATE, S_MUTATION_RATE);

  /**
   * The population size for the blockiness-optimizing GA.
   */
  public static final int B_POP_SIZE = 50;

  /**
   * The number of generations of the blockiness-optimizing GA to run.
   */
  public static final int B_NUMBER_OF_GENERATIONS = 50;

  /**
   * The elitism rate of the blockiness-optimizing GA.
   */
  public static final double B_ELITISM_RATE = 0.5;

  /**
   * The mutation rate of the blockiness-optimizing GA.
   */
  public static final double B_MUTATION_RATE = 0.1;

  /**
   * The gradient factor for rank selection in the blockiness-optimizing GA.
   */
  public static final double B_SELECTION_GRADIENT = 10;
}
