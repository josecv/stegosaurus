package com.stegosaurus.steganographers.coders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import steganographers.coders.ImgCoder;

/*
 * The JPEG standard splits a file into chunks delimited by markers which are
 * the 0xFF byte followed by any non-zero byte. Should an actual 0xFF be
 * desired, it is escaped by placing a zero byte after it. Should a 0xFF be
 * followed by one or more 0xFFs, it is not a marker but padding, usually
 * preceding a marker, and should be ignored.
 */
/**
 * Deals with JPG images as carriers.
 * 
 * @author joe
 */
public abstract class JPEGCoder extends ImgCoder {

	/**
	 * Code for the start of image marker.
	 */
	protected static final int SOI_MARKER = 0xDB;
	/**
	 * Code for the start of frame marker. Indicates this as a baseline DCT JPG.
	 * Gives width height number of components and component subsampling.
	 */
	protected static final int SOF0_MARKER = 0xC0;
	/**
	 * Code for the start of frame marker. Indicates this as a progressive DCT
	 * JPG. Same as with SOF0.
	 */
	protected static final int SOF2_MARKER = 0xC2;
	/**
	 * Code for the Huffman tables marker.
	 */
	protected static final int DHT_MARKER = 0xC4;
	/**
	 * Code for the quantization tables marker.
	 */
	protected static final int DQT_MARKER = 0xDB;
	/**
	 * Code for the Define Restart Interval marker. Specifies the intervals
	 * between RSTn markers.
	 */
	protected static final int DRI_MARKER = 0xDD;
	/**
	 * Code for the start of scan marker. Starts the image scan, from top to
	 * bottom.
	 */
	protected static final int SOS_MARKER = 0xDA;
	/**
	 * Code for a text comment.
	 */
	protected static final int COM_MARKER = 0xFE;
	/**
	 * Code for the end of image marker.
	 */
	protected static final int EOI_MARKER = 0xD9;
	/**
	 * A Buffer with the entire JPG file. Used so that we can look ahead.
	 */
	protected byte[] buffer;

	/**
	 * The index of the last returned segment.
	 */
	protected int segment_index;

	/**
	 * Return whether the given byte is an RSTn marker. It would be indicated by
	 * being 0xDn, where n=0..7.
	 * 
	 * @param b
	 *            the byte to test.
	 * @return true if it is an RSTn marker.
	 */
	protected static boolean IsRSTMarker(byte b) {
		byte lsb = (byte) (b & 0x0F);
		byte msb = (byte) (b & 0xF0);
		return 0 <= lsb && lsb <= 7 && msb == 0xD0;
	}

	/**
	 * Return whether the given byte is an application specific marker. It would
	 * be indicated by being 0xEn.
	 * 
	 * @param b
	 *            the byte to test.
	 * @return true if it is an APPn marker.
	 */
	protected static boolean IsAPPMarker(byte b) {
		return (b & 0xF0) == 0xE0;
	}

	/**
	 * Initialize the JPGCoder.
	 * 
	 * @param in
	 *            the InputStream with the JPEG image.
	 * @throws Exception
	 */
	public JPEGCoder(InputStream in) throws Exception {
		super(in);
		buffer = new byte[instream.available()];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) instream.read();
		}
		segment_index = 0;
	}

	/**
	 * Get the next segment in this jpeg file.
	 * 
	 * @return the next segment, which begins with a marker.
	 * @throws IOException
	 *             on read error
	 */
	public byte[] NextSegment() throws IOException {
		int marker = FindMarker(segment_index);
		byte[] retval = Arrays.copyOfRange(buffer, segment_index, marker);
		segment_index = marker;
		return retval;
	}

	/**
	 * Look at the buffer, starting at the location of the previous marker,
	 * until the next marker is found, and return the index where the marker
	 * starts.
	 * 
	 * @param start
	 *            the location of the preceding marker.
	 * 
	 * @return where to find the next marker.
	 * @throws IOException
	 *             on file read error.
	 */
	protected int FindMarker(int start) throws IOException {
		int c = start;
		/* FUCK ELEGANCE!! */
		/* TODO: Add elegance */
		if (c + 2 == buffer.length) {
			return buffer.length;
		}
		short i = 0, j = 0;
		while ((i != 0xFF || j == 0xFF || j == 0) && (c + 2 < buffer.length)) {
			i = (short) (buffer[c + 1] & 0xFF);
			j = (short) (buffer[c + 2] & 0xFF);
			c++;
		}
		return c;
	}
}
