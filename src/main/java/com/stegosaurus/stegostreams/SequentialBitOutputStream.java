package com.stegosaurus.stegostreams;

/**
 * LIke BitOutputStream but deals in big endian.
 * 
 * @author joe
 * 
 */
public class SequentialBitOutputStream extends BitOutputStream {

	public SequentialBitOutputStream() {
		super();
	}

	/**
	 * Write a new bit to the stream.
	 * 
	 * @param b
	 *            the bit to write.
	 */
	@Override
	public void write(int b) {
		if (i / 8 == data.size()) {
			data.add((byte) 0);
		}
		byte current = data.get(i / 8);
		current |= (byte) (b << 7 - (i % 8));
		data.set(i / 8, current);
		i++;
	}
}
