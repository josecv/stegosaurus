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
package com.stegosaurus.steganographers.genetic;

import com.stegosaurus.genetic.GAParameters;

/**
 * A container for  the parameters for the GAs in use within the class
 * GeneticPM1.
 * Mostly split into two: the parameters for the seed optimization, and
 * those for the sequence optimization.
 */
public class GeneticPM1Parameters {

  /**
   * CTOR. See individual fields for parameter documentation.
   * @param SPopSize
   * @param SNumberOfGenerations
   * @param SElitismRate
   * @param SMutationRate
   * @param SSelectionGradient
   * @param BPopSize
   * @param BNumberOfGenerations
   * @param BElitismRate
   * @param BMutationRate
   * @param BSelectionGradient
   */
  public GeneticPM1Parameters(int SPopSize, int SNumberOfGenerations,
      double SElitismRate, double SMutationRate, double SSelectionGradient,
      int BPopSize, int BNumberOfGenerations, double BElitismRate,
      double BMutationRate, double BSelectionGradient) {
    this.SPopSize = SPopSize;
    this.SNumberOfGenerations = SNumberOfGenerations;
    this.SElitismRate = SElitismRate;
    this.SMutationRate = SMutationRate;
    this.SSelectionGradient = SSelectionGradient;
    this.SParams = new GAParameters(SPopSize, Short.SIZE, SElitismRate,
        SMutationRate);
    this.BPopSize = BPopSize;
    this.BNumberOfGenerations = BNumberOfGenerations;
    this.BElitismRate = BElitismRate;
    this.BMutationRate = BMutationRate;
    this.BSelectionGradient = BSelectionGradient;
  }

  /**
   * The population size of the seed-optimizing GA.
   */
  private final int SPopSize;

  /**
   * The number of generations of the seed-optimizing GA to run.
   */
  private final int SNumberOfGenerations;

  /**
   * The elitism rate of the seed-optimizing GA.
   */
  private final double SElitismRate;

  /**
   * The mutation rate of the seed-optimizing GA.
   */
  private final double SMutationRate;

  /**
   * The gradient factor for rank selection in the seed-optimizing GA.
   */
  private final double SSelectionGradient;

  /**
   * The parameters structure for the seed-optimizing GA.
   */
  private final GAParameters SParams;

  /**
   * The population size for the blockiness-optimizing GA.
   */
  private final int BPopSize;

  /**
   * The number of generations of the blockiness-optimizing GA to run.
   */
  private final int BNumberOfGenerations;

  /**
   * The elitism rate of the blockiness-optimizing GA.
   */
  private final double BElitismRate;

  /**
   * The mutation rate of the blockiness-optimizing GA.
   */
  private final double BMutationRate;

  /**
   * The gradient factor for rank selection in the blockiness-optimizing GA.
   */
  private final double BSelectionGradient;

  /**
   * Get the population size of the seed-optimizing GA.
   */
  public int getSPopSize() {
    return SPopSize;
  }

  /**
   * Get the number of generations of the seed-optimizing GA to run.
   */
  public int getSNumberOfGenerations() {
    return SNumberOfGenerations;
  }

  /**
   * Get the elitism rate of the seed-optimizing GA.
   */
  public double getSElitismRate() {
    return SElitismRate;
  }

  /**
   * Get the mutation rate of the seed-optimizing GA.
   */
  public double getSMutationRate() {
    return SMutationRate;
  }

  /**
   * Get the gradient factor for rank selection in the seed-optimizing GA.
   */
  public double getSSelectionGradient() {
    return SSelectionGradient;
  }

  /**
   * Get the parameters structure for the seed-optimizing GA.
   */
  public GAParameters getSParams() {
    return SParams;
  }

  /**
   * Get the population size for the blockiness-optimizing GA.
   */
  public int getBPopSize() {
    return BPopSize;
  }

  /**
   * Get the number of generations of the blockiness-optimizing GA to run.
   */
  public int getBNumberOfGenerations() {
    return BNumberOfGenerations;
  }

  /**
   * Get the elitism rate of the blockiness-optimizing GA.
   */
  public double getBElitismRate() {
    return BElitismRate;
  }

  /**
   * Get the mutation rate of the blockiness-optimizing GA.
   */
  public double getBMutationRate() {
    return BMutationRate;
  }

  /**
   * Get the gradient factor for rank selection in the blockiness-optimizing GA.
   */
  public double getBSelectionGradient() {
    return BSelectionGradient;
  }
}
