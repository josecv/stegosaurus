package com.stegosaurus.huffman;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import com.stegosaurus.huffman.trees.TreeNode;
import com.stegosaurus.stegostreams.BitInputStream;


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
   * @param in
   *            the BitInputStream to read from.
   * @return the huffman decoded bytes.
   */
  public byte[] decode(BitInputStream in) {
    ArrayList<Byte> retval = new ArrayList<Byte>();
    /*
     * For every set of bits, the first leaf encountered contains the byte
     * that they stand for.
     */
    while (in.available() > 0) {
      retval.add(decodeNext(in));
    }
    return ArrayUtils.toPrimitive(retval.toArray(new Byte[retval.size()]));
  }

  /**
   * Decode the next byte in the BitInputStream given, and return it.
   * 
   * @param in
   *            the bit input stream with the encoded data.
   * @return the next decoded byte.
   */
  public byte decodeNext(BitInputStream in) {
    TreeNode n = root;
    while(n != null && !n.isLeaf() && in.available() > 0) {
      if (in.read() == 0) {
        n = n.left();
      } else {
        n = n.right();
      }
    }
    return n.data();
  }

  /**
   * Return whether this decoder equals the object given.
   * 
   * @return true if o is a HuffmanDecoder and it uses the exact same code as
   *         this one.
   */
  public boolean equals(Object o) {
    if (o instanceof HuffmanDecoder) {
      HuffmanDecoder h = (HuffmanDecoder) o;
      return root.equals(h.root);
    } else {
      return false;
    }
  }

  /**
    * Return a hash code for this decoder. Note that since equality is defined
    * as equality of codes, this merely hashes the code in use.
    *
    * @return a hash code for this decoder
    */
  public int hashCode() {
    return root.hashCode();
  }

  @Override
  public String toString() {
    return "Decoder for tree: " + root.toString();
  }
}
