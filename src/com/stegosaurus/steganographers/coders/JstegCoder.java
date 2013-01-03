package com.stegosaurus.steganographers.coders;

import java.io.InputStream;

/**
 * A JPEG Coder which specifically implements the Jsteg algorithm.
 * @author joe
 *
 */
public abstract class JstegCoder extends JPEGCoder {

	public JstegCoder(InputStream in) throws Exception {
		super(in);
	}

}
