package com.stegosaurus.huffman;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.huffman.trees.JPEGTreeNodeTest;
import com.stegosaurus.stegostreams.BitInputStream;

public class HuffmanDecoderTest {

  @Test
  public void testDecode() {
    HuffmanDecoder huff = new JPEGHuffmanDecoder(JPEGTreeNodeTest.getTable());
    byte[] args = {0b00010011, (byte) 0b11101110}; 
    byte[] retval = huff.decode(new BitInputStream(args));
    byte[] expected = {0x00, 0x01, 0x02, 0x06, 0x06};
    assertTrue("Wrong return value from decode", Arrays.equals(expected, retval));
  }
  
  @Test
  public void testEquals() {
    HuffmanDecoder huff = new JPEGHuffmanDecoder(JPEGTreeNodeTest.getTable());
    HuffmanDecoder huff2 = new JPEGHuffmanDecoder(JPEGTreeNodeTest.getTable());
    assertTrue("huff.equals huff2 returning false", huff.equals(huff2));
    assertTrue("huff2.equals huff returning false", huff2.equals(huff));
    assertFalse("huff == huff2", huff == huff2);
  }
}
