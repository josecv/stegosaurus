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
package com.stegosaurus.stegosaurus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.stegosaurus.testing.TestWithInjection;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests the StegosaurusFacadeImpl class.
 * TODO It would be nice to have a unit test that mocks various objects
 */
public class StegosaurusFacadeImplTest extends TestWithInjection {
  /**
   * The message.
   */
  private static String MSG = "This is my message\n" +
                              "How nice it is to have a message";

  /**
   * The key.
   */
  private static String KEY = "Lionel Messi";

  /**
   * Test the facade end to end.
   */
  @Test
  public void integrationTest() throws Exception {
    StegosaurusFacadeImpl facade =
      injector.getInstance(StegosaurusFacadeImpl.class);
    InputStream in =
      StegosaurusFacadeImplTest.class.getResourceAsStream("napoleon.jpg");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    facade.embed(in, out, MSG, KEY);
    ByteArrayInputStream readBack = new ByteArrayInputStream(out.toByteArray());
    String result = facade.extract(readBack, KEY);
    assertEquals(MSG, result);
  }
}
