package testcases.trees;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import datastructures.trees.BinarySearchTree;
import datastructures.trees.EmptyTreeException;
import datastructures.trees.RedBlackBinarySearchTree;

public class RedBlackBinarySearchTreeTest {

	private BinarySearchTree<Integer> intTree= new RedBlackBinarySearchTree<Integer>();

	@Test
	public void testEmptiness(){
		assertTrue(intTree.isEmpty());
		try {
			intTree.getRoot();
		} catch (EmptyTreeException e) { // This exception should be thrown
			assertTrue(true);
		}
		assertEquals(intTree.size(), 0);
		intTree.clear();
	}

	@Test
	public void testFirstTree(){

		
		int[] nums = {5, 9, -1, 2, 0, 4};
		for(Integer n: nums)
			intTree.add(n);
		assertFalse(intTree.isEmpty());
		assertEquals(intTree.size(), nums.length);
		try {
			assertEquals(intTree.getMin(), new Integer(-1));
			assertEquals(intTree.getMax(), new Integer(9));
		} catch (EmptyTreeException e) {
			fail("getMin() and/or getMax() reported an empty tree, which shouldn't be the case.");
		}

		// Try to remove different types of nodes, see what happens.
		try {
			intTree.remove(0);
			assertEquals(intTree.getMin(), new Integer(-1));// Min and Max shouldn't change
			assertEquals(intTree.getMax(), new Integer(9));
			assertEquals(intTree.size(), nums.length - 1); // 1 less
		} catch(EmptyTreeException exc){
			fail("remove() threw an unexpected EmptyTreeException when removing a leaf node.");
		}

		try {
			intTree.remove(2); // After removal of 0, 2 is still a pre-leaf. Check what happens.
			assertEquals(intTree.getMin(), new Integer(-1));// Min and Max shouldn't change
			assertEquals(intTree.getMax(), new Integer(9));
			assertEquals(intTree.size(), nums.length - 2); // 1 less
		} catch(EmptyTreeException exc){
			fail("remove() threw an unexpected EmptyTreeException when removing a pre-leaf node.");
		}

		try {
			intTree.remove(-1); // Remove the tree's smallest element. 4 should now be the minimum element.
			assertEquals(intTree.getMin(), new Integer(4));
			assertEquals(intTree.getMax(), new Integer(9));
			assertEquals(intTree.size(), nums.length - 3); // 1 less
		} catch(EmptyTreeException exc){
			fail("remove() threw an unexpected EmptyTreeException when removing a pre-leaf node.");
		}

		try {
			intTree.remove(9); // Remove the tree's largest element. 5 (the root) should now be the largest element.
			assertEquals(intTree.getMin(), new Integer(4));
			assertEquals(intTree.getMax(), new Integer(5));
			assertEquals(intTree.size(), nums.length - 4); // 1 less
		} catch(EmptyTreeException exc){
			fail("remove() threw an unexpected EmptyTreeException when removing a leaf node.");
		}

		// Two sequential root deletions should provide us with an empty tree.
		try {
			intTree.remove(5);
			intTree.remove(4);
			assertTrue(intTree.isEmpty());
		} catch(EmptyTreeException exc){
			fail("remove() threw an unexpected EmptyTreeException when removing the root.");
		}
		intTree.clear(); // Should not affect the emptiness of the tree 
		assertTrue(intTree.isEmpty());
	}

	@Test
	public void testSecondTree(){

		int[] nums = {-10, 9, -2, 0, -2, 3, 5, 6, -12};
		for(Integer n: nums)
			intTree.add(n);
		try {
			// Removing -10 should keep the same root.
			intTree.remove(-10);
			assertEquals(intTree.getRoot(), new Integer(0));
		} catch(EmptyTreeException exc){
			fail("remove() threw an unexpected EmptyTreeException when removing the root.");
		}

		// Removing -10 again should yield null, because there's no duplicates
		// of the former root node in the tree.

		try {
			assertEquals(intTree.remove(-10), null);
		} catch(EmptyTreeException exc){
			fail("Should not have thrown an EmptyTreeException at this point.");
		}

		// The second removal of -2 should not yield null, because there are two
		// instances of -2 in the tree.
		try {
			intTree.remove(-2);
			assertEquals(intTree.remove(-2), new Integer(-2));
		} catch(EmptyTreeException exc){
			fail("Threw an EmptyTreeException for removal of nodes from a non-empty tree.");
		}

		// Finally, removing the root node up until we get an EmptyTreeException should leave us with
		// an empty tree.

		boolean exceptionThrown = false;
		while(true){
			try {
				intTree.remove(intTree.getRoot());
			} catch(EmptyTreeException exc){
				exceptionThrown = true;
				break;
			}
		}
		if(!(exceptionThrown && intTree.isEmpty()))
			fail("When the EmptyTreeException was thrown, the tree should also be empty.");
		intTree.clear();
	}


