package com.stegosaurus.steganographers.coders;

import java.io.InputStream;

import com.stegosaurus.steganographers.coders.Coder;

/**
 * A coder which operates on images.
 * 
 * @author joe
 */
public abstract class ImgCoder implements Coder {

	/**
	 * An InputStream representing the image data.
	 */
	protected InputStream instream;

	/**
	 * Initialize the ImgCoder and read the header in.
	 * 
	 * @param in
	 *            the InputStream with the image data.
	 */
	public ImgCoder(InputStream in) throws Exception {
		instream = in;
	}

	/**
	 * Hide the given bit in the LSB of the carrier int.
	 * 
	 * @param bit
	 *            either 0 or 1, the bit to place in the carrier
	 * @param carrier
	 *            the int whose LSB will be modified
	 * @return the modified carrier.
	 */
	public static int HideInLSB(int bit, int carrier) {
		int retval;
		if (bit == 0) {
			/*
			 * If we have a zero, zero out the last bit
			 */
			retval = carrier & 0xfffffffe;
		} else {
			retval = carrier | 1;
		}
		return retval;
	}
}
