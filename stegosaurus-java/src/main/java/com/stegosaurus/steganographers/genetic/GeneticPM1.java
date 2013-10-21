package com.stegosaurus.steganographers.genetic;

import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.B_ELITISM_RATE;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.B_MUTATION_RATE;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.B_NUMBER_OF_GENERATIONS;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.B_POP_SIZE;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.B_SELECTION_GRADIENT;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.S_NUMBER_OF_GENERATIONS;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.S_PARAMS;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.S_SELECTION_GRADIENT;

import java.util.Random;

import com.google.inject.Inject;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.genetic.GAParameters;
import com.stegosaurus.genetic.GeneticAlgorithm;
import com.stegosaurus.genetic.Individual;
import com.stegosaurus.genetic.IndividualFactory;
import com.stegosaurus.genetic.RankSelection;
import com.stegosaurus.genetic.SelectionOperator;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.PM1Embedder;
import com.stegosaurus.steganographers.PMSequence;

/**
 * The core algorithm of Stegosaurus' JPEG capabilities: embeds messages into
 * JPEG images, using genetic algorithms to find the best (ie hardest to
 * detect algorithm).
 * <p>First optimizes the seed, so as to attempt to modify the image as little
 * as possible, and then optimizes the plus minus sequence, used to embed
 * any bits that do need to be changed, so as to attempt to make the
 * modifications as hard to detect as possible.</p>
 * <p>Triviality of detection is quantified using the ratio of an image's
 * blockiness to a lossily cropped equivalent's blockiness</p>
 * @see PM1Embedder for a discussion of the structure of the stego image.
 */
public class GeneticPM1 {

  /**
   * The embedder factory in use.
   */
  private PM1Embedder.Factory embedderFactory;

  /**
   * The random object involved in the mutation of individual solutions.
   * Note that mother nature is well and truly random (ie we don't care if
   * it doesn't repeat at all), so we don't really give much of a damn
   * about using it over and over without re-seeding, or even in multiple
   * places concurrently.
   */
  Random motherNature = new Random();

  /**
   * Construct a new GeneticPM1 object. Ought to be invoked by Guava.
   * @param embedderFactory the embedder factory to use.
   */
  @Inject
  public GeneticPM1(PM1Embedder.Factory embedderFactory) {
    this.embedderFactory = embedderFactory;
  }

  /**
   * Run a GA to optimize some thing or another.
   * @param <C> the specific individual type in use.
   * @param request the embed request we're working with.
   * @param gradient the gradient for the rank selection.
   * @param factory the individual factory we want to use.
   * @param params the GA parameters for this optimization.
   * @param generations the number of generations to run.
   * @return the fittest individual.
   */
  private <C extends Individual<C>> Individual<C> optimize(
      EmbedRequest request, double gradient, int generations,
      IndividualFactory<C> factory, GAParameters params) {
    SelectionOperator<C> o = new RankSelection<>(gradient);
    GeneticAlgorithm<C> algo = new GeneticAlgorithm<>(factory, o,
      motherNature, params);
    algo.init();
    return algo.runNGenerations(generations);
  }

  /**
   * Optimize the seed for the embed request given.
   * @param request the embed request.
   * @return the best seed we could come up with.
   */
  private short optimizeSeed(EmbedRequest request) {
    SeedChangeCountIndividualFactory factory =
      new SeedChangeCountIndividualFactory(request, embedderFactory);
    Individual<SeedChangeCountIndividual> result = optimize(request,
        S_SELECTION_GRADIENT, S_NUMBER_OF_GENERATIONS, factory, S_PARAMS);
    return result.getChromosome().asShort();
  }

  /**
   * Optimize the PM sequence for the embed request and seed given.
   * @param request the embed request.
   * @param seed the seed.
   * @return the best PM sequence we could find.
   */
  private PMSequence optimizeSequence(EmbedRequest request, short seed) {
    BlockinessIndividualFactory factory =
      new BlockinessIndividualFactory(request, seed, embedderFactory);
    GAParameters params = new GAParameters(B_POP_SIZE,
      (request.getMessage().length * 8) + 16, B_ELITISM_RATE,
      B_MUTATION_RATE);
    Individual<BlockinessIndividual> result = optimize(request,
        B_SELECTION_GRADIENT, B_NUMBER_OF_GENERATIONS, factory, params);
    return result.getChromosome();
  }

  /**
   * Embeds the request's message into its cover image given, with its key.
   * @param request the embed request.
   */
  public JPEGImage embed(EmbedRequest request) {
    short seed = optimizeSeed(request);
    PMSequence sequence = optimizeSequence(request, seed);
    PM1Embedder embedder = embedderFactory.build(new Random(), sequence);
    return embedder.embed(request, seed);
  }
}
