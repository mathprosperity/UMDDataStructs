package testcases.lists;

import static org.junit.Assert.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.junit.Test;

import datastructures.lists.ArrayListBasedList;
import datastructures.lists.DoublyLinkedList;
import datastructures.lists.EmptyListException;
import datastructures.lists.IllegalListAccessException;
import datastructures.lists.List;
import datastructures.lists.LinkedList;

public class DoublyLinkedListTest {

	private final int DEFAULT_CAPACITY = 50;
	private List<String> stringList = new DoublyLinkedList<String>();
	private List<Integer> integerList = new DoublyLinkedList<Integer>();
	
	private String[] strings = {"Madrigal", "Meth", "Walt", "Jessie", "Brock"};

	@Test
	public void testConstructorsAndSize(){
		assertEquals(stringList.size(), 0);
		assertEquals(integerList.size(), 0);
	}

	@Test
	public void testCapacityShifting(){
		for(int i = 0; i < DEFAULT_CAPACITY; i++)
			integerList.pushBack(i);
		try {
			integerList.pushBack(DEFAULT_CAPACITY + 1);
		} catch(IndexOutOfBoundsException ie){
			fail("Adding element at index " + (DEFAULT_CAPACITY + 1) + " should work...");
		}
		assertEquals(integerList.size(), DEFAULT_CAPACITY + 1);
		integerList.clear();
	}
	
	@Test
	public void testCopyConstructorAndEquals(){
		
		// First, do a standard copy construction and test it.
		for(String s: strings)
			stringList.pushBack(s);
		List<String> stringList2 = new LinkedList<String>(stringList);
		assertEquals(stringList2, stringList);
		
		// Second, create a new list of type ArrayListLinearList<String> and then
		// see whether you can create an ArrayLinearList<String> by copying *that* list.
		
		List<String> stringList3 = new ArrayListBasedList<String>(stringList);
		assertEquals(stringList3, stringList);
		assertEquals(stringList3, stringList2);
	}

	@Test
	public void testPushFront(){
		stringList.pushFront("Mack");
		try {
			assertEquals(stringList.getFirst(), "Mack");
		}catch(EmptyListException exc){
			fail("List should not be empty after first push.");
		}
		stringList.pushFront("Jello");
		try {
			assertEquals(stringList.getFirst(), "Jello");
		}catch(EmptyListException exc){
			fail("List should not be empty after second push.");
		}
		stringList.clear();
	}

	@Test 
	public void testPushBack(){
		stringList.pushBack("Mack");
		try {
			assertEquals(stringList.getLast(), "Mack");
			assertEquals(stringList.getFirst(), "Mack"); // First and last elements should be the same at this point.
		}catch(EmptyListException exc){
			fail("List should not be empty after first push.");
		}
		stringList.pushBack("Jello");
		try {
			assertEquals(stringList.getLast(), "Jello");
			assertEquals(stringList.getFirst(), "Mack"); // First element should still be "Mack".
		}catch(EmptyListException exc){
			fail("List should not be empty after second push.");
		}
		stringList.clear();
	}

	@Test
	public void testGetters(){
		for(int i = 0; i < 10; i++)
			integerList.pushBack(i);
		try {
			assertEquals(integerList.getFirst(), new Integer(0));
			assertEquals(integerList.getLast(), new Integer(9));
		} catch (EmptyListException e) {
			fail("EmptyListException thrown for a non-empty list.");
		}
		try {
			assertEquals(integerList.get(2), new Integer(2));
		} catch(EmptyListException e){
			fail("EmptyListException thrown for a non-empty list.");
		} catch(IllegalListAccessException ile){
			fail("IllegalListAccessException thrown for a valid accessing");
		}
		integerList.clear();
	}
	
	@Test
	public void testClear(){
		for(int i = 0; i < 10; i++)
			integerList.pushBack(i);
		assertEquals(integerList.size(), 10);
		integerList.clear();
		assertEquals(integerList.size(), 0);
	}

