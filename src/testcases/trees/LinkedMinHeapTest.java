package testcases.trees;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;

import datastructures.trees.*;
public class LinkedMinHeapTest {

	private MinHeap<Integer> intMinHeap = new LinkedMinHeap<Integer>();
	private MinHeap<String> stringMinHeap = new LinkedMinHeap<String>();

	@Test
	public void testConstructorAndBasicAdd(){
		assertEquals(intMinHeap.size(), 0);
		assertEquals(stringMinHeap.size(), 0);
		assertTrue(intMinHeap.isEmpty() && stringMinHeap.isEmpty());
		stringMinHeap.add("Dibidabo");
		assertEquals(stringMinHeap.size(), 1);
		stringMinHeap.clear();
		assertTrue(stringMinHeap.isEmpty());
	}
	
	@Test
	public void testCopyConstructorAndEquals(){
		MinHeap<String> stringMinHeapCopy = new LinkedMinHeap<String>(stringMinHeap);
		assertEquals(stringMinHeapCopy, stringMinHeap);
		assertEquals(stringMinHeap, stringMinHeapCopy);
	}

	@Test
	public void testSimpleAdd(){
		String[] strings = {"George", "Gianna", "Greg"};
		for(String s : strings)
			stringMinHeap.add(s);
		try {
			assertEquals(stringMinHeap.getMin(), "George");
		} catch (EmptyHeapException e) {
			fail("An Empty Heap exception should not have been thrown by getMin()," + 
						" since the heap is not empty.");
		} // root element should be "George"
		assertEquals(stringMinHeap.size(), 3);
		try {
			assertEquals(stringMinHeap.removeMin(), "George");
		}catch(EmptyHeapException e){
			fail("An Empty Heap exception should not have been thrown by removeMin()," + 
						" since the heap is not empty.");
		}
		assertEquals(stringMinHeap.size(), 2);
		assertFalse(stringMinHeap.isEmpty());
		try {
			while(!stringMinHeap.isEmpty())
				stringMinHeap.removeMin();
		} catch(EmptyHeapException e){
			fail("An EmptyHeapException should not have been thrown here.");
		}
	}
	
	@Test
	public void testSimpleAddUnbalanced(){
		/*
		 * [5, -1, 0, 8, 3, 10] should yield:
		 * 
		 * 					 -1
		 * 				    /   \
		 * 				   3     0
		 *                / \   /
		 *               8   5 10
		 */
		
		int[] ints = {5, -1, 0, 8, 3, 10};
		for(Integer i: ints)
			intMinHeap.add(i);
		assertEquals(intMinHeap.size(), ints.length);
		try{
			// Take elements off one-by-one, examine min
			assertEquals(intMinHeap.removeMin(), new Integer(-1));
			assertEquals(intMinHeap.removeMin(), new Integer(0));
			assertEquals(intMinHeap.removeMin(), new Integer(3));
			assertEquals(intMinHeap.removeMin(), new Integer(5));
			assertEquals(intMinHeap.removeMin(), new Integer(8));
			assertEquals(intMinHeap.removeMin(), new Integer(10));
		} catch(EmptyHeapException exc){
			fail("Should not have thrown an empty heap exception at this point.");
		}
	}
	
	@Test
	public void testIterator(){
		/*
		 * An input of [5, -1, 0, 8, 3, 10] was determined by the previous test
		 * to be yielding:
		 * 
		 * 					 -1
		 * 				    /   \
		 * 				   3     0
		 *                / \   /
		 *               8   5 10
		 *               
		 *  Our goal now is to make sure that iterator() returns an Iterator which
		 *  accesses the elements in ascending order, as implied by the MinHeap structure.
		 */
		
		Integer[] ints = {5, -1, 0, 8, 3, 10};
		for(Integer i : ints)
			intMinHeap.add(i);
		Arrays.sort(ints); // Arrays.sort always sorts in ascending order.
		int currentIndex = 0;
		for(Integer i : intMinHeap)
			assertEquals(i, ints[currentIndex++]);
	}
}
