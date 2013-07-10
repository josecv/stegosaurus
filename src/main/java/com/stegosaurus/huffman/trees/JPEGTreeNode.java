package com.stegosaurus.huffman.trees;

public class JPEGTreeNode extends TreeNode {
  /**
   * Construct an empty Huffman tree.
   */
  public JPEGTreeNode() {
    super();
  }

  /**
   * Construct a new Huffman tree from the JPEG Huffman table given.
   * 
   * @param table
   *            a Huffman table.
   */
  public JPEGTreeNode(byte[] table) {
    super();
    byte[][] byLength = sortTableByLength(table);
    for (int i = 0; i < byLength.length; i++) {
      for (byte val : byLength[i]) {
        insertWithDepth(val, i + 1);
      }
    }
  }

  /**
   * Given a Huffman table, produce a double array where the first indices are
   * the number of bits required to encode a value, and the arrays they lead
   * to are the values thus encoded.
   * 
   * @param table
   *            the Huffman table.
   * @return the encoded values, in byte arrays sorted by the number of bits
   *         required to encode each.
   */
  public static byte[][] sortTableByLength(byte[] table) {
    byte[][] byLength = new byte[16][];
    int i;
    for (i = 0; i < 16; i++) {
      byLength[i] = new byte[table[i]];
    }
    for (byte[] arr : byLength) {
      System.arraycopy(table, i, arr, 0, arr.length);
      i += arr.length;
    }
    return byLength;
  }
}
