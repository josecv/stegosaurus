package com.stegosaurus.steganographers.coders;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegostreams.SequentialBitInputStream;
import com.stegosaurus.steganographers.coders.Hider;

/**
 * A JPEG hider which makes use of the Jsteg algorithm.
 * 
 * @author joe
 * 
 */
public class JstegHider extends JPEGCoder implements Hider {

	/**
	 * The 8x8 block being dealt with right now, unencoded.
	 */
	private byte[] current_block;

	/**
	 * The number of blocks of the current block type that we have seen in a
	 * row.
	 */
	private int blocks_of_type;

	/**
	 * The number of the component we are dealing with. EG if this is a
	 * "typical" coloured JPEG image, and we are dealing with a Luma macroblock,
	 * this is 0.
	 */
	private int component;

	/**
	 * The number of components in the current scan. Usually 3.
	 */
	private int numberOfComponents;

	/**
	 * The huffman table for each component. The indices are the component ids -
	 * 1 (eg 0 for Luma) and the values are the name of the Huffman table to
	 * use.
	 */
	private byte[] huffmanTableId;

	/**
	 * An entire scan (at least before the next reset marker), as an input stream.
	 */
	private BitInputStream scan;
	
	/**
	 * {@inheritDoc}
	 */
	public JstegHider(InputStream in) throws Exception {
		super(in);
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] close() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Load the next 8x8 block from the working set.
	 * 
	 * @return
	 */
	private JPEGCoder LoadBlock() {
		return this;
	}

	/**
	 * {@inheritDoc} Also unescapes the working data, and loads in the component
	 * information
	 * 
	 * @TODO: Consider pulling up responsibility for some of the stuff here
	 */
	@Override
	protected JPEGCoder LoadWorkingSet() {
		working_data = Unescape(working_data);
		blocks_of_type = 0;
		numberOfComponents = working_data[4];
		int i;
		for (i = 0; i < numberOfComponents * 2; i += 2) {
			byte id = working_data[i];
			byte table = working_data[i + 1];
			huffmanTableId[id - 1] = table;
		}
		data = ArrayUtils.addAll(data, Arrays.copyOf(working_data, i + 5));
		return LoadBlock();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void Hide(BitInputStream datastream, int count) throws Exception {
		if (working_data.length == 0) {
			LoadWorkingSet();
		}
	}

}