	@Test
	public void testTraversalsSimple() throws EmptyTreeException{
		intTree.add(0);
		intTree.add(-1);
		intTree.add(1);
		Iterator<Integer> preorder, inorder, postorder, levelorder;

		// Test the traversals one at a time.
		preorder = intTree.preorder();
		assertTrue(preorder.hasNext());
		assertEquals(preorder.next(), new Integer(0));
		assertEquals(preorder.next(), new Integer(-1));
		assertEquals(preorder.next(), new Integer(1));
		try {
			preorder.next(); // Should throw a NoSuchElementException
			fail("Should've thrown a NoSuchElementException.");
		} catch(NoSuchElementException exc){
			// dummy catchblock
		}

		inorder = intTree.inOrder();
		assertTrue(inorder.hasNext());
		assertEquals(inorder.next(), new Integer(-1));
		assertEquals(inorder.next(), new Integer(0));
		assertEquals(inorder.next(), new Integer(1));
		try {
			inorder.next(); 
			fail("Should've thrown a NoSuchElementException.");
		} catch(NoSuchElementException exc){}

		postorder = intTree.postOrder();
		assertTrue(postorder.hasNext());
		assertEquals(postorder.next(), new Integer(-1));
		assertEquals(postorder.next(), new Integer(1));
		assertEquals(postorder.next(), new Integer(0));
		try {
			postorder.next(); 
			fail("Should've thrown a NoSuchElementException.");
		}catch(NoSuchElementException exc) {}

		levelorder = intTree.levelOrder();
		assertTrue(levelorder.hasNext());
		assertEquals(levelorder.next(), new Integer(0));
		assertEquals(levelorder.next(), new Integer(-1));
		assertEquals(levelorder.next(), new Integer(1));
		try {
			levelorder.next(); 
			fail("Should've thrown a NoSuchElementException.");
		}catch(NoSuchElementException exc) {}

		intTree.clear();
	}

	@Test
	public void testTraversalsComplex() throws EmptyTreeException{
		int[] nums = {5, 9, -1, 2, 0, 4};
		for(Integer n: nums)
			intTree.add(n);
		Iterator<Integer> preorder, inorder, postorder, levelorder;

		// preorder
		preorder = intTree.preorder();
		assertEquals(preorder.next(), new Integer(5));
		assertEquals(preorder.next(), new Integer(0));
		assertEquals(preorder.next(), new Integer(-1));
		assertEquals(preorder.next(), new Integer(4));
		assertEquals(preorder.next(), new Integer(2));
		assertEquals(preorder.next(), new Integer(9));


		// inorder
		inorder = intTree.inOrder();
		assertEquals(inorder.next(), new Integer(-1));
		assertEquals(inorder.next(), new Integer(0));
		assertEquals(inorder.next(), new Integer(2));
		assertEquals(inorder.next(), new Integer(4));
		assertEquals(inorder.next(), new Integer(5));
		assertEquals(inorder.next(), new Integer(9));

		// postorder
		postorder = intTree.postOrder();
		assertEquals(postorder.next(), new Integer(-1));
		assertEquals(postorder.next(), new Integer(2));
		assertEquals(postorder.next(), new Integer(4));
		assertEquals(postorder.next(), new Integer(0));
		assertEquals(postorder.next(), new Integer(9));
		assertEquals(postorder.next(), new Integer(5));

		// levelorder
		levelorder = intTree.levelOrder();
		assertEquals(levelorder.next(), new Integer(5));
		assertEquals(levelorder.next(), new Integer(0));
		assertEquals(levelorder.next(), new Integer(9));
		assertEquals(levelorder.next(), new Integer(-1));
		assertEquals(levelorder.next(), new Integer(4));
		assertEquals(levelorder.next(), new Integer(2));
		intTree.clear();
	}

