package stegotests;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;
import steganographers.coders.JPEGCoder;
import stegotests.testutils.ByteStream;

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
			(byte) 0xFF, (byte) 0xD9 };

	private class TestableCoder extends JPEGCoder {

		public TestableCoder(InputStream in) throws Exception {
			super(in);
		}

		@Override
		public byte[] close() throws Exception {
			return null;
		}

	}

	@Test
	public void testNextSegment() {
		ByteStream stream = new ByteStream(data);
		byte[] retval = { (byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF };
		try {
			TestableCoder coder = new TestableCoder(stream);
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
			byte[] SOF0_EXPECTED = { (byte) 0xFF, (byte) 0xC0, 0x00, 0x11,
					0x08, 0x00, 0x30, 0x00, 0x3C, 0x03, 0x01, 0x21, 0x00, 0x02,
					0x11, 0x01, 0x03, 0x11, 0x01 };
			assertTrue("SOF0 Not returned right",
					Arrays.equals(retval, SOF0_EXPECTED));
			retval = coder.NextSegment();
			byte[] DHT_EXPECTED = { (byte) 0xFF, (byte) 0xC4, 0x00, 0x1F, 0x00,
					0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03,
					0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B };
			assertTrue("DHT not returned right",
					Arrays.equals(retval, DHT_EXPECTED));
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
}
