package com.stegosaurus.huffman;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeNoException;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stegosaurus.huffman.HuffmanEncoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.huffman.trees.JPEGTreeNodeTest;
import com.stegosaurus.stegostreams.BitOutputStream;

public class HuffmanEncoderTest {

  private HuffmanEncoder encoder;

  /**
   * Set up our test by creating a new encoder.
   */
  @Before
  public void setUp() {
    encoder = new HuffmanEncoder(new JPEGHuffmanDecoder(
      JPEGTreeNodeTest.getTable()));
  }

  /**
   * Ensure that the encoder is properly disposed of.
   */
  @After
  public void tearDown() {
    encoder = null;
  }

  /**
   * Test the encode method, working with multiple bytes.
   */
  @Test
  public void testEncode() {
    byte[] data = {0, 2, 9, 3, 1, 0xA};
    /* 00 011 1111110 100 010 11111110 */
    /* 00011111 11101000 10111111 10000000 */
    byte[] expected = {0b00011111, (byte) 0b11101000, (byte) 0b10111111,
                       (byte) 0b10000000};
    try {
      byte[] result = encoder.encode(data);
      assertArrayEquals("Wrong value encoded", expected, result);
      BitOutputStream os = new BitOutputStream();
      encoder.encode(data, os);
      result = os.data();
      os.close();
      assertArrayEquals("Wrong value written to stream", expected, result);
    } catch (IOException ioe) {
      assumeNoException(ioe);
    }
  }

  /**
   * Test the encode method for encoding a single byte.
   */
  @Test
  public void testEncodeSingle() {
    byte data = 2;
    byte[] expected = {0b01100000};
    try {
      byte[] result = encoder.encode(data);
      assertArrayEquals("Single byte not properly encoded", expected, result);
      BitOutputStream os = new BitOutputStream();
      encoder.encode(data, os);
      result = os.data();
      assertArrayEquals("Single byte not encoded to stream", expected, result);
      os.close();
    } catch(IOException ioe) {
      assumeNoException(ioe);
    }
  }
}
