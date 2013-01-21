package com.stegosaurus.steganographers.coders;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.steganographers.coders.Hider;


/**
 * A JPEG hider which makes use of the Jsteg algorithm.
 * 
 * @author joe
 * 
 */
public class JstegHider extends JPEGCoder implements Hider {

	/**
	 * An enum declaring an 8x8 block as either a Luma or Chroma component.
	 * 
	 * @author joe
	 * 
	 */
	private enum BlockType {
		L, CB, CR
	};

	/**
	 * The 8x8 block being dealt with right now.
	 */
	private byte[] current_block;

	/**
	 * The type of the 8x8 block currently being dealt with.
	 */
	private BlockType current_block_type;

	/**
	 * The index in the working set that we are dealing with right now.
	 */
	private int working_index;

	/**
	 * The number of blocks of the current block type that we have seen in a
	 * row.
	 */
	private int blocks_of_type;

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
	 * Given a JPEG segment, remove the 0x00 bytes that follow any legitimate
	 * 0xFF bytes, so that the data might be dealt with.
	 * 
	 * @param segment
	 *            the segment in question
	 * @return the segment, with 0x00 bytes removed.
	 */
	private static byte[] Unescape(byte[] segment) {
		ArrayList<Byte> s = new ArrayList<Byte>(Arrays.asList(ArrayUtils
				.toObject(segment)));
		int i;
		for (i = s.size() - 1; i >= 0; i--) {
			if (s.get(i) == 0 && s.get(i) == 0xFF) {
				s.remove(i);
			}
		}
		return ArrayUtils.toPrimitive(s.toArray(new Byte[s.size()]));
	}

	/**
	 * Load the next 8x8 block from the working set.
	 * 
	 * @return
	 */
	private JstegHider LoadBlock() {
		current_block = Arrays.copyOfRange(working_data, working_index,
				working_index + 64);
		working_index += 64;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void Hide(BitInputStream datastream, int count) throws Exception {
		if (working_data.length == 0) {
			LoadWorkingSet();
			working_data = Unescape(working_data);
			current_block_type = BlockType.L;
			blocks_of_type = 0;
			working_index = 0; 
			LoadBlock();
		}
	}

}
