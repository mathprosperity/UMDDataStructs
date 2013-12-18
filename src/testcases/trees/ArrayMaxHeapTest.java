package testcases.trees;

import static org.junit.Assert.*;
import java.util.Arrays;
import datastructures.trees.*;

import org.junit.Test;
import java.util.Iterator;

public class ArrayMaxHeapTest {

	private MaxHeap<Integer> intHeap = new ArrayMaxHeap<Integer>();
	private MaxHeap<String> stringHeap = new ArrayMaxHeap<String>();

	@Test
	public void testConstructorAndBasicAdd(){
		assertEquals(intHeap.size(), 0);
		assertEquals(stringHeap.size(), 0);
		assertTrue(intHeap.isEmpty() && stringHeap.isEmpty());
		stringHeap.add("Dibidabo");
		assertEquals(stringHeap.size(), 1);
		stringHeap.clear();
	}

	@Test
	public void testCopyConstructorAndEquals(){
		MaxHeap<String> stringHeapCopy = new ArrayMaxHeap<String>(stringHeap);
		assertEquals(stringHeapCopy, stringHeap);
		assertEquals(stringHeap, stringHeapCopy);
	}


	@Test
	public void testSimpleAdd() throws EmptyHeapException{
		/*
		 * [5, 6, 0] should yield:   6
		 *  	  					/ \
		 * 						   5   0
		 */

		intHeap.add(5);
		intHeap.add(6);
		assertEquals(intHeap.getMax(), new Integer(6));
		intHeap.add(0);
		assertEquals(intHeap.size(), 3);
		assertFalse(intHeap.isEmpty());
		try {
			assertEquals(intHeap.getMax(), new Integer(6));
			assertEquals(intHeap.removeMax(), new Integer(6));
			assertEquals(intHeap.removeMax(), new Integer(5));
			assertEquals(intHeap.removeMax(), new Integer(0));
		} catch(EmptyHeapException exc){
			fail("An EmptyHeapException should not have been thrown at this point.");
		}
		intHeap.clear();
		assertTrue(intHeap.isEmpty());
	}

	@Test
	public void testComplexAdd(){
		/*
		 *  [ 5, 6, 0, -1, -3, 7, -10] should yield:
		 *  
		 *  				 7
		 *                 /   \ 
		 *				 5       6
		 *              / \     / \
		 *            -1  -3   0   -10	  
		 */

		int[] vals = {5, 6, 0, -1, -3, 7, -10};
		for(Integer v: vals)
			intHeap.add(v);
		try {
			assertEquals(intHeap.getMax(), new Integer(7));
			assertEquals(intHeap.removeMax(), new Integer(7));
			assertEquals(intHeap.removeMax(), new Integer(6));
			while(!intHeap.isEmpty())
				intHeap.removeMax();
		} catch (EmptyHeapException exc) {
			fail("Should not have thrown an EmptyHeapException at this point.");
		}
		assertTrue(intHeap.isEmpty());
		intHeap.clear();
	}

	@Test
	public void testUnbalancedAdd1(){
		/*
		 * [ 5, 6, 0, -1, -3, 7, -10, 0] should yield:
		 * 
		 *  *  				 7
		 *                 /   \ 
		 *				 5       6
		 *              / \     / \
		 *             0  -3   0   -10	  
		 *            /
		 *          -1
		 */

		int[] vals = {5, 6, 0, -1, -3, 7, -10, 0};
		for(Integer v: vals)
			intHeap.add(v);
		try {
			assertEquals(intHeap.removeMax(), new Integer(7));

			/*
			 * At this point, the heap should look like:
			 * 
			 * 				
			 * 						6
			 *            		  /   \
			 *                   5      0
			 *                  / \    / \
			 *                 0  -3 -1  -10
			 */
			assertEquals(intHeap.removeMax(), new Integer(6));

			/*
			 * At this point:
			 * 					5
			 * 	       		  /   \
			 *               0     0
			 *              / \   / \
			 *            -10 -3 -1 -10
			 * 
			 */
		} catch (EmptyHeapException e) {
			fail("Should not have thrown an EmptyHeapException at this point.");
		}
		intHeap.clear();
	}

	@Test
	public void testUnbalancedAdd2(){
		/*
		 * [ 5, 6, 0, -1, -3, 7, -10, 0, 4] should yield:
		 * 
		 *  *  				 7
		 *                 /   \ 
		 *				 5       6
		 *              / \     / \
		 *             4  -3   0   -10	  
		 *            / \
		 *          -1   0
		 */

		int[] vals = {5, 6, 0, -1, -3, 7, -10, 0, 4};
		for(Integer v: vals)
			intHeap.add(v);
		try {
			assertEquals(intHeap.removeMax(), new Integer(7));
		} catch (EmptyHeapException e) {
			fail("Should not have thrown an EmptyHeapException at this point.");
		}
		intHeap.clear();
	}

	@Test
	public void testIterator(){
		/*
		 * [ 5, 6, 0, -1, -3, 7, -10, 0, 4] yields the following heap:
		 * 
		 *  				 7
		 *                 /   \ 
		 *				 5       6
		 *              / \     / \
		 *             4  -3   0   -10	  
		 *            / \
		 *          -1   0
		 *          
		 * and the iterator() method should return an Iterator that returns
		 * the elements in a descending order, akin to heapsort.
		 */

		Integer[] vals = {5, 6, 0, -1, -3, 7, -10, 0, 4};
		for(Integer v : vals)
			intHeap.add(v);
		Arrays.sort(vals); // Sorting is always done in ascending order
		int currIndex = vals.length - 1;
		for(Integer it : intHeap)
			assertEquals(it, vals[currIndex--]);
	}
}


