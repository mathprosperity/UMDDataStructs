package testcases.symbol_tables;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import datastructures.symbol_tables.*;
import datastructures.symbol_tables.BadRankException;

public class RBBSTOrderedSTTest {

	private OrderedST<String, String> phonebook = new RBBSTOrderedST<String, String>();
	private OrderedST<Integer, String> genericNames = new RBBSTOrderedST<Integer, String>();

	@Test
	public void testSimpleAddAndSize(){
		assertEquals(phonebook.size(), 0);
		assertTrue(phonebook.isEmpty());
		phonebook.put("Suzan", "210-546-7890");
		assertEquals(phonebook.size(), 1);
		assertFalse(phonebook.isEmpty());
		assertTrue(phonebook.contains("Suzan"));
		phonebook.clear();
		assertEquals(phonebook.size(), 0);
		assertTrue(phonebook.isEmpty());
		assertFalse(phonebook.contains("Suzan"));
	}

	@Test
	public void testPutSameKey(){
		phonebook.put("Suzan", "210-546-7890");
		phonebook.put("Suzan", "214-541-7820");
		assertEquals(phonebook.size(), 1);
		phonebook.clear();
	}

	@Test
	public void testRetrieveRightValues(){
		for(int i = 0; i < 200; i++)
			genericNames.put(i, new Integer(i).toString());
		int keyCounter = 0;
		for(Integer ik: genericNames.keys()){
			assertEquals(ik, new Integer(keyCounter));
			assertEquals(genericNames.get(ik), new Integer(keyCounter).toString());
			keyCounter++;
		}
		assertEquals(keyCounter, 200);
		genericNames.clear();
	}

	@Test
	public void testMinAndMax(){
		for(int i = 0; i < 50; i++){
			try{
				genericNames.put(i, " "); // value doesn't matter
			}catch(Throwable t){
				fail("Failed at insertion: " + i + " with message: " + t.getMessage());
			}
		}
		assertEquals(genericNames.min(), new Integer(0));
		assertEquals(genericNames.max(), new Integer(49));
	}

	@Test
	public void testDeletions(){
		for(int i = 0; i < 50; i++){
			try{
				genericNames.put(i, " "); // value doesn't matter
			}catch(Throwable t){
				fail("Failed at insertion: " + i + " with message: " + t.getMessage());
			}
		}
		genericNames.deleteMin();
		assertEquals(genericNames.min(), new Integer(1));
		assertEquals(genericNames.max(), new Integer(49)); // Shouldn't have changed
		assertEquals(genericNames.size(), 49); // 1 less
		genericNames.deleteMax();
		assertEquals(genericNames.min(), new Integer(1)); // Shouldn't have changed
		assertEquals(genericNames.max(), new Integer(48));
		assertEquals(genericNames.size(), 48); // 1 less

		// Also do an arbitrary delete...
		try {
			genericNames.delete(10);
		}catch(Throwable t){
			fail("delete(10) failed with a message of: " + t.getMessage());
		}
		assertEquals(genericNames.size(), 47);

		// Empty up table through constant deletions...
		int deletedElCounter = 0;
		while(!genericNames.isEmpty()){
			try {
				genericNames.deleteMax();
			} catch(Throwable t){
				fail("deleteMax() failed at iteration #:" + deletedElCounter);
			}
			deletedElCounter++;
		}
		assertEquals(deletedElCounter, 47);
	}

	@Test
	public void testFloorAndCeil(){
		for(int i = 10; i > -1; i-=2) // Decrementation step of 2
			genericNames.put(i, " ");
		assertEquals(genericNames.ceiling(2), new Integer(2));
		assertEquals(genericNames.floor(2), new Integer(2));
		assertEquals(genericNames.ceiling(3), new Integer(4));
		assertEquals(genericNames.floor(3), new Integer(2));

		phonebook.put("Alex", "132243464");
		phonebook.put("Melissa", "238472394");
		assertEquals(phonebook.ceiling("Bob"), "Melissa");
		assertEquals(phonebook.floor("Bob"), "Alex");
		
		// When searching for a floor of a key that is smaller than
		// anything in the array, floor() should return the key itself.
		
		assertEquals(phonebook.floor("Aaron"), null);
		
		// Equivalently, when searching for the ceiling of a key that
		// is larger than anything in the array, ceiling() should return
		// the key itself.
		
		assertEquals(phonebook.ceiling("Nick"), null);
		genericNames.clear();
		phonebook.clear();
	}

	@Test
	public void testSelect(){
		for(int i = -1000; i < 1001; i++) //[-1000, 1000]
			genericNames.put(i, new Integer(i).toString());

		for(int i = 0; i < 2001; i++){ // Ranks are non-negative integers
			try { 
				assertEquals(genericNames.select(i), new Integer(i - 1000));
			} catch (BadRankException e) {
				fail("Iteration #" + i + " threw a BadRankException.");
			}
		}
	}

	@Test
	public void testKeyRange(){

		// Examine equivalence of "keys()" and "keys(lo, hi)":

		for(int i = 0; i < 100; i++)
			genericNames.put(new Integer(i), " ");
		assertEquals(genericNames.keys(), 
				genericNames.keys(new Integer(0),new Integer(99)));

		// Check whether an "empty" Iterable is returned when a
		// bad range is given:

		Iterable<Integer> it = genericNames.keys(5, 4);
		assertFalse(it.iterator().hasNext());

		// Examine various different cases. 

		// "high" key in the symbol table. 

		phonebook.put("George", "256-09834-1232");
		phonebook.put("Melissa", " 2323424");
		phonebook.put("Alex", "asdqweq213");
		phonebook.put("Jason", "3243243207");


		Iterable<String> keyRange1 = phonebook.keys("Alex", "George");
		Iterator<String> keyir1 = keyRange1.iterator();
		assertEquals(keyir1.next(), "Alex");
		assertEquals(keyir1.next(), "George");
		try {
			keyir1.next();
			fail("A NoSuchElementException should've been thrown by next().");
		} catch(NoSuchElementException exc){}
		catch(Throwable t){
			fail("Instead of a NoSuchElementException, a " + t.getClass() + 
					" was thrown, with message: " + t.getMessage() + ".");
		}
		assertFalse(keyir1.hasNext());

		// "high" key not in the symbol table.

		Iterable<String> keyRange2 = phonebook.keys("Alex", "Harry");
		Iterator<String> keyir2 = keyRange2.iterator();
		assertEquals(keyir2.next(), "Alex");
		assertEquals(keyir2.next(), "George");
		assertFalse(keyir2.hasNext());

		// Neither "high" nor "low" in the symbol table.

		Iterable<String> keyRange3 = phonebook.keys("Bob", "Harry");
		Iterator<String> keyir3 = keyRange3.iterator();
		assertEquals(keyir3.next(), "George");
		assertFalse(keyir3.hasNext());

		phonebook.clear();
		genericNames.clear();
	}

	@Test
	public void testCopyConstructorAndEquals(){
		for(int i = 1000; i > -1; i--)
			genericNames.put(i, new Integer(i).toString());
		OrderedST<Integer, String> copy = null;
		try {
			copy = new RBBSTOrderedST<Integer, String>(genericNames);
		}catch(Throwable t){
			fail("Copy construction failed with an error message of: " + t.getMessage());
		}
		assertEquals(copy, genericNames);
	}

}
