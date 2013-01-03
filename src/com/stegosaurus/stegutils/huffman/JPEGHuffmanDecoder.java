package com.stegosaurus.stegutils.huffman;

/* TODO: This is written to be easily testable, but it's
 * quite ill-designed. Refactor it without losing that testability.
 */

/**
 * Decodes Huffman encoded data specifically from JPEG files.
 * @author joe
 */
public class JPEGHuffmanDecoder extends HuffmanDecoder {

    /**
     * Construct the JPEGHuffmanDecoder from the Huffman table given.
     *
     * @param table the huffman table. Should be passed after the marker, the
     * size bytes, and the DHT class/id.
     */
    public JPEGHuffmanDecoder(byte[] table) {
        super();
        root = BuildTree(SortTableByLength(table));
    }

    protected static byte[][] SortTableByLength(byte[] table) {
        byte[][] by_length = new byte[16][];
        int i;
        for (i = 0; i < 16; i++) {
            by_length[i] = new byte[table[i]];
        }
        for (byte[] arr : by_length) {
            for (int j = 0; j < arr.length; j++, i++) {
                arr[j] = table[i];
            }
        }
        return by_length;
    }
    
    /**
     * Construct the HuffmanTree from the size-ordered encoded bytes.
     *
     * @param by_length the size-ordered encoded bytes.
     */
    protected static TreeNode BuildTree(byte[][] by_length) {
    	TreeNode root = new TreeNode();
        for (int i = 0; i < by_length.length; i++) {
            for (byte val : by_length[i]) {
                if(!InsertWithDepth(val, i + 1, root)) {
                    throw new RuntimeException("Top-Level call to InsertWithDepth failed.");
                }
            }
        }
        return root;
    }

    /**
     * Recursively insert data into the tree given, with the depth given.
     *
     * @param data the data to insert in a leaf
     * @param depth the depth at which data will be inserted.
     * @param tree the root of the tree to insert the data into.
     * @return whether it was possible to do so (unless calling inside
     * InsertWithDepth, should always be true).
     */
    protected static boolean InsertWithDepth(byte data, int depth, TreeNode tree) {
    	/* TODO: This method is on crack. Refactor it later */
        if (depth == 1) {
            if (tree.left() == null) {
                tree.GrowLeft();
                tree.left().SetData(data);
                return true;
            } else if (tree.right() == null) {
                tree.GrowRight();
                tree.right().SetData(data);
                return true;
            } else {
                return false;
            }
        } else {
            if (tree.left() == null) {
                tree.GrowLeft();
                /*
                 * It will, of course, return true, since we managed to find a
                 * new way in, as it were.
                 */
                return InsertWithDepth(data, depth - 1, tree.left());
            } else if (!tree.left().IsLeaf()
                    && InsertWithDepth(data, depth - 1, tree.left())) {
                return true;
            } else if (tree.right() == null) {
                tree.GrowRight();
                return InsertWithDepth(data, depth - 1, tree.right());
            } else if (!tree.right().IsLeaf()) {
                return InsertWithDepth(data, depth - 1, tree.right());
            } else {
                return false;
            }
        }
    }
}
