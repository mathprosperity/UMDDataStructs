package testcases.symbol_tables;

import static org.junit.Assert.*;

import org.junit.Test;

import datastructures.symbol_tables.KeyNotInTableException;
import datastructures.symbol_tables.StringST;

public class StringSTTest {
	
	private StringST<CharSequence, String> phonebook = new StringST<CharSequence, String>();

	@Test
	public void testSimpleOps(){
		assertTrue(phonebook.isEmpty());
		assertEquals(phonebook.size(), 0);
		phonebook.put("Marie", "240-098-761");
		assertFalse(phonebook.isEmpty());
		assertEquals(phonebook.size(), 1);
		phonebook.clear();
		assertTrue(phonebook.isEmpty());
		assertEquals(phonebook.size(), 0);
	}
	
	@Test
	public void testAdditions(){
		phonebook.put("Marie", "240-098-7614");
		phonebook.put("Gus", "450-345-2133");
		assertEquals(phonebook.size(), 2);
		phonebook.put("Gus", "567-098-6712"); // Overwrite Guss' phone number
		assertEquals(phonebook.size(), 2);
		phonebook.put("Ashley", "768-098-2331");
		assertEquals(phonebook.size(), 3);
		phonebook.clear();
	}
	
	@Test
	public void testGet(){
		phonebook.put("Marie", "240-098-7614");
		phonebook.put("Gus", "450-345-2133");
		phonebook.put("Ashley", "768-098-2331");
		try {
			assertEquals(phonebook.get("Gus"), "450-345-2133");
			assertEquals(phonebook.get("Ashley"), "768-098-2331");
		} catch(KeyNotInTableException k){
			fail("A KeyNotInTable exception should've NOT been thrown.");
		}
		String noVal = phonebook.get("Jason");
		assertEquals(noVal, null);
		phonebook.clear();
	}
	
	@Test
	public void testKeys(){
		phonebook.put("Marie", "240-098-7614");
		phonebook.put("Gus", "450-345-2133");
		phonebook.put("Ashley", "768-098-2331");
		
		// Have to find every single key
		boolean foundMarie = false, foundGus = false, foundAshley = false;
		Iterable<CharSequence> keyStrings = phonebook.keys();
		for(CharSequence k : keyStrings)
			if(k.equals("Marie")) // Order is not important in a classic symbol table
				foundMarie = true;
			else if(k.equals("Gus"))
				foundGus = true;
			else if(k.equals("Ashley"))
				foundAshley = true;
			else
				fail("Uninserted key returned by keys() method.");
		assertTrue(foundMarie && foundGus && foundAshley);
		phonebook.clear();
	}
	
	@Test
	public void testSoftDeletions(){
		phonebook.put("Marie", "240-098-7614");
		phonebook.put("Gus", "450-345-2133");
		phonebook.put("Ashley", "768-098-2331");
		assertEquals(3, phonebook.size());
		phonebook.delete("Ashley");
		assertEquals(2, phonebook.size());
		phonebook.delete("Ashley"); // Same key again shouldn't alter the ST's size
		assertEquals(2, phonebook.size());
		phonebook.delete("Gus");
		phonebook.delete("Marie");
		assertTrue(phonebook.isEmpty());
	}


}
