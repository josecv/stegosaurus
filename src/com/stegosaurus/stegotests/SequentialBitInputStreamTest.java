package com.stegosaurus.stegotests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import com.stegosaurus.stegostreams.SequentialBitInputStream;

public class SequentialBitInputStreamTest {

	@Test
	public void testRead() {
		byte[] arg = { 0b01011101 };
		SequentialBitInputStream st = new SequentialBitInputStream(arg);
		byte[] expected = { 0, 1, 0, 1, 1, 1, 0, 1 };
		byte[] retval = new byte[8];
		try {
			for (int i = 0; i < retval.length; i++) {
				retval[i] = (byte) st.read();
			}
			st.close();
		} catch (IOException e) {
			fail("Unexpected exception: " + e);
		}
		assertTrue("Wrong return value from read",
				Arrays.equals(retval, expected));
	}

}
