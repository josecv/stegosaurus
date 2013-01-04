package com.stegosaurus.stegostreams;

/**
 * Like the BitInputStream but in Big Endian order.
 * 
 * @author joe
 * @see BitInputStream
 * 
 */
public class SequentialBitInputStream extends BitInputStream {

	/**
	 * Initialize this input stream with the byte array given.
	 * 
	 * @param in the byte array to work with.
	 */
	public SequentialBitInputStream(byte[] in) {
		super(in);
	}
	
	/**
	 * Read the next bit
	 * 
	 * @return the next bit.
	 */
	@Override
	public int read() {
		int retval = (in[index/8] >> (7 - (index % 8))) & 1;
		index++;
		return retval;
	}
}
