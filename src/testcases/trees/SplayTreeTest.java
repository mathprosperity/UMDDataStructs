package testcases.trees;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Random;
import org.junit.Test;

import datastructures.trees.EmptyTreeException;
import datastructures.trees.SplayTree;

public class SplayTreeTest {

	private SplayTree<Integer> spTree = new SplayTree<Integer>();
	private Random r = new Random();
	private final static String EMPTY_MSG = "Tree is not empty; should not have thrown an EmptyTreeException at this point.";

	@Test
	public void testEmptinessAndSize(){
		assertTrue(spTree.isEmpty());
		assertEquals(0, spTree.size());
		spTree.add(1);
		assertFalse(spTree.isEmpty());
		assertEquals(1, spTree.size());
		spTree.clear();
		assertTrue(spTree.isEmpty());
		assertEquals(0, spTree.size());
	}

	@Test
	public void testInsertionsSimple(){
		Integer[] ints = new Integer[]{-10, 10, 2};
		for(Integer i: ints)
			spTree.add(i);
		/* The tree should now look like this:
		 * 
		 * 				2
		 * 			   / \
		 *           -10 10
		 */
		try {
			Iterator<Integer> it = spTree.preorder();
			assertTrue(checkExpectedOrder(it, new Integer[]{2, -10, 10}));
		} catch(EmptyTreeException exc){
			fail(EMPTY_MSG);
		}
	}

	@Test
	public void testInsertionsComplex(){
		int i = 0, randNum = 0;
		try {
			for(i = 0; i < 1000; i++){
				randNum = r.nextInt();
				spTree.add(randNum);
				assertEquals(spTree.getRoot(), new Integer(randNum));
			}
		} catch (EmptyTreeException e) {
			fail("Iteration " + i + " with random number " + randNum + ": " + EMPTY_MSG);
		} catch(Throwable t){
			fail("Iteration " + i + " with random number " + randNum + ": threw a " + t.getClass() + " with message: " + t.getMessage());
		}
		spTree.clear();
	}

	@Test
	public void testSeekingSimple(){
		Integer[] ints = new Integer[]{-10, 10, 2};
		for(Integer i : ints)
			spTree.add(i);
		/* The tree now looks like this:
		 * 
		 * 				2
		 * 			  /   \
		 * 			-10		10
		 */
		try {
			assertTrue(seekElements(spTree, new Integer[]{2, -10, 10})); // All those elements are in the tree and should be found at the root after seeking
			// We re-insert the elements for the subsequent tests
			// because seekElements() will've re-organized them.
			spTree.clear();
			for(Integer i : ints)
				spTree.add(i);
			assertEquals(null, spTree.find(-11));

			/* Despite the unsuccessful search, the underlying splaying of the tree should make
			 * it unbalanced, like so:
			 * 
			 * 					-10
			 *					   \
			 *						2
			 *						  \	
			 *							10 				
			 */
			Iterator<Integer> postOrder = spTree.postOrder();
			assertTrue(checkExpectedOrder(postOrder, new Integer[]{10, 2, -10}));
		} catch (EmptyTreeException e) {
			fail(EMPTY_MSG);
		}
		spTree.clear();
	}

	@Test
	public void testSeekingComplex(){
		int i = 0, j = 0;
		try{
			for(i = 0; i < 1000; i++)
				spTree.add(i);
			for(j = 0; j < 1000; j++){
				assertEquals(new Integer(j), spTree.find(j)); // The last value of i generated is the immediate predecessor of all new additions.
				assertEquals(new Integer(j), spTree.getRoot()); // Which should also be at the root
			}
		} catch(Throwable t){
			fail("After " + i + " insertions and " + j + " searches, we caught a " + t.getClass() + " with message: " + t.getMessage() + ".");
		}
		spTree.clear();
	}

	@Test
	public void testDeletionSimple(){
		Integer[] ints = new Integer[]{-10, 10, 2};
		for(Integer i : ints)
			spTree.add(i);

		/* The tree now looks like this:
		 * 
		 * 				2
		 * 			  /   \
		 * 			-10		10
		 * 
		 * Trying to delete -11 should not be successful in deleting any nodes, but it should
		 * splay the tree, bringing -10 to the root for the unbalanced result:
		 * 
		 * 					-10
		 *					   \
		 *						2
		 *						  \	
		 *							10 				
		 */
		assertEquals(3, spTree.size());
		spTree.remove(-11);
		assertEquals(3, spTree.size());
		try {
			Iterator<Integer> postOrder = spTree.postOrder();
			assertTrue(checkExpectedOrder(postOrder, new Integer[]{10, 2, -10}));
			while(!spTree.isEmpty())
				spTree.remove(spTree.getRoot());
			assertTrue(spTree.isEmpty());
		} catch(EmptyTreeException e){
			fail(EMPTY_MSG);
		}
		spTree.clear();
	}

	@Test
	public void testDeletionComplex(){
		int i = 0, j = 0;
		try {
			for(i = 0; i < 1000; i++)
				spTree.add(i);
			for(j = 999; j > -1; j--){
				assertEquals(new Integer(j), spTree.remove(j)); // All elements removed are actually in the tree
				if(j > 0)
					assertEquals(new Integer(j - 1), spTree.getRoot()); // When 999 is removed, 998 will be the new root, and so on until 0.
				else
					assertTrue(spTree.isEmpty());
			}
		} catch(Throwable t){
			fail("After " + i + " insertions and " + j + " deletions, we caught a " + t.getClass() + " with message: " + t.getMessage() );
		}
	}

	/**
	 * 
	 * @param spTree A {@link datastructures.trees.SplayTree} of Integers.
	 * @param element The element we seek in <tt>spTree</tt>
	 * @return <tt>true</tt> if the element was in the tree and proper splaying occurred, bringing it to the root of the tree.
	 * <tt>false</tt> otherwise.
	 * @throws EmptyTreeException If the tree is empty.
	 */
	private boolean seekElement(SplayTree<Integer> spTree, Integer element) throws EmptyTreeException{
		Integer retVal = spTree.find(element);
		if(!retVal.equals(element))
			return false;
		if(!spTree.getRoot().equals(retVal)) // Sought elements, when found, should ascend to the root in a splay tree
			return false;
		return true;
	}

	/**
	 * @param spTree A {@link datastructures.trees.SplayTree} of Integers.
	 * @param elements An array of Integers that holds the elements we are seeking.
	 * @return <tt>true</tt> If all the elements are in the tree and proper splaying occurs for every seeking.
	 * <tt>false</tt> otherwise.
	 * @throws EmptyTreeException If the tree is empty.
	 */
	private boolean seekElements(SplayTree<Integer> spTree, Integer[] elements) throws EmptyTreeException{
		for(Integer i : elements)
			if(!seekElement(spTree, i))
				return false;
		return true;
	}

	/**
	 * Checks whether the iterator's elements are in the order specified by array. 
	 * @param it The iterator whose elements we want to sequentially scan.
	 * @param array The array holding the order of elements desired.
	 * @return <tt>true</tt> if <tt>it</tt> returns the elements in the order suggested by <tt>array</tt>, <tt>false</tt> otherwise.
	 */
	private boolean checkExpectedOrder(Iterator<Integer> it, Integer[] array){
		if(!it.hasNext())
			return false;
		for(Integer i: array)
			if(!it.next().equals(i))
				return false;
		if(it.hasNext())
			return false;
		return true;
	}

}
