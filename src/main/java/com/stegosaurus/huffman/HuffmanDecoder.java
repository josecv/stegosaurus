package com.stegosaurus.huffman;

import java.util.ArrayList;

import com.stegosaurus.huffman.trees.TreeNode;
import com.stegosaurus.stegostreams.BitInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

  private Logger log = LoggerFactory.getLogger(HuffmanDecoder.class);

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
    String logged = "";
    while(n != null && !n.isLeaf() && in.available() > 0) {
      int read = in.read();
      logged += read;
      if (read == 0) {
        n = n.left();
      } else {
        n = n.right();
      }
    }
    log.info("Decoding " + logged);
    byte data = n.data();
    log.info("Decoded to " + data);
    return data;
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