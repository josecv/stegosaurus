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
