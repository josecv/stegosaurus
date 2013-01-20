package com.stegosaurus.stegotests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.stegosaurus.huffman.HuffmanDecoder;
import com.stegosaurus.huffman.JPEGHuffmanDecoder;
import com.stegosaurus.steganographers.coders.JPEGCoder;
import com.stegosaurus.stegotests.testutils.ByteStream;

/*
 * AHOY MATEY!! Bad testing practices at large here.
 * TODO: Make me less... bad.
 */

public class JPEGCoderTest {

	/**
	 * Monstruous table of data. Subset of a JPEG file, with casts wherever the
	 * compiler complained about overflow.
	 */
	private static final byte[] data = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF,
			(byte) 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05,
			0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D,
			0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A,
			0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20,
			0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31,
			0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E,
			0x33, 0x34, 0x32, (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x01, 0x09,
			0x09, 0x09, 0x0C, 0x0B, 0x0C, 0x18, 0x0D, 0x0D, 0x18, 0x32, 0x21,
			0x1C, 0x21, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
			0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
			0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
			0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
			0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, (byte) 0xFF,
			(byte) 0xC0, 0x00, 0x11, 0x08, 0x00, 0x30, 0x00, 0x3C, 0x03, 0x01,
			0x21, 0x00, 0x02, 0x11, 0x01, 0x03, 0x11, 0x01, (byte) 0xFF,
			(byte) 0xC4, 0x00, 0x1F, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01,
			0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
			(byte) 0xFF, (byte) 0xDA, 0x00, 0x0C, 0x03, 0x01, 0x00, 0x02, 0x11,
			0x03, 0x11, 0x00, 0x3F, 0x00, (byte) 0xF6, 0x18, (byte) 0xD8, 0x37,
			(byte) 0xDD, 0x7E, (byte) 0x9D, (byte) 0xF3, (byte) 0x9C,
			(byte) 0xD5, (byte) 0xA0, (byte) 0xE0, 0x1E, (byte) 0xB9, 0x35,
			0x77, 0x66, 0x2A, 0x23, (byte) 0xD2, 0x40, 0x39, 0x20, (byte) 0xE4,
			0x76, 0x15, (byte) 0x8B, (byte) 0xE2, (byte) 0x8D, 0x75, 0x34,
			0x0F, 0x0E, (byte) 0xDE, (byte) 0xEA, 0x7B, 0x72, (byte) 0xF0,
			0x46, 0x59, 0x37, 0x29, 0x23, 0x71, (byte) 0xE0, 0x67, 0x1C,
			(byte) 0xFF, 0x00, 0x67, 0x1D, (byte) 0xA8, (byte) 0xBE,
			(byte) 0x83, (byte) 0xB1, (byte) 0xF3, 0x1E, (byte) 0xA9,
			(byte) 0xE3, 0x5D, 0x5F, 0x53, (byte) 0x9A, 0x56, (byte) 0xBA,
			(byte) 0xBF, (byte) 0xBC, 0x76, 0x76, (byte) 0xC9, 0x06, 0x63,
			(byte) 0xB4, 0x75, (byte) 0xE8, (byte) 0xA3, (byte) 0x81,
			(byte) 0xCF, (byte) 0xFF, (byte) 0xD9 };

	/**
	 * The Huffman table included in the data table.
	 */
	private static final byte[] DHT = { (byte) 0xFF, (byte) 0xC4, 0x00, 0x1F,
			0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04,
			0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B };

	/**
	 * The frame included in the data table.
	 */
	private static final byte[] SOF0 = { (byte) 0xFF, (byte) 0xC0, 0x00, 0x11,
			0x08, 0x00, 0x30, 0x00, 0x3C, 0x03, 0x01, 0x21, 0x00, 0x02, 0x11,
			0x01, 0x03, 0x11, 0x01 };

	/**
	 * The image scan included in the data table.
	 */
	private static final byte[] SOS = { (byte) 0xFF, (byte) 0xDA, 0x00, 0x0C,
			0x03, 0x01, 0x00, 0x02, 0x11, 0x03, 0x11, 0x00, 0x3F, 0x00,
			(byte) 0xF6, 0x18, (byte) 0xD8, 0x37, (byte) 0xDD, 0x7E,
			(byte) 0x9D, (byte) 0xF3, (byte) 0x9C, (byte) 0xD5, (byte) 0xA0,
			(byte) 0xE0, 0x1E, (byte) 0xB9, 0x35, 0x77, 0x66, 0x2A, 0x23,
			(byte) 0xD2, 0x40, 0x39, 0x20, (byte) 0xE4, 0x76, 0x15,
			(byte) 0x8B, (byte) 0xE2, (byte) 0x8D, 0x75, 0x34, 0x0F, 0x0E,
			(byte) 0xDE, (byte) 0xEA, 0x7B, 0x72, (byte) 0xF0, 0x46, 0x59,
			0x37, 0x29, 0x23, 0x71, (byte) 0xE0, 0x67, 0x1C, (byte) 0xFF, 0x00,
			0x67, 0x1D, (byte) 0xA8, (byte) 0xBE, (byte) 0x83, (byte) 0xB1,
			(byte) 0xF3, 0x1E, (byte) 0xA9, (byte) 0xE3, 0x5D, 0x5F, 0x53,
			(byte) 0x9A, 0x56, (byte) 0xBA, (byte) 0xBF, (byte) 0xBC, 0x76,
			0x76, (byte) 0xC9, 0x06, 0x63, (byte) 0xB4, 0x75, (byte) 0xE8,
			(byte) 0xA3, (byte) 0x81, (byte) 0xCF };

