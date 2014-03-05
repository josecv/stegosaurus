package com.stegosaurus.testing;

import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.stegosaurus.stegosaurus.StegosaurusModule;

/**
 * Provides a Guice injector to any test cases that may need one.
 * The general mechanic is that a test case that needs to use the Injector
 * will extend from this one.
 * The injector is configured with the StegosaurusModule, as you might expect.
 */
public abstract class TestWithInjection {

  /**
   * The Guice injector.
   */
  protected Injector injector;

  /**
   * Set up the test.
   * Should be explicitly called by any children (i.e. super.setUp()).
   */
  @Before
  public void setUp() {
    injector = Guice.createInjector(new StegosaurusModule());
  }
}
