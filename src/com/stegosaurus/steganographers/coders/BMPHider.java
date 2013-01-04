package com.stegosaurus.steganographers.coders;

import java.io.InputStream;

import steganographers.coders.BMPCoder;
import com.stegosaurus.steganographers.coders.Hider;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.ArrayUtils;

/**
 * Hides payload data in a BMP carrier.
 * 
 * @author joe
 */
public class BMPHider extends BMPCoder implements Hider {

	/**
	 * Construct the BMP Hider to deal with the input stream given.
	 * 
	 * @param in
	 * @throws Exception
	 */
	public BMPHider(InputStream in) throws Exception {
		super(in);
	}

	/**
	 * Close this Hider and return the entire carrier as a byte stream.
	 * 
	 * @return the carrier file (with the hidden payload, evidently).
	 */
	@Override
	public byte[] close() throws Exception {
		instream.read(imgdata, bytes_read, data_size - bytes_read);
		instream.close();
		return ArrayUtils.addAll(ArrayUtils.addAll(header, dib), imgdata);
	}

	/**
	 * Hide the number of bits given from the data stream in the carrier.
	 * 
	 * @param datastream
	 *            the input stream to take the payload from.
	 * @param count
	 *            the number of bits to take from the stream.
	 */
	@Override
	public void Hide(BitInputStream datastream, int count) throws Exception {
		for (int i = 0; i < count; i++) {
			int off = NextPixel();
			/*
			 * Actually place the bit in the lsb of the pixel
			 */
			imgdata[off] = (byte) HideInLSB(datastream.read(), imgdata[off]);
		}
	}
}
