package com.stegosaurus.huffman.trees;

import java.util.Map;
import java.util.TreeMap;

import com.stegosaurus.huffman.HuffmanCode;

/**
 * The root of a huffman binary tree.
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
  public boolean isLeaf() {
    return right == null && left == null && data != null;
  }

  /**
   * Set the data on this leaf.
   * 
   * @param data the data to be carried by the leaf.
   */
  public void setData(byte data) {
    if (right != null || left != null) {
      throw new IllegalStateException("Setting data on a non-leaf node.");
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
  public void growLeft() {
    if (data != null) {
      throw new IllegalStateException(
          "Violating leaf-ness of node with data: " + data);
    } else {
      left = new TreeNode();
    }
  }

  /**
   * Add a right child to this node.
   */
  public void growRight() {
    if (data != null) {
      throw new IllegalStateException(
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
  public void insertWithDepth(byte data, int depth) {
    insertWithDepthInner(data, depth);
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
   * @see insertWithDepth
   */
  private boolean insertWithDepthInner(byte data, int depth) {
    /* TODO: This method is on crack. Refactor it later */
    if (depth == 1) {
      if (this.left() == null) {
        this.growLeft();
        this.left().setData(data);
        return true;
      } else if (this.right() == null) {
        this.growRight();
        this.right().setData(data);
        return true;
      } else {
        return false;
      }
    } else {
      if (this.left() == null) {
        this.growLeft();
        /*
         * It will, of course, return true, since we managed to find a
         * new way in, as it were.
         */
        return this.left.insertWithDepthInner(data, depth - 1);
      } else if (!this.left().isLeaf()
          && this.left.insertWithDepthInner(data, depth - 1)) {
        return true;
      } else if (this.right() == null) {
        this.growRight();
        return right.insertWithDepthInner(data, depth - 1);
      } else if (!this.right().isLeaf()) {
        return right.insertWithDepthInner(data, depth - 1);
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
  public Map<Byte, HuffmanCode> asMap() {
    Map<Byte, HuffmanCode> retval = new TreeMap<Byte, HuffmanCode>();
    if (left != null) {
      retval.putAll(left.asMap(0, 1));
    }
    if (right != null) {
      retval.putAll(right.asMap(1, 1));
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
  private Map<Byte, HuffmanCode> asMap(int prefix, int len) {
    Map<Byte, HuffmanCode> retval = new TreeMap<Byte, HuffmanCode>();
    if (isLeaf()) {
      HuffmanCode code = new HuffmanCode(data, prefix, len);
      retval.put(data, code);
    } else {
      /*
       * For a movement to the left, append a 0. For one to the right
       * append a 1.
       */
      if (left != null) {
        retval.putAll(left.asMap((prefix << 1), len + 1));
      }
      if (right != null) {
        retval.putAll(right.asMap(((prefix << 1) | 1), len + 1));
      }
    }
    return retval;
  }

  /**
   * Return whether o is equal to this tree.
   * 
   * @return true if o is a tree and its children are equal to this tree's
   *         children. If they're leafs, return true if they have the same
   *         data.
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof TreeNode) {
      TreeNode tree = (TreeNode) o;
      if (this.isLeaf()) {
        if (tree.isLeaf()) {
          return tree.data == this.data;
        } else {
          return false;
        }
      } else {
        boolean l = false, r = false;
        if (left != null && tree.left != null) {
          l = left.equals(tree.left);
        } else if (left == null && tree.left == null) {
          l = true;
        }
        if (right != null && tree.right != null) {
          r = right.equals(tree.right);
        } else if (right == null && tree.right == null) {
          r = true;
        }
        return r && l;
      }
    } else {
      return false;
    }
  }
  
  /**
   * Get a hash code for this tree. It is a function of the hash codes of any
   * children, or if the tree is a leaf, it is simply the data.
   */
  @Override
  public int hashCode() {
    if(this.isLeaf()) {
      return this.data;
    }
    int l = left == null ? 0 : left.hashCode();
    int r = right == null ? 0 : right.hashCode();
    return 1 + l + (r * 31);
  }
}
