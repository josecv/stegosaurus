package com.stegosaurus.steganographers.genetic;

import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.SEED_ELITISM_RATE;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.SEED_MUTATION_RATE;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.SEED_NUMBER_OF_GENERATIONS;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.SEED_POP_SIZE;
import static com.stegosaurus.steganographers.genetic.GeneticPM1Parameters.SEED_SELECTION_GRADIENT;

import java.util.Random;

import com.google.inject.Inject;
import com.stegosaurus.cpp.JPEGImage;
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
   * Optimize the seed for the embed request given.
   * @param request the embed request.
   * @return the best seed we could come up with.
   */
  private short optimizeSeed(EmbedRequest request) {
    SelectionOperator<SeedChangeCountIndividual> o =
      new RankSelection<>(SEED_SELECTION_GRADIENT);
    SeedChangeCountIndividualFactory factory =
      new SeedChangeCountIndividualFactory(request, embedderFactory);
    GeneticAlgorithm<SeedChangeCountIndividual> algo =
        new GeneticAlgorithm<>(factory, o, motherNature,
                               SEED_POP_SIZE, Short.SIZE, SEED_ELITISM_RATE,
                               SEED_MUTATION_RATE);
    algo.init();
    Individual<SeedChangeCountIndividual> result =
      algo.runNGenerations(SEED_NUMBER_OF_GENERATIONS);
    return result.getChromosome().asShort();
  }

  /**
   * Optimize the PM sequence for the embed request and seed given.
   * @param request the embed request.
   * @param seed the seed.
   * @return the best PM sequence we could find.
   */
  private PMSequence optimizeSequence(EmbedRequest request, short seed) {
    SelectionOperator<BlockinessIndividual> o =
      new RankSelection<>(SEED_SELECTION_GRADIENT);
    BlockinessIndividualFactory factory =
      new BlockinessIndividualFactory(request, seed, embedderFactory);
    GeneticAlgorithm<BlockinessIndividual> algo =
        new GeneticAlgorithm<>(factory, o, motherNature,
                               SEED_POP_SIZE, Short.SIZE, SEED_ELITISM_RATE,
                               SEED_MUTATION_RATE);
    algo.init();
    Individual<BlockinessIndividual> result =
      algo.runNGenerations(SEED_NUMBER_OF_GENERATIONS);
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