	@Test
	public void testContains(){
		for(int i = 0; i < 1000; i++)
			if(i % 2 == 0)
				integerList.pushBack(i);
		for(int i = 0; i <=998; i+=2) // even integers in [0, 999]
			assertTrue(integerList.contains(i));
		for(int i = 1; i <=999; i+=2) // odd integers in [0, 999]
			assertFalse(integerList.contains(i));
		integerList.clear();
	}

	@Test
	public void testRemoving(){
		stringList.pushBack("The");
		stringList.pushBack("resemblance");
		stringList.pushBack("is");
		stringList.pushBack("uncanny.");
		assertTrue(stringList.remove("is"));
		assertFalse(stringList.remove("is")); // No duplicates of "is".
		assertTrue(stringList.removeAll("The"));
		// The list's length should now be 2, so if I ask of the object
		// to remove the object at index 2, I should get an IllegalListAccessException thrown at me.
		try {
			stringList.remove(2);
		} catch(IllegalListAccessException ie){
			assertTrue(true);
		}
		// However, removing 0 and 1 should be ok...
		try {
			stringList.remove(0);
			stringList.remove(0); // Note that the first element will be at position 0 after the previous line is executed!
		} catch(IllegalListAccessException ae){
			fail("Positions 0 and 1 should be valid for this list to remove from...");
		}

		//... and should leave me with a list of size 0
		assertTrue(stringList.isEmpty());

		// Let us also make sure that duplicates are removed from removeAll()
		// and that leaves us with a list of the appropriate size...
		integerList.pushBack(1);
		integerList.pushFront(1);
		integerList.pushFront(2);
		assertTrue(integerList.removeAll(1));
		assertFalse(integerList.contains(1));
		assertTrue(integerList.contains(2));
		assertEquals(integerList.size(), 1);
		integerList.clear();
	}

	@Test
	public void testIteratorAndConcurrentModifications(){
		for(String s: strings) // private array
			stringList.pushBack(s);
		Iterator<String> it = stringList.iterator();
		int count = 0;
		while(it.hasNext())
			assertEquals(strings[count++], it.next());
		Iterator<String> it2 = stringList.iterator();
		try {
			it2.remove();
			fail("Call should've thrown an IllegalStateException.");
		}catch(IllegalStateException ile){}
		catch(Throwable t){
			fail("Threw a " + t.getClass() + " with a message of: " + t.getMessage() + 
					" instead of a " + new IllegalStateException().getClass() + ".");
		}
		try {
			it2.next(); // This should NOT throw a ConcurrentModificationException, because we allow for removals from within iterators.
		} catch(ConcurrentModificationException exc){
			fail("Exception should not have been thrown.");
		}
		
		try {
			it2.remove(); // This should NOT throw an IllegalStateException.
		}catch(IllegalStateException ile){
			fail("Should not have thrown an IllegalStateException at this point.");
		}
		
		try {
			stringList.remove(0);
			it2.next(); // This SHOULD throw a ConcurrentModificationException.
			fail("Concurrent Modification Exception should've been thrown.");
		} catch(ConcurrentModificationException exc){
			// Good
		} catch(Throwable t){// but no other exception should be thrown!
			fail("Instead of a " + (new ConcurrentModificationException().getClass()) + 
					", a " + t.getClass() + " was thrown, with message: " + t.getMessage() + ".");
		}
		// Lastly, check to see whether removal via an iterator 
		// results in predictable results.
		for(int i = 0 ; i < 10; i++)
			integerList.pushBack(i);
		Iterator<Integer> intit= integerList.iterator();
		
		while(intit.hasNext()){
			Integer next = intit.next();
			if(next.compareTo(5) < 0)
				intit.remove();
		}
		intit = integerList.iterator(); // reset iterator, re-check
		for(int i = 5; i < 10; i++)
			assertEquals(intit.next(), new Integer(i));
		
		stringList.clear();
		integerList.clear();
	}
	
	@Test
	public void testToString(){
		for(int i = 1; i <= 10; i++)
			integerList.pushBack(i);
		assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]", integerList.toString());
		integerList.clear();
	}

}
