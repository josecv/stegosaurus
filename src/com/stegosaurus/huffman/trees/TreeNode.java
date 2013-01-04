package com.stegosaurus.huffman.trees;

import java.util.Map;
import java.util.TreeMap;

import com.stegosaurus.huffman.HuffmanCode;

/**
 * The root of a binary tree.
 * 
 * @author joe
 * 
 */
public class TreeNode {

	/**
	 * Left child.
	 */
	private TreeNode left;
	/**
	 * Right child.
	 */
	private TreeNode right;
	/**
	 * Data (only if this is a leaf will there be any data).
	 */
	private Byte data;

	/**
	 * Construct the node.
	 */
	public TreeNode() {
		data = null;
		right = left = null;
	}

	/**
	 * Is this node a leaf (it's a leaf if it has no children).
	 * 
	 * @return true if this node is a leaf.
	 */
	public boolean IsLeaf() {
		return right == null && left == null && data != null;
	}

	/**
	 * Set the data on this leaf.
	 * 
	 * @param data
	 *            the data to be carried by the leaf.
	 */
	public void SetData(byte data) {
		if (right != null || left != null) {
			throw new RuntimeException("Setting data on a non-leaf node.");
		} else {
			this.data = data;
		}
	}

	/**
	 * Get the left child of this node.
	 * 
	 * @return the left child of this node.
	 */
	public TreeNode left() {
		return left;
	}

	/**
	 * Get the right child of this node.
	 * 
	 * @return the right child of this node.
	 */
	public TreeNode right() {
		return right;
	}

	/**
	 * Get this leaf's data.
	 * 
	 * @return the byte data associated with this leaf.
	 */
	public byte data() {
		return data;
	}

	/**
	 * Add a left child to this node.
	 */
	public void GrowLeft() {
		if (data != null) {
			throw new RuntimeException(
					"Violating leaf-ness of node with data: " + data);
		} else {
			left = new TreeNode();
		}
	}

	/**
	 * Add a right child to this node.
	 */
	public void GrowRight() {
		if (data != null) {
			throw new RuntimeException(
					"Violating leaf-ness of node with data: " + data);
		} else {
			right = new TreeNode();
		}
	}

	/**
	 * Insert the data given into a new leaf depth nodes down from the root, on
	 * the tree rooted on this node. Prefer going left.
	 * 
	 * @param data
	 *            the data to insert on the leaf.
	 * @param depth
	 *            the number of paths separating this node from the leaf with
	 *            data.
	 */
	public void InsertWithDepth(byte data, int depth) {
		InsertWithDepthInner(data, depth);
	}

	/**
	 * As with InsertWithDepth, but does the actual inserting. Called
	 * recursively.
	 * 
	 * @param data
	 *            the data to insert.
	 * @param depth
	 *            the depth of the leaf.
	 * @return whether an insertion was possible.
	 * @see InsertWithDepth
	 */
	private boolean InsertWithDepthInner(byte data, int depth) {
		/* TODO: This method is on crack. Refactor it later */
		if (depth == 1) {
			if (this.left() == null) {
				this.GrowLeft();
				this.left().SetData(data);
				return true;
			} else if (this.right() == null) {
				this.GrowRight();
				this.right().SetData(data);
				return true;
			} else {
				return false;
			}
		} else {
			if (this.left() == null) {
				this.GrowLeft();
				/*
				 * It will, of course, return true, since we managed to find a
				 * new way in, as it were.
				 */
				return this.left.InsertWithDepthInner(data, depth - 1);
			} else if (!this.left().IsLeaf()
					&& this.left.InsertWithDepthInner(data, depth - 1)) {
				return true;
			} else if (this.right() == null) {
				this.GrowRight();
				return right.InsertWithDepthInner(data, depth - 1);
			} else if (!this.right().IsLeaf()) {
				return right.InsertWithDepthInner(data, depth - 1);
			} else {
				return false;
			}
		}
	}

	/* TODO: Factor out the repetition here */

	/**
	 * Produce a map representation of this tree going from the values encoded
	 * to a HuffmanCode structure.
	 * 
	 * @return a Map mapping from the encoded values to the corresponding
	 *         HuffmanCodes.
	 */
	public Map<Byte, HuffmanCode> AsMap() {
		Map<Byte, HuffmanCode> retval = new TreeMap<>();
		if (left != null) {
			retval.putAll(left.AsMap(0, 1));
		}
		if (right != null) {
			retval.putAll(right.AsMap(1, 1));
		}
		return retval;
	}

	/**
	 * Same as with the other AsMap, but prepend prefix to any codes
	 * encountered. Thus, if, say, the code for 0xAD is 011, and prefix is 101,
	 * the code becomes 101011.
	 * 
	 * @param prefix
	 *            a prefix to prepend to all encountered codes
	 * @param len
	 *            the length of the prefix.
	 * @return a Map going from the encoded values to the corresponding
	 *         HuffmanCodes.
	 */
	private Map<Byte, HuffmanCode> AsMap(int prefix, int len) {
		Map<Byte, HuffmanCode> retval = new TreeMap<>();
		if (IsLeaf()) {
			HuffmanCode code = new HuffmanCode(data, prefix, len);
			retval.put(data, code);
		} else {
			/*
			 * For a movement to the left, append a 0. For one to the right
			 * append a 1.
			 */
			if (left != null) {
				retval.putAll(left.AsMap((prefix << 1), len + 1));
			}
			if (right != null) {
				retval.putAll(right.AsMap(((prefix << 1) | 1), len + 1));
			}
		}
		return retval;
	}

}
