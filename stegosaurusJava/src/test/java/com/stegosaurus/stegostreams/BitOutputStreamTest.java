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
package com.stegosaurus.stegostreams;

import static org.junit.Assert.assertArrayEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BitOutputStreamTest {

  /**
   * The BitOutputStream under test.
   */
  private BitOutputStream s;

  /**
   * Set up a test.
   */
  @Before
  public void setUp() {
    s = new BitOutputStream();
  }

  /**
   * Tear down a test.
   */
  @After
  public void tearDown() {
    s.close();
    s = null;
  }

  /**
   * Test the write methods by writing some bits.
   */
  @Test
  public void testWrite() {
    s.write(0);
    s.write(0);
    s.write(1);
    s.write(1);
    s.write(0);
    s.write(0);
    s.write(0);
    s.write(1);
    s.write(0);
    s.write(1);
    s.write(1);
    byte[] data = { 0b00110001, 0b01100000 };
    assertArrayEquals("Write failure", data, s.data());
  }

  /**
   * Test the writeToEndOfByte method.
   */
  @Test
  public void testWriteToEndOfByte() {
    s.write(0);
    s.write(1);
    s.write(0);
    s.write(1);
    s.write(0);
    s.writeToEndOfByte(1);
    byte[] data = { 0b01010111 };
    assertArrayEquals("Write to end of byte failure", data, s.data());
  }

  /**
   * Test the writeInt method.
   */
  @Test
  public void testWriteInt() {
    byte[] expected = { 0x3A, 0x4E, 0x1F };
    s.writeInt(0x3A, 8);
    s.write(0);
    s.write(1);
    s.writeInt(0, 2);
    s.writeInt(0xE1, 8);
    s.writeInt(0b111, 3);
    s.write(1);
    byte[] result = s.data();
    assertArrayEquals("WriteInt failed", expected, result);
  }

  /**
   * Test the reset method.
   */
  @Test
  public void testReset() {
    byte[] expected = { 0x3A, 0x4E };
    s.writeInt(0xFF, 8);
    s.writeInt(0xCA, 8);
    s.reset();
    s.writeInt(0x3A4E, 16);
    byte[] result = s.data();
    assertArrayEquals("Reset not working", expected, result);
  }
}
