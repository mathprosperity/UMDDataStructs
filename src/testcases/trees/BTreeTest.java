package testcases.trees;
/* Test-cases for B-Trees.*/
import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.Random;
import org.junit.Test;

import datastructures.trees.BTree;
import datastructures.trees.EmptyTreeException;
public class BTreeTest {

	// We will test with both a 3-tree and a 4-tree
	private BTree<Integer> threeTree = new BTree<Integer>(3),
			fourTree = new BTree<Integer>(4);

	@Test
	public void testEmptinessAndSize(){
		assertTrue(threeTree.isEmpty());
		assertEquals(0, threeTree.size());
		assertEquals(0, threeTree.height());
		threeTree.clear();
		assertTrue(threeTree.isEmpty());
		assertEquals(0, threeTree.size());
		assertEquals(0, threeTree.height());
	}

	/* Make a stub tree, see whether it behaves correctly. */
	@Test
	public void testStub(){
		threeTree.add(9);
		try {
			assertEquals(new Integer(9), threeTree.getMin());
			assertEquals(threeTree.getMin(), threeTree.getMax());
		} catch (EmptyTreeException e) {
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		assertEquals(1, threeTree.size());
		assertFalse(threeTree.isEmpty());
		threeTree.clear();
	}

	/* See whether root splits work in an odd-B tree */
	@Test
	public void test3NodeSplit(){
		threeTree.add(9);
		threeTree.add(3);
		threeTree.add(4); // should split the 4-node
		try {
			assertEquals(new Integer(4), threeTree.getRoot());
			assertEquals(new Integer(3), threeTree.getMin());
			assertEquals(new Integer(9), threeTree.getMax());
		} catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		assertEquals(3, threeTree.size());
		threeTree.clear();
	}
	
	@Test
	public void testCopyConstructorAndEquals(){
		Integer[] elsToAdd = {5, 2, 3, 10, 20};
		for(Integer i : elsToAdd)
			threeTree.add(i);
		BTree<Integer> copy = null;
		try {
			copy = new BTree<Integer>(threeTree);
		} catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " from the copy constructor.");
		}
		assertEquals(copy, threeTree);
		threeTree.clear();
	}

	/* See whether root splits work in an even-B tree */
	@Test
	public void test4NodeSplit(){
		fourTree.add(10);
		fourTree.add(11);
		fourTree.add(15);
		fourTree.add(13); // should split the 5-node
		try {
			assertEquals(new Integer(13), fourTree.getRoot());
			assertEquals(new Integer(10), fourTree.getMin());
			assertEquals(new Integer(15), fourTree.getMax());
		} catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		assertEquals(4, fourTree.size());
		fourTree.clear();
	}

	/* Use a 4-tree to see whether right rotations work as intended when a leaf node overflows. */	
	@Test
	public void testRightKeyRotationsAddLeaf(){
		try {
			fourTree.add(10);
			fourTree.add(11);
			fourTree.add(15);
			fourTree.add(13);
			fourTree.add(12); // Left child will be full after this addition
			assertEquals(new Integer(13), fourTree.getRoot());
			fourTree.add(12); // Should trigger a key rotation to the right leaf
			assertEquals(new Integer(12), fourTree.getRoot());
		} catch (EmptyTreeException e) {
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		fourTree.clear();
	}

	/* Use a 3-tree to see whether left rotations work as intended when a leaf node overflows. */
	@Test
	public void testLeftKeyRotationsAddLeaf(){
		threeTree.add(12);
		threeTree.add(20);
		threeTree.add(10);
		threeTree.add(30); // right child will be full after this addition
		threeTree.add(25); // should trigger a left key rotation
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			assertTrue(levelOrder.hasNext());
			Integer[] expectedOrder = new Integer[]{20, 10, 12, 25, 30};
			for(Integer i : expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		threeTree.clear();
	}

	/* Use a 3-tree to determine whether rotations are applied recursively, even in the non-leaf case. */
	@Test
	public void testRecursiveKeyRotations(){
		Integer[] valsToAdd = new Integer[]{12, 20, 10, 30, 11, 40, -6, -5, -6, -5, -8, 10, -10, -15};
		for(Integer i: valsToAdd)
			threeTree.add(i); // last addition should trigger a right-rotation one level above the leaf.
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			Integer[] expectedOrder = new Integer[]{-5, -10, -6, 11, 30, -15, -8, -6, -5, 10, 10, 12, 20, 40};
			for(Integer i: expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		threeTree.clear();
	}

	/* Use a 3-tree to see whether leaf nodes are split, whenever key rotations are not possible. */
	@Test
	public void testLeafSplitting(){
		Integer[] array = new Integer[]{12, 20, 10, 30, 11};
		for(Integer i: array)
			threeTree.add(i);
		threeTree.add(40); // should split the right node into two nodes and make the root a 2-node
		try {
			assertEquals(new Integer(30), threeTree.getRoot());
			assertEquals(new Integer(10), threeTree.getMin());
			assertEquals(new Integer(40), threeTree.getMax());
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			assertTrue(levelOrder.hasNext());
			assertEquals(new Integer(12), levelOrder.next());
			assertEquals(new Integer(30), levelOrder.next());
			assertEquals(new Integer(10), levelOrder.next());
			assertEquals(new Integer(11), levelOrder.next());
			assertEquals(new Integer(20), levelOrder.next());
			assertEquals(new Integer(40), levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch (EmptyTreeException e) {
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		threeTree.clear();
	}

	/* Use a 3-tree to test whether recursive node splitting is taking place as it should. */
	@Test
	public void testInnerNodeSplitting(){
		Integer[] array = new Integer[]{12, 20, 10, 30, 11, 40}; // Previous test's values
		for(Integer i: array)
			threeTree.add(i);
		threeTree.add(-6); // should trigger a right rotation between left and central child nodes
		threeTree.add(-5); // should split both the leaf node and the root.
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			assertTrue(levelOrder.hasNext());
			assertEquals(new Integer(11), levelOrder.next());
			assertEquals(new Integer(-5), levelOrder.next());
			assertEquals(new Integer(30), levelOrder.next());
			assertEquals(new Integer(-6), levelOrder.next());
			assertEquals(new Integer(10), levelOrder.next());
			assertEquals(new Integer(12), levelOrder.next());
			assertEquals(new Integer(20), levelOrder.next());
			assertEquals(new Integer(40), levelOrder.next());
			assertFalse(levelOrder.hasNext());
		} catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		threeTree.clear();
	}

	/* A stress-test with M = 256 and addition of a million random integers */
	@Test
	public void stressTestForAdd(){
		BTree<Integer> realistic = new BTree<Integer>(256);
		Random r = new Random();
		r.setSeed(47);
		int i = 0;
		try {
			for(i = 0; i < 1000000; i++)
				realistic.add(r.nextInt());
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " at iteration: " + i + ".");
		}
	}

	/* Test removal of the single node in a tree stub.*/
	@Test
	public void testRemovalStub(){
		threeTree.add(10);
		fourTree.add(10);
		assertFalse(threeTree.isEmpty());
		assertFalse(fourTree.isEmpty());
		try {
			threeTree.remove(10);
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when removing a node from a 3-tree stub.");
		}
		try {
			fourTree.remove(10);
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when removing a node from a 4-tree stub.");
		}
		assertTrue(threeTree.isEmpty());
		assertTrue(fourTree.isEmpty());
		threeTree.clear();
		fourTree.clear();
	}

	/* Test removal from a leaf which doesn't underflow. */
	@Test
	public void testLeafNoUnderflow(){
		threeTree.add(9);
		threeTree.add(3);
		threeTree.add(4); // should split the 4-node
		threeTree.add(-10); // Left child is now at capacity
		Integer retVal = null;
		try {
			retVal = threeTree.remove(-10);
			assertEquals(new Integer(-10), retVal);
		} catch(AssertionError err){
			fail("Instead of -10, BTree.remove() returned: " + retVal + ".");
		} catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when removing -10.");
		}
		threeTree.add(10);
		try {
			retVal = threeTree.remove(10);
			assertEquals(new Integer(10), retVal);
		}catch(AssertionError err){
			fail("Instead of 10, BTree.remove() returned: " + retVal + ".");
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when removing 10.");
		}
		threeTree.clear();
	}

	@Test
	public void testElementaryNodeMergeOddM(){
		threeTree.add(5);
		threeTree.add(-2);
		threeTree.add(7);
		try {
			assertEquals(new Integer(5), threeTree.getRoot());
		} catch (EmptyTreeException e) {
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		try {
			threeTree.remove(threeTree.getRoot()); // Should trigger node merging and root underflow
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when removing the root.");
		}
		threeTree.clear();
	}
	
	@Test 
	public void testElementaryNodeMergeEvenM(){
		fourTree.add(10);
		fourTree.add(5);
		fourTree.add(13);
		fourTree.add(2); // Should trigger a node split
		try {
			assertEquals(new Integer(10), fourTree.getRoot());
		} catch (EmptyTreeException e) {
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		try {
			fourTree.remove(5); // Removes a key from a leaf, no underflow in the left child leaf
			fourTree.remove(2); // Removes a key from the left child, causes underflow in the left child leaf
			// The tree should now only consist of a root node with 10 and 13 as keys
			Iterator<Integer> levelOrder = fourTree.levelOrder();
			assertEquals(new Integer(10), levelOrder.next());
			assertEquals(new Integer(13), levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch (EmptyTreeException e) {
			fail("Should not have thrown an EmptyTreeException at this point.");
		} catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when removing the root.");
		}
		fourTree.clear();
	}

	
	@Test
	public void testComplexNodeMerge(){
		Integer[] valsToAdd = {8, 0, 2, -5, -10, 1, 19, -15, -2, 20};
		for(Integer i: valsToAdd)
			threeTree.add(i);
	}
	
	@Test
	public void testInnerNodeNoUnderflow(){
		Integer[] valsToAdd = {5, -2, 7, -10, 20, 5, 6, 6, 5, -20, -30};
		for(Integer i: valsToAdd)
			threeTree.add(i);
		/* The above additions should yield the following tree: 
		 * 
		 * 						   (5)
		 * 						 /	  \
		 * 						/	   \
		 * 					   /	    \
		 * 				   (-20,-2)      (6)
		 * 			       /   |  \		 / \
		 * 		       (-30)(-10) (5,5)(6) (7,20)
		 */
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			Integer[] expectedOrder = {5, -20, -2, 6, -30, -10, 5, 5, 6, 7, 20}; 
			for(Integer i : expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch(EmptyTreeException e){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		assertEquals(new Integer(-20), threeTree.remove(-20)); 
		/* The above removal should leave the tree as follows:
		 * 
		 * 
		 * 						  (5)
		 * 						/	  \
		 * 					  /			\
		 *					/ 			  \	
		 * 			   (-10,5)			  (6)
		 * 			    /  |  \   		  / \
		 * 		    (-30) (-2) (5)  	(6)	(7,20)
		 */
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			Integer[] expectedOrder = {5, -10, 5, 6, -30, -2, 5, 6, 7, 20}; 
			for(Integer i : expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch(EmptyTreeException e){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		threeTree.clear();
	}

	@Test
	public void testLeafUnderflowLeftRotation(){
		threeTree.add(10);
		threeTree.add(50);
		threeTree.add(45);
		threeTree.add(60);
		assertEquals(new Integer(10), threeTree.remove(10));
		try {
			assertEquals(new Integer(50), threeTree.getRoot());
			assertEquals(new Integer(45), threeTree.getMin());
			assertEquals(new Integer(60), threeTree.getMax());
		}catch(EmptyTreeException e){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
	}

	@Test
	public void testLeafUnderflowRightRotation(){
		threeTree.add(-2);
		threeTree.add(50);
		threeTree.add(8);
		threeTree.add(-10);
		assertEquals(new Integer(50), threeTree.remove(50));
		try {
			assertEquals(new Integer(-2), threeTree.getRoot());
			assertEquals(new Integer(-10), threeTree.getMin());
			assertEquals(new Integer(8), threeTree.getMax());
		}catch(EmptyTreeException e){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
	}

	@Test
	public void testInnerNodeUnderflowLeftRotation(){
		Integer[] valsToAdd = {5, 9, 2, 6, 4, 3, 15, 7, 20, 30};
		for(Integer i: valsToAdd)
			threeTree.add(i);
		/* The above additions should yield the following tree:
		 * 
		 * 					(6)
		 * 				   /   \
		 * 				 /	 	\
		 *			 (3)		(15)
		 * 			 / \ 		/  \
		 * 		  (2)  (4,5)  (7,9) (20, 30)
		 */
		
		assertEquals(new Integer(3), threeTree.remove(3));
		
		/* The deletion above should leave the tree looking like:
		 * 
		 * 				  (6)
		 * 				 /	  \
		 * 			   /  	 	\
		 * 			(4)			(15)
		 * 			/ \			/   \
		 * 		   /   \	   /	 \
		 * 		(2)    (5)  (7,9) 	(20,30)
		 */
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			Integer[] expectedOrder = {6, 4, 15, 2, 5, 7, 9, 20, 30}; 
			for(Integer i : expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch(EmptyTreeException e){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
	}

	@Test
	public void testInnerNodeNoUnderFlowOrRotations(){
		Integer[] valsToAdd = {5, -2, 7, -10, 20, 5, 6, 6, 5, -12};
		for(Integer i: valsToAdd)
			threeTree.add(i);
		/* The above additions should yield the following tree:
		 * 
		 * 						  (5)
		 * 						/	  \
		 * 					  /			\
		 *					/ 			  \	
		 * 				  (-2)			  (6)
		 * 			     /    \		      / \
		 * 		    (-12,-10) (5,5)		(6)	(7, 20)
		 */
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			Integer[] expectedOrder = {5, -2, 6 ,-12, -10, 5, 5, 6, 7, 20};
			for(Integer i: expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		} catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		
		assertEquals(new Integer(6), threeTree.remove(6));
		/* The above removal should yield the following tree:
		 *  
		 * 						  (5)
		 * 						/	  \
		 * 					  /			\
		 *					/ 			  \	
		 * 			      (-2)		      (7)
		 * 			    /     \		  	  / \
		 * 		    (-12,-10) (5,5)	    (6) (20)
		 * 
		 * where right rotation has occurred.
		 */
		try {
			Iterator<Integer> levelOrder = threeTree.levelOrder();
			Integer[] expectedOrder = {5, -2, 7, -12, -10, 5, 5, 6, 20}; 
			for(Integer i : expectedOrder)
				assertEquals(i, levelOrder.next());
			assertFalse(levelOrder.hasNext());
		}catch(EmptyTreeException e){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}
		threeTree.clear();
	}
	
	@Test
	public void stressTestForRemove(){
		BTree<Integer> bigTree = new BTree<Integer>(256);
		Random r = new Random();
		r.setSeed(47);
		int i = 0;
		try {
			for(i = 0; i < 1000000; i++)
				bigTree.add(r.nextInt());
		//System.out.println(bigTree.height());
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " at iteration: " + i + ".");
		}
		try {
			for(i = 0; !bigTree.isEmpty(); i++)
				assertEquals(bigTree.getRoot(), bigTree.remove(bigTree.getRoot()));
		} catch(EmptyTreeException exc){
			fail("Threw an EmptyTreeException at iteration: " + i + ".");
		} catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " at iteration: " + i + ".");
		}
	}
}
