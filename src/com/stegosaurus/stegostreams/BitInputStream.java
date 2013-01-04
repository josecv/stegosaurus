package com.stegosaurus.stegostreams;

import java.io.InputStream;

/**
 * Produces, bit by bit, the byte array given. The least significant bit of the
 * 0th byte will be returned, then the second to least significant bit of the
 * 0th byte, and so on (Little Endian).
 * 
 * @author joe
 */
public class BitInputStream extends InputStream {

	/**
	 * The byte array.
	 */
	protected byte[] in;
	/**
	 * The next bit to read.
	 */
	protected int index;

	/**
	 * Initialize the input stream with the input given.
	 * 
	 * @param in
	 *            the array of bytes whose bits we will return one by one.
	 */
	public BitInputStream(byte[] in) {
		this.in = in;
		index = 0;
	}

	/**
	 * Get the next bit (0 or 1) from the byte array.
	 * 
	 * @return the next bit.
	 */
	@Override
	public int read() {
		int retval = (in[index / 8] >> (index % 8)) & 1;
		index++;
		return retval;
	}

	/**
	 * Get the number of available bits that can be read without blocking, or 0
	 * if there is nothing left to read.
	 * 
	 * @return the number of bits that can be read.
	 */
	@Override
	public int available() {
		return (in.length * 8) - index;
	}
}
