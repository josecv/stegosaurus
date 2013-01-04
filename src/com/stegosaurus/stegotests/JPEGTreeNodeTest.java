package com.stegosaurus.stegotests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.stegosaurus.huffman.HuffmanCode;
import com.stegosaurus.huffman.trees.JPEGTreeNode;
import com.stegosaurus.huffman.trees.TreeNode;

public class JPEGTreeNodeTest {

	/**
	 * The Huffman table we'll be using to test. Very unusual table, but still,
	 * taken from an actual file.
	 */
	protected final static byte[] table = { 00, 01, 05, 01, 01, 01, 01, 01, 01,
			00, 00, 00, 00, 00, 00, 00, 00, 01, 02, 03, 04, 05, 06, 07, 0x08,
			0x09, 0x0A, 0x0B };

	/**
	 * The same table, but sorted by the amount of bits required to encode each
	 * member.
	 */
	private final static byte[][] len_sorted_table = { {}, { 0 },
			{ 1, 2, 3, 4, 5 }, { 6 }, { 7 }, { 0x08 }, { 0x09 }, { 0x0A },
			{ 0x0B }, {}, {}, {}, {}, {}, {}, {} };

	@Test
	public void testSortTableByLength() {
		byte[][] retval = JPEGTreeNode.SortTableByLength(table);
		assertTrue(retval.length == 16);
		try {
			assertTrue(Arrays.deepEquals(retval, len_sorted_table));
		} catch (AssertionError ae) {
			for (byte[] b : retval) {
				System.out.print("[");
				for (byte bb : b) {
					System.out.print(bb + ",");
				}
				System.out.println("]");
			}
			fail("Expected value of sort by length differs from actual value.");
		}
	}

	@Test
	public void testCTOR() {
		JPEGTreeNode tree = new JPEGTreeNode(table);
		/* Depth 2 */
		assertTrue("0 inserted in wrong position",
				tree.left().left().data() == 0);
		/* 3 */
		assertTrue("1 inserted in wrong position", tree.left().right().left()
				.data() == 1);
		assertTrue("2 inserted in wrong position", tree.left().right().right()
				.data() == 2);
		assertTrue("3 inserted in wrong position", tree.right().left().left()
				.data() == 3);
		assertTrue("4 inserted in wrong position", tree.right().left().right()
				.data() == 4);
		assertTrue("5 inserted in wrong position", tree.right().right().left()
				.data() == 5);
		/* 4 */
		TreeNode depth3root = tree.right().right().right();
		assertTrue("6 inserted in wrong position",
				depth3root.left().data() == 6);
		/* 5 */
		assertTrue("7 inserted in wrong position", depth3root.right().left()
				.data() == 7);
		/* 6 */
		assertTrue("8 inserted in wrong position", depth3root.right().right()
				.left().data() == 8);
		/* 7 */
		assertTrue("9 inserted in wrong position", depth3root.right().right()
				.right().left().data() == 9);
		/* 8 */
		assertTrue("A inserted in wrong position", depth3root.right().right()
				.right().right().left().data() == 0xA);
		/* 9 */
		assertTrue("B inserted in wrong position", depth3root.right().right()
				.right().right().right().left().data() == 0xB);
	}

	@Test
	public void testInsertWithDepth() {
		TreeNode tree = new TreeNode();
		tree.InsertWithDepth((byte) 2, 1);
		assertTrue("InsertWithDepth inserting in wrong direction",
				tree.right() == null);
		assertTrue("InsertWithDepth inserting wrong data: "
				+ tree.left().data(), tree.left().data() == 2);
		tree.InsertWithDepth((byte) 3, 3);
		tree.InsertWithDepth((byte) 4, 3);
		tree.InsertWithDepth((byte) 5, 3);
		assertTrue("Future insertions violate the leaf 2.", tree.left()
				.IsLeaf());
		assertTrue("Wrong Insertion of value 3", tree.right().left().left()
				.data() == 3);
		assertTrue("Wrong Insertion of value 4", tree.right().left().right()
				.data() == 4);
		assertTrue("Wrong Insertion of value 5", tree.right().right().left()
				.data() == 5);
		assertTrue("Phantom node created by insertion", tree.right().right()
				.right() == null);
		tree.InsertWithDepth((byte) 6, 4);
		tree.InsertWithDepth((byte) 7, 4);
		assertTrue("Future insertions violate the leaf 3", tree.right().left()
				.left().IsLeaf());
		assertTrue("Future insertions violate the leaf 4", tree.right().left()
				.right().IsLeaf());
		assertTrue("Future insertions violate the leaf 5", tree.right().right()
				.left().IsLeaf());
		assertTrue("Wrong Insertion of value 6", tree.right().right().right()
				.left().data() == 6);
		assertTrue("Wrong Insertion of value 7", tree.right().right().right()
				.right().data() == 7);
	}

	@Test
	public void testAsMap() {
		TreeNode tree = new JPEGTreeNode(table);
		Map<Byte, HuffmanCode> retval = tree.AsMap();
		TreeMap<Byte, HuffmanCode> expected = new TreeMap<>();
		expected.put((byte) 0, new HuffmanCode(0, 0, 2));
		expected.put((byte) 1, new HuffmanCode(1, 0b010, 3));
		expected.put((byte) 2, new HuffmanCode(2, 0b011, 3));
		expected.put((byte) 3, new HuffmanCode(3, 0b100, 3));
		expected.put((byte) 4, new HuffmanCode(4, 0b101, 3));
		expected.put((byte) 5, new HuffmanCode(5, 0b110, 3));
		expected.put((byte) 6, new HuffmanCode(6, 0b1110, 4));
		expected.put((byte) 7, new HuffmanCode(7, 0b11110, 5));
		expected.put((byte) 8, new HuffmanCode(8, 0b111110, 6));
		expected.put((byte) 9, new HuffmanCode(9, 0b1111110, 7));
		expected.put((byte) 0xA, new HuffmanCode(0xA, 0b11111110, 8));
		expected.put((byte) 0xB, new HuffmanCode(0xB, 0b111111110, 9));
		for (Byte key : expected.keySet()) {
			assertTrue(
					"Wrong value for key " + key + ", got: " + retval.get(key)
							+ " expected: " + expected.get(key),
					expected.get(key).equals(retval.get(key)));
		}
	}

}
