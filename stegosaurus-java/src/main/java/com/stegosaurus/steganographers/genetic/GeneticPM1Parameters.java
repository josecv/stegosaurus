package com.stegosaurus.steganographers.genetic;

/**
 * The parameters for the GAs in use within the class GeneticPM1.
 * Mostly split into two: the parameters for the seed optimization, and
 * those for the sequence optimization.
 */
class GeneticPM1Parameters {
  /**
   * The population size of the seed-optimizing GA.
   */
  public static final int SEED_POP_SIZE = 50;

  /**
   * The number of generations of the seed-optimizing GA to run.
   */
  public static final int SEED_NUMBER_OF_GENERATIONS = 50;

  /**
   * The elitism rate of the seed-optimizing GA.
   */
  public static final double SEED_ELITISM_RATE = 0.4;

  /**
   * The mutation rate of the seed-optimizing GA.
   */
  public static final double SEED_MUTATION_RATE = 0.3;

  /**
   * The gradient factor for rank selection in the seed-optimizing GA.
   */
  public static final double SEED_SELECTION_GRADIENT = 10;

  /**
   * The population size for the blockiness-optimizing GA.
   */
  public static final int BLOCKINESS_POP_SIZE = 50;

  /**
   * The number of generations of the blockiness-optimizing GA to run.
   */
  public static final int BLOCKINESS_NUMBER_OF_GENERATIONS = 50;

  /**
   * The elitism rate of the blockiness-optimizing GA.
   */
  public static final double BLOCKINESS_ELITISM_RATE = 0.5;

  /**
   * The mutation rate of the blockiness-optimizing GA.
   */
  public static final double BLOCKINESS_MUTATION_RATE = 0.1;

  /**
   * The gradient factor for rank selection in the blockiness-optimizing GA.
   */
  public static final double BLOCKINESS_SELECTION_GRADIENT = 10;
}
