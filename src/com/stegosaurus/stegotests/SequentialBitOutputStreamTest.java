package com.stegosaurus.stegotests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.stegosaurus.stegostreams.SequentialBitOutputStream;

public class SequentialBitOutputStreamTest {

	@Test
	public void testWriteInt() {
		SequentialBitOutputStream s = new SequentialBitOutputStream();
		s.write(0);
		s.write(0);
		s.write(1);
		s.write(1);
		s.write(0);
		s.write(0);
		s.write(0);
		s.write(1);
		s.write(0);
		s.write(1);
		s.write(1);
		byte[] data = { 0b00110001, 0b01100000 };
		assertTrue("Wrong return value for data", Arrays.equals(data, s.data()));
		try {
			s.close();
		} catch (IOException io) {
			io.printStackTrace();
			fail("Unexpected exception " + io.getMessage());
		}
	}

}
