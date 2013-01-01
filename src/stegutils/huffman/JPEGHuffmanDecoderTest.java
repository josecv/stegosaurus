package stegutils.huffman;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class JPEGHuffmanDecoderTest {

	/**
	 * The Huffman table we'll be using to test. Very unusual table, but still, taken
	 * from an actual file.
	 */
	private final static byte[] table = {
		00, 01, 05, 01, 01, 01, 01, 01, 01, 00, 00, 00, 00, 00, 00, 00,
		00, 01, 02, 03, 04, 05, 06, 07, 0x08, 0x09, 0x0A, 0x0B};
	
	private final static byte[][] len_sorted_table = {{}, {0}, {1, 2, 3, 4, 5}, {6}, {7}, {0x08},
		{0x09}, {0x0A}, {0x0B}, {}, {}, {}, {}, {}, {}, {}};
	
	@Test
	public void testSortTableByLength() {
		byte[][] retval = JPEGHuffmanDecoder.SortTableByLength(table);
		assertTrue(retval.length == 16);
		try {
			assertTrue(Arrays.deepEquals(retval, len_sorted_table));
		} catch (AssertionError ae) {
			for (byte[] b : retval) {
				System.out.print("[");
				for (byte bb: b) {
					System.out.print(bb + ",");
				}
				System.out.println("]");
			}
			fail("Expected value of sort by length differs from actual value.");
		}
	}

	@Test
	public void testBuildTree() {
		HuffmanDecoder.TreeNode tree = JPEGHuffmanDecoder.BuildTree(len_sorted_table);
		/* Depth 2 */
		assertTrue("0 inserted in wrong position", tree.left().left().data() == 0);
		/* 3 */
		assertTrue("1 inserted in wrong position", tree.left().right().left().data() == 1);
		assertTrue("2 inserted in wrong position", tree.left().right().right().data() == 2);
		assertTrue("3 inserted in wrong position", tree.right().left().left().data() == 3);
		assertTrue("4 inserted in wrong position", tree.right().left().right().data() == 4);
		assertTrue("5 inserted in wrong position", tree.right().right().left().data() == 5);
		/* 4 */
		HuffmanDecoder.TreeNode depth3root = tree.right().right().right();
		assertTrue("6 inserted in wrong position", depth3root.left().data() == 6);
		/* 5 */
		assertTrue("7 inserted in wrong position", depth3root.right().left().data() == 7);
		/* 6 */
		assertTrue("8 inserted in wrong position", depth3root.right().right().left().data() == 8);
		/* 7 */
		assertTrue("9 inserted in wrong position", depth3root.right().right().right().left().data() == 9);
		/* 8 */
		assertTrue("A inserted in wrong position", depth3root.right().right().right().right().left().data() == 0xA);
		/* 9 */
		assertTrue("B inserted in wrong position", depth3root.right().right().right().right().right().left().data() == 0xB);
	}

	@Test
	public void testInsertWithDepth() {
		HuffmanDecoder.TreeNode tree = new HuffmanDecoder.TreeNode();
		JPEGHuffmanDecoder.InsertWithDepth((byte) 2, 1, tree);
		assertTrue("InsertWithDepth inserting in wrong direction", tree.right() == null);
		assertTrue("InsertWithDepth inserting wrong data: " + tree.left().data(),
				tree.left().data() == 2);
		JPEGHuffmanDecoder.InsertWithDepth((byte) 3, 3, tree);
		JPEGHuffmanDecoder.InsertWithDepth((byte) 4, 3, tree);
		JPEGHuffmanDecoder.InsertWithDepth((byte) 5, 3, tree);
		assertTrue("Future insertions violate the leaf 2.", tree.left().IsLeaf());
		assertTrue("Wrong Insertion of value 3", tree.right().left().left().data() == 3);
		assertTrue("Wrong Insertion of value 4", tree.right().left().right().data() == 4);
		assertTrue("Wrong Insertion of value 5", tree.right().right().left().data() == 5);
		assertTrue("Phantom node created by insertion", tree.right().right().right() == null);
		JPEGHuffmanDecoder.InsertWithDepth((byte) 6, 4, tree);
		JPEGHuffmanDecoder.InsertWithDepth((byte) 7, 4, tree);
		assertTrue("Future insertions violate the leaf 3", tree.right().left().left().IsLeaf());
		assertTrue("Future insertions violate the leaf 4", tree.right().left().right().IsLeaf());
		assertTrue("Future insertions violate the leaf 5", tree.right().right().left().IsLeaf());
		assertTrue("Wrong Insertion of value 6", tree.right().right().right().left().data() == 6);
		assertTrue("Wrong Insertion of value 7", tree.right().right().right().right().data() == 7);

	}

}