	/**
	 * A tiny concrete class to test the JPEGCoder easily.
	 * 
	 * @author joe
	 * 
	 */
	private class TestableCoder extends JPEGCoder {

		public TestableCoder(InputStream in) throws Exception {
			super(in);
		}

		@Override
		public byte[] close() throws Exception {
			return null;
		}

		@Override
		protected void LoadWorkingSet() throws IOException {
			super.LoadWorkingSet();
		}

		public byte[] working_data() {
			return working_data;
		}

		public byte[] data() {
			return data;
		}

		public Map<Integer, HuffmanDecoder> decoders() {
			return decoders;
		}

		public byte[][] subsampling() {
			return subsampling;
		}
	}

	@Test
	public void testNextSegment() {
		ByteStream stream = new ByteStream(data);
		byte[] retval = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF };
		try {
			JPEGCoder coder = new TestableCoder(stream);
			retval = coder.NextSegment();
			byte[] SOI_EXPECTED = { (byte) 0xFF, (byte) 0xD8 };
			assertTrue("SOI not returned right",
					Arrays.equals(retval, SOI_EXPECTED));
			retval = coder.NextSegment();
			byte[] DQT_EXPECTED = { (byte) 0xFF, (byte) 0xDB, 0x00, 0x43, 0x00,
					0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07,
					0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B,
					0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E,
					0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22,
					0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31,
					0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C,
					0x2E, 0x33, 0x34, 0x32 };
			assertTrue("DQT not returned right",
					Arrays.equals(retval, DQT_EXPECTED));
			retval = coder.NextSegment();
			byte[] DQT2_EXPECTED = { (byte) 0xFF, (byte) 0xDB, 0x00, 0x43,
					0x01, 0x09, 0x09, 0x09, 0x0C, 0x0B, 0x0C, 0x18, 0x0D, 0x0D,
					0x18, 0x32, 0x21, 0x1C, 0x21, 0x32, 0x32, 0x32, 0x32, 0x32,
					0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
					0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
					0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
					0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32,
					0x32, 0x32, 0x32, 0x32, 0x32 };
			assertTrue("DQT not returned right",
					Arrays.equals(retval, DQT2_EXPECTED));
			retval = coder.NextSegment();
			assertTrue("SOF0 Not returned right", Arrays.equals(retval, SOF0));
			retval = coder.NextSegment();
			assertTrue("DHT not returned right", Arrays.equals(retval, DHT));
			retval = coder.NextSegment();
			assertTrue("SOS not returned right", Arrays.equals(retval, SOS));
			retval = coder.NextSegment();
			byte[] EOI_EXPECTED = { (byte) 0xFF, (byte) 0xD9 };
			assertTrue("EOI not returned right",
					Arrays.equals(retval, EOI_EXPECTED));
		} catch (AssertionError ae) {
			for (byte b : retval) {
				System.out.println(b);
			}
			fail(ae.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception came out of nowhere: " + e.getMessage());
		}
	}

	@Test
	public void testLoadWorkingSet() {
		try {
			ByteStream b = new ByteStream(data);
			TestableCoder c = new TestableCoder(b);
			c.LoadWorkingSet();
			assertTrue("Working data getting loaded badly",
					Arrays.equals(c.working_data(), SOS));
			HuffmanDecoder huff = new JPEGHuffmanDecoder(Arrays.copyOfRange(
					DHT, 5, DHT.length));
			assertFalse("Decoder not being emplaced",
					c.decoders().get(0) == null);
			assertTrue("Huffman decoder being created badly",
					huff.equals(c.decoders().get(0)));
			byte[][] subsamp = new byte[3][2];
			subsamp[0][0] = 2;
			subsamp[0][1] = 1;
			subsamp[1][0] = 1;
			subsamp[1][1] = 1;
			subsamp[2][0] = 1;
			subsamp[2][1] = 1;
			assertTrue("Subsampling array being loaded badly",
					Arrays.deepEquals(subsamp, c.subsampling()));
			byte[] expected_data = Arrays.copyOfRange(data, 0, 192);
			assertTrue("Data being loaded badly",
					Arrays.equals(expected_data, c.data()));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception " + e.getMessage());
		}
	}
}
