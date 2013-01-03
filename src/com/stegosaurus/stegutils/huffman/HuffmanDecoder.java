/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stegosaurus.stegutils.huffman;

import java.util.ArrayList;

import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.ArrayUtils;


/**
 * Performs Huffman Decoding on a BitInputStream. Construction of the Tree is
 * delegated to child classes.
 *
 * @author joe
 */
public abstract class HuffmanDecoder {

    /**
     * The root of the huffman tree.
     */
    protected TreeNode root;

    /**
     * Start the decoder.
     */
    public HuffmanDecoder() {
        root = new TreeNode();
    }

    /**
     * A Node in the Huffman Tree.
     */
    protected static class TreeNode {

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
         * @param data the data to be carried by the leaf.
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
         * Get the right Child of this node.
         *
         * @return the right child of this node.
         */
        public TreeNode right() {
            return right;
        }
        
        /**
         * Get this leaf's data.
         * @return the byte data associated with this leaf.
         */
        public byte data() {
        	return data;
        }

        public void GrowLeft() {
            if (data != null) {
                throw new RuntimeException("Violating leaf-ness of node with data: " + data);
            } else {
                left = new TreeNode();
            }
        }

        public void GrowRight() {
            if (data != null) {
                throw new RuntimeException("Violating leaf-ness of node with data: " + data);
            } else {
                right = new TreeNode();
            }
        }
    }

    /**
     * Decode every bit inside the BitInputStream given, and return the
     * resulting bytes.
     *
     * @param in the BitInputStream to read from.
     * @return the huffman decoded bytes.
     */
    public byte[] Decode(BitInputStream in) {
        ArrayList<Byte> retval = new ArrayList<>();
        /*
         * For every set of bits, the first leaf encountered contains the byte
         * that they stand for.
         */
        while (in.available() > 0) {
            TreeNode n = root;
            while (!n.IsLeaf()) {
                if (in.read() == 0) {
                    n = n.left();
                } else {
                    n = n.right();
                }
            }
            retval.add(n.data());
        }
        return ArrayUtils.toPrimitive(retval.toArray(new Byte[retval.size()]));
    }
}