	// We will now make sure that, when removing various elements
	// from an RB-BST, the node traversals provide us with
	// expected results. We will also assert that prior and after
	// the node removals, the tree still retains perfect black height.
	@Test
	public void testRemovalCorrectness(){
		int[] nums = {5, 9, -1, 2, 0, 4};
		for(Integer n: nums)
			intTree.add(n);
		try {
			assertTrue(((RedBlackBinarySearchTree<Integer>) intTree).isRBBST());
		} catch (EmptyTreeException e1) {
			fail("Tree is not empty: should not have thrown an EmptyTreeException at this point.");
		}
		// The above lines will yield an RB-BST that looks like the following
		// (We use double lines for red links, single ones for black links)

		/*
		 * 
		 *                5
		 *              // \ 
		 *             0    9
		 *           /   \
		 *          1     4
		 *              //
		 *              2
		 */

		try {
			intTree.remove(0);
			assertTrue(((RedBlackBinarySearchTree<Integer>) intTree).isRBBST());
		} catch (EmptyTreeException e) {
			fail("An EmptyTreeException should not have been thrown at this point.");
		}

		// The above call should now give us an RB-BST that looks like: 

		/*
		 * 				5
		 *            //  \
		 *           2     9
		 *          / \
		 *        -1   4
		 *       
		 */

		// We will check whether that's the case through a level-order traversal.

		Iterator<Integer> levelOrder = null;
		try {
			levelOrder = intTree.levelOrder();
		} catch (EmptyTreeException e) {
			fail("Tree is not empty: should not have thrown an EmptyTreeException");
		}
		assertNotEquals(levelOrder, null);
		assertTrue(levelOrder.hasNext());
		assertEquals(new Integer(5), levelOrder.next());
		assertEquals(new Integer(2), levelOrder.next());
		assertEquals(new Integer(9), levelOrder.next());
		assertEquals(new Integer(-1), levelOrder.next());
		assertEquals(new Integer(4), levelOrder.next());
		assertFalse(levelOrder.hasNext());

		try {
			intTree.remove(4);
		} catch(EmptyTreeException exc){
			fail("Tree is not empty: Should not have thrown an EmptyTreeException at this point.");
		}

		/*
		 * The above removal should leave the RB-BST at the following state:
		 * 
		 *				5
		 *            /  \
		 *           2     9
		 *          // 
		 *        -1   
		 */

		try {
			levelOrder = intTree.levelOrder();
		} catch (EmptyTreeException e) {
			fail("Tree is not empty: should not have thrown an EmptyTreeException");
		}
		assertNotEquals(levelOrder, null);
		assertTrue(levelOrder.hasNext());
		assertEquals(new Integer(5), levelOrder.next());
		assertEquals(new Integer(2), levelOrder.next());
		assertEquals(new Integer(9), levelOrder.next());
		assertEquals(new Integer(-1), levelOrder.next());
		assertFalse(levelOrder.hasNext());
		intTree.clear();
	}
	
	@Test
	public void testHeight(){
		/*
		 * We will make sure that the red-black tree
		 * 
		 * 
		 *                5
		 *              // \ 
		 *             0    9
		 *           /   \
		 *          1     4
		 *              //
		 *              2
		 *              
		 *  has a height of 4. 
		 */
		
		int[] nums = {5, 9, -1, 2, 0, 4};
		for(Integer n: nums)
			intTree.add(n);
		assertEquals(4, intTree.height());
		intTree.clear();
		int[] nums2 = {-10, 9, -2, 0, -2, 3, 5, 6, -12};
		for(Integer n: nums2)
			intTree.add(n);
		
		/*
		 * 
		 *  The above insertions would yield the following ordinary binary
		 *  search tree of height 7:
		 *  
		 *   				 10
		 *                  /  \
		 *                -12   9
		 *                     /
		 *                    -2
		 *                      \ 
		 *                       0
		 *                      / \
		 *                    -2   3
		 *                          \
		 *                           5 
		 *                            \ 
		 *                              6
		 *                              
		 *                              
		 *   But in the case of an RB-BST, they should yield the following
		 *   tree of height 4 (did the calculations manually):
		 *   
		 *   							 0
		 *    						  /    \
		 *     						-2      5
		 *     						/ \    / \
		 *     					  -10 -2  3   9
		 *     					  //		 //
		 *     					 -12		 6
		 */
		
		assertEquals(intTree.height(), 4);		
		intTree.clear();
	}
}
