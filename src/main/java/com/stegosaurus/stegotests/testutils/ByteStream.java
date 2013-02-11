package com.stegosaurus.stegotests.testutils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Given a byte array returns them byte by byte in stream form. Useful as
 * a pretend file.
 * @author joe
 *
 */
public class ByteStream extends InputStream {
	
	/**
	 * The array we will be returning.
	 */
	private byte[] arr;
	
	/**
	 * Which index we are currently on.
	 */
	private int i;

	public ByteStream(byte[] arr) {
		i = 0;
		this.arr = arr.clone();
	}
	
	@Override
	public int read() throws IOException {
		if (i > arr.length) {
			throw new IOException ("This byte stream is exhausted.");
		}
		byte retval = arr[i];
		i++;
		return retval;
	}

	
	@Override
	public int available() {
		return arr.length - i;
	}
}
