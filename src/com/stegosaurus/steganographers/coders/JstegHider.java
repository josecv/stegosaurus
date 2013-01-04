package com.stegosaurus.steganographers.coders;

import java.io.InputStream;

import com.stegosaurus.stegostreams.BitInputStream;

/**
 * A JPEG hider which makes use of the Jsteg algorithm.
 * 
 * @author joe
 * 
 */
public class JstegHider extends JPEGCoder implements Hider {

	public JstegHider(InputStream in) throws Exception {
		super(in);
		// TODO Auto-generated constructor stub
	}

	@Override
	public byte[] close() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void Hide(BitInputStream datastream, int count) throws Exception {
		// TODO Auto-generated method stub

	}

}
