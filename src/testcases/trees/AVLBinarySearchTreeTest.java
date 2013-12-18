package testcases.trees;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import datastructures.trees.*;

public class AVLBinarySearchTreeTest {

	private BinarySearchTree<Integer> intTree= new AVLBinarySearchTree<Integer>();

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
	public void testAddFirst(){
		/*                    
		 * 	Adding these numbers sequentially: {5, 9, -1, 2, 0, 4} yields
		 *  the classic binary tree
		 *  
		 * 							   5
		 * 						      / \
		 *                           /   \
		 *                          -1    9
		 *                           \
		 *                            \
		 *                             2
		 *                            / \
		 *                           /   \
		 *                          0     4
		 *                          
		 *   but in terms of an AVL tree, it should yield:
		 *   
		 *   					2
		 *   				   / \
		 *    			      /   \
		 *                   0     5
		 *                  /     / \
		 *                 /     /   \
		 *                -1    4     9
		 */

		Integer[] nums = {5, 9, -1, 2, 0, 4};
		for(Integer i: nums)
			intTree.add(i);

		checkLevelOrderTraversal(intTree, new Integer[]{2, 0, 5, -1, 4, 9});
		intTree.clear();
	}

	@Test
	public void testAddSecond(){
		/* 
		 * 
		 * Adding the numbers {-10, 9, -2, 0, -2, 3, 5, 6, -12}
		 * 
		 * should yield the AVL tree:  
		 * 
		 * 					
		 * 								0
		 * 							  /   \	
		 * 							/	    \
		 * 						  /	   		  \
		 *						-2			   5 						
		 * 					   /  \			  / \
		 * 					  /    \	     /	 \
		 * 					-10     -2		3	  9
		 * 					/					 / 
		 * 				   /					/   
		 * 				 -12				   6	 
		 */
		Integer[] nums = {-10, 9, -2, 0, -2, 3, 5, 6, -12};
		for(Integer i: nums)
			intTree.add(i);
		checkLevelOrderTraversal(intTree, new Integer[]{0, -2, 5, -10, -2, 3, 9, -12, 6});
		intTree.clear();

	}
	
	@Test
	public void testRemoval(){
		/*   							
		 * Removing 9 from the AVL tree:
		 * 
		 * 
		 *	 							0
		 * 							  /   \	
		 * 							/	    \
		 * 						  /	   		  \
		 *						-2			   5 						
		 * 					   /  \			  / \
		 * 					  /    \	     /	 \
		 * 					-10     -2		3	  9
		 * 					/					 / 
		 * 				   /					/   
		 * 				 -12				   6
		 * 
		 * should yield the AVL tree:
		 * 
		 * 								0
		 * 							  /   \	
		 * 							/	    \
		 * 						  /	   		  \
		 *						-2			   5 						
		 * 					   /  \			  / \
		 * 					  /    \	     /	 \
		 * 					-10     -2		3	  6
		 * 					/					 
		 * 				   /					   
		 * 				 -12				   	 
		 */
		
		Integer[] nums = {-10, 9, -2, 0, -2, 3, 5, 6, -12};
		for(Integer i: nums)
			intTree.add(i);
		try {
			intTree.remove(9);
			assertEquals(8, intTree.size());
			checkLevelOrderTraversal(intTree, new Integer[]{0, -2, 5, -10, -2, 3, 6, -12});
		} catch (EmptyTreeException e) {
			fail("Tree is not empty; should not have thrown an EmptyTreeException at this point.");
		}
		
		/*
		 * Removing -2 should yield the following AVL tree:
		 * 
		 * 
		 * 								0
		 * 							  /   \	
		 * 							/	    \
		 * 						  /	   		  \
		 *						-2			   5 						
		 * 					   /  			  / \
		 * 					  /    	     	 /	 \
		 * 					-10     		3	  6
		 * 					/					  
		 * 				   /					   
		 * 				 -12				   	 
		 * 
		 * 
		 */
		
		try {
			intTree.remove(-2);
			assertEquals(7, intTree.size());
			checkLevelOrderTraversal(intTree, new Integer[]{0, -2, 5, -10, 3, 6, -12});
		} catch(EmptyTreeException exc){
			fail("The tree is not empty; an EmptyTreeException should not have been thrown.");
		}
		try {
			while(!intTree.isEmpty())
				intTree.remove(intTree.getRoot());
		} catch(Throwable t){
			fail("A " + t.getClass() + " was thrown, with message: " + t.getMessage() + ".");
		}
		assertTrue(intTree.isEmpty() && intTree.size() == 0);
		intTree.clear();
	}
	
	private void checkLevelOrderTraversal(BinarySearchTree<Integer> tree, Integer[] nums){
		Iterator<Integer> levelOrder = null;
		try {
			levelOrder = tree.levelOrder();
		} catch (EmptyTreeException e) {
			fail("Tree is not empty: should not have yielded an EmptyTreeException.");
		}
		assertTrue(levelOrder.hasNext());
		for(Integer i: nums)
			assertEquals(i, levelOrder.next());
		assertFalse(levelOrder.hasNext());
	}

}
