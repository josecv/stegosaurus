package com.stegosaurus.concurrent;

import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * An object capable of providing an ExecutorService for stegosaurus classes
 * that need access to one.
 * Only provides a single executor service, which is built when first
 * requested and provided on every subsequent request.
 */
@Singleton
public class ListeningExecutorServiceProvider
  implements Provider<ListeningExecutorService> {

  private ListeningExecutorService service = null;

  /**
   * Get an executor service for this application.
   * @return the executor service.
   */
  public ListeningExecutorService get() {
    if(service == null) {
      service =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    }
    return service;
  }
}
