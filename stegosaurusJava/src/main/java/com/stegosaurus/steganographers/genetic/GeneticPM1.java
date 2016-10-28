package com.stegosaurus.steganographers.genetic;

import java.util.Random;

import com.google.inject.Inject;
import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.genetic.GAFactory;
import com.stegosaurus.genetic.GAParameters;
import com.stegosaurus.genetic.GeneticAlgorithm;
import com.stegosaurus.genetic.Individual;
import com.stegosaurus.genetic.IndividualFactory;
import com.stegosaurus.genetic.RankSelection;
import com.stegosaurus.genetic.SelectionOperator;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.EmbedRequest;
import com.stegosaurus.steganographers.pm1.PM1Embedder;
import com.stegosaurus.steganographers.pm1.PM1EmbedderFactory;
import com.stegosaurus.steganographers.pm1.PMSequence;

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
class GeneticPM1 implements Embedder {

  /**
   * The embedder factory in use.
   */
  private PM1EmbedderFactory embedderFactory;

  /**
   * The random object involved in the mutation of individual solutions.
   * Note that mother nature is well and truly random (ie we don't care if
   * it doesn't repeat at all), so we don't really give much of a damn
   * about using it over and over without re-seeding, or even in multiple
   * places concurrently.
   */
  Random motherNature = new Random();

  /**
   * The factory used to build parallel GAs.
   */
  private GAFactory gaFactory;

  /**
   * The genetic parameters to startup both algorithms.
   */
  private GeneticPM1Parameters globalParams;

  /**
   * Construct a new GeneticPM1 object.
   * @param embedderFactory the embedder factory to use.
   * @param gaFactory the genetic algorithm factory
   * @param globalParams the global parameters
   */
  GeneticPM1(PM1EmbedderFactory embedderFactory,
      GAFactory gaFactory, GeneticPM1Parameters globalParams) {
    this.embedderFactory = embedderFactory;
    this.gaFactory = gaFactory;
    this.globalParams = globalParams;
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
    GeneticAlgorithm<C> algo = gaFactory.build(factory, o, motherNature,
        params);
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
        globalParams.getSSelectionGradient(),
        globalParams.getSNumberOfGenerations(),
        factory, globalParams.getSParams());
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
    GAParameters params = new GAParameters(globalParams.getBPopSize(),
      (request.getMessage().length * 8) + 16, globalParams.getBElitismRate(),
      globalParams.getBMutationRate());
    Individual<BlockinessIndividual> result = optimize(request,
        globalParams.getBSelectionGradient(),
        globalParams.getBNumberOfGenerations(),
        factory, params);
    return result.getChromosome();
  }

  /**
   * Embeds the request's message into its cover image given, with its key.
   * @param request the embed request.
   * @return the steganographic JPEG image
   */
  public JPEGImage embed(EmbedRequest request) {
    short seed = optimizeSeed(request);
    PMSequence sequence = optimizeSequence(request, seed);
    PM1Embedder embedder = embedderFactory.build(sequence);
    return embedder.embed(request, seed);
  }
}
