package com.stegosaurus.huffman;

import com.stegosaurus.huffman.trees.JPEGTreeNode;

/* TODO: This is written to be easily testable, but it's
 * quite ill-designed. Refactor it without losing that testability.
 */

/**
 * Decodes Huffman encoded data specifically from JPEG files.
 * 
 * @author joe
 */
public class JPEGHuffmanDecoder extends HuffmanDecoder {

	/**
	 * Construct the JPEGHuffmanDecoder from the Huffman table given.
	 * 
	 * @param table the huffman table. Should be passed after the marker, the
	 * size bytes, and the DHT class/id.
	 */
	public JPEGHuffmanDecoder(byte[] table) {
		super();
		root = new JPEGTreeNode(table.clone());
	}
}
