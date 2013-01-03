package com.stegosaurus.huffman;

import java.util.ArrayList;

import com.stegosaurus.stegostreams.BitInputStream;
import com.stegosaurus.stegutils.ArrayUtils;
import com.stegosaurus.huffman.trees.TreeNode;


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
