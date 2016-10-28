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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.stegosaurus.cpp.JPEGImage;
import com.stegosaurus.steganographers.Embedder;
import com.stegosaurus.steganographers.pm1.AbstractPM1Test;

/**
 * Tests the GeneticPM1 class.
 */
public class GeneticPM1Test extends AbstractPM1Test {
  /**
   * The algorithm factory.
   */
  private GeneticPM1Factory factory;

  /**
   * Set up the test.
   */
  @Before
  public void setUp() {
    super.setUp();
    factory = injector.getInstance(GeneticPM1Factory.class);
  }

  /**
   * One of the all-or-nothing-in-a-huge-operation tests that stegosaurus is
   * famous for.
   */
  @Test
  public void testAllTheThings() {
    Embedder algo = factory.build();
    JPEGImage stego = algo.embed(request);
    assertImageContainsMessage("Stego image lacks message", stego, KEY,
                               MSG.getBytes());
  }
}
