package com.stegosaurus.huffman;

import static org.junit.Assert.*;

import org.junit.Test;

import com.stegosaurus.huffman.HuffmanEncoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.huffman.trees.JPEGTreeNodeTest;

public class HuffmanEncoderTest {

	@Test
	public void testEncode() {
		HuffmanEncoder encoder = new HuffmanEncoder(new JPEGHuffmanDecoder(
				JPEGTreeNodeTest.getTable()));
		byte[] data = {0, 2, 9, 3, 1, 0xA};
		/* 00 011 1111110 100 010 11111110 */
		/* 00011111 11101000 10111111 10000000 */
		byte[] expected = {0b00011111, (byte) 0b11101000, (byte) 0b10111111, (byte) 0b10000000};
		byte[] retval = new byte[0];
		try {
			retval = encoder.encode(data);
			assertArrayEquals("Wrong value returned", expected, retval);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception: " + e.getMessage());
		}
	}

}
