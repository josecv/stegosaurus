package com.stegosaurus.stegosaurus;

import java.nio.charset.Charset;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.AbstractModule;
import com.stegosaurus.concurrent.ListeningExecutorServiceProvider;
import com.stegosaurus.crypt.DefaultPermutationProvider;
import com.stegosaurus.crypt.PermutationProvider;
import com.stegosaurus.genetic.GeneticModule;
import com.stegosaurus.steganographers.SteganographersModule;
import com.stegosaurus.stegutils.ByteBufferHelper;
import com.stegosaurus.stegutils.ByteBufferHelperImpl;

/**
 * The Stegosaurus module provides dependency injection to Stegosaurus
 * classes.
 * Particularly, it declares implementations of services required by
 * Stegosaurus classes.
 */
public class StegosaurusModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new SteganographersModule());
    install(new GeneticModule());
    bind(ByteBufferHelper.class).to(ByteBufferHelperImpl.class);
    bind(PermutationProvider.class).to(DefaultPermutationProvider.class);
    bind(ListeningExecutorService.class)
      .toProvider(ListeningExecutorServiceProvider.class);
    bind(HashFunction.class).toInstance(Hashing.sipHash24());
    bind(Charset.class).toInstance(Charset.defaultCharset());
  }
}
