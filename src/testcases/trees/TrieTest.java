package testcases.trees;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Random;

import datastructures.trees.EmptyTreeException;
import datastructures.trees.Trie;

import org.junit.Test;

public class TrieTest {

	private Trie<Integer> intTrie = new Trie<Integer>(256); // Our trie will work with extended ASCII strings
	private final static String EMPTY_MSG = "Trie is not empty: Should not have thrown an EmptyTreeException.";
	private Random r = new Random();

	@Test
	public void testEmptiness(){
		assertTrue(intTrie.isEmpty());
		assertEquals(0, intTrie.size());
		intTrie.clear();
		assertTrue(intTrie.isEmpty());
	}

	@Test
	public void testDegenerateTrie(){
		// Just one string in this trie.
		try {
			intTrie.insert("sea", 10);
		}catch(Throwable t){
			fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + ".");
		}
		assertFalse(intTrie.isEmpty());
		intTrie.clear();
		assertTrue(intTrie.isEmpty());
	}

	@Test
	public void testInsertionAndLookup(){
		intTrie.insert("sea", 10);
		assertFalse(intTrie.isEmpty());
		try {
			assertEquals(new Integer(10), intTrie.find("sea"));
			intTrie.insert("sea", 20); // should simply change the value
			assertEquals(new Integer(20), intTrie.find("sea"));
			assertEquals(null, intTrie.find("kakapupupipishire")); // This key is not in the trie
			intTrie.insert("death", -10);
			intTrie.insert("deathly hallows", -10);
			assertEquals(intTrie.find("death"), intTrie.find("deathly hallows"));
		} catch (EmptyTreeException e) {
			fail(EMPTY_MSG);
		}		
		intTrie.clear();
		assertTrue(intTrie.isEmpty());
	}

	@Test
	public void testDeletion(){
		intTrie.insert("sea", 10);
		intTrie.delete("sea"); // should nullify the value
		assertTrue(intTrie.isEmpty());		
		intTrie.clear();
	}

	@Test
	public void testKeysWithPrefixSimple(){
		String[] strings = {"she", "shell", "shells"};
		for(String s : strings)
			intTrie.insert(s, r.nextInt()); // Don't really care about the values right now
		Iterable<String> it = intTrie.keysWithPrefix("she");
		/* Every string in "it" should be contained in "strings". */
		assertTrue(checkForAllOccurrences(it, strings));
		intTrie.clear();
	}

	@Test
	public void testKeysWithPrefixComplex(){
		String[] strings = {"she ", "surrounds", "seven", "shells", "shore", "she knows", "sea"};
		for(String s : strings)
			intTrie.insert(s, r.nextInt());
		Iterable<String> it = intTrie.keysWithPrefix("s");
		assertTrue(checkForAllOccurrences(it, strings));
		it = intTrie.keysWithPrefix("she "); // Note the space!
		String[] subArray1 = new String[2], subArray2 = new String[1];
		subArray1[0] = strings[0]; 
		subArray1[1] = strings[5];
		subArray2[0] = strings[3];
		assertTrue(checkForOccurrences(it, subArray1));
		assertFalse(checkForOccurrences(it, subArray2));// "shells" does not have "she " as a prefix.
		intTrie.clear();
	}

	// Check whether all elements in the Iterable<String> iter are also in the String array provided.
	private boolean checkForAllOccurrences(Iterable<String> iter, String[] array){
		int numStrings = 0;
		boolean oneNotFound = false;
		for(String s : iter){
			numStrings++;
			for(String s2: array){
				oneNotFound = true; // guilty until proven innocent
				if(s.equals(s2)){ // found it
					oneNotFound = false;
					break; // search for the next String.
				}
			}
			if(oneNotFound)
				return false;
		}
		return (numStrings == array.length);
	}

	// Same method as before, only not checking for length; in this method, the Iterable<String>
	// need only be a subset of the array (not necessarily proper)
	private boolean checkForOccurrences(Iterable<String> iter, String[] array){
		boolean oneNotFound = false;
		for(String s : iter){
			for(String s2: array){
				oneNotFound = true; // guilty until proven innocent
				if(s.equals(s2)){ // found it
					oneNotFound = false;
					break; // search for the next String.
				}
			}
			if(oneNotFound)
				return false;
		}
		return true;
	}

	@Test
	public void testLongestPrefixOfSimple(){
		intTrie.insert("kakapupupipishire", r.nextInt());
		assertEquals(intTrie.longestPrefixOf("kakapupupipishire"), "kakapupupipishire"); // the same
		intTrie.clear();
	}

	@Test
	public void testLongestPrefixOfComplex(){
		String[] strings = {"shells", "she", "sea", "seas"};
		for(String s : strings)
			intTrie.insert(s, r.nextInt());
		assertEquals("shells", intTrie.longestPrefixOf("shellsoup"));
		assertEquals("seas", intTrie.longestPrefixOf("seashore"));
		intTrie.clear();
	}

	@Test
	public void testKeysThatMatchNoWildcard(){
		intTrie.insert("dog", r.nextInt());
		intTrie.insert("dogs", r.nextInt());
		intTrie.insert("dog leash", r.nextInt());
		Iterator<String> matches = intTrie.keysThatMatch("dog").iterator();
		assertTrue(matches.hasNext());
		assertEquals("dog", matches.next());
		assertFalse(matches.hasNext());
		Iterator<String> noMatches = intTrie.keysThatMatch("dawg").iterator();
		assertFalse(noMatches.hasNext());
		intTrie.clear();
	}

	@Test
	public void testKeysThatMatchWithWildCard(){
		String[] aces = new String[]{"aceH", "aceD", "aceC", "aceS"};
		for(String s : aces)
			intTrie.insert(s, r.nextInt());
		Iterable<String> it = intTrie.keysThatMatch("ace."); // Should match all
		assertTrue(checkForAllOccurrences(it, aces));
		intTrie.clear();
	}

	@Test
	public void insertionStressTest(){
		System.out.println(System.getProperty("user.dir"));
		Scanner sc = null;
		try {
			sc = new Scanner(new File("data/leipzig300k.txt"));
		} catch (FileNotFoundException e) {
			fail("Could not open text file for reading.");
		}
		while(sc.hasNextLine()){ 
			for(String word: sc.nextLine().split(" ")){ // For every word
				try{
					intTrie.insert(word, r.nextInt());
				}catch(Throwable t){
					fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when inserting word:" + word);
				}
			}
			
		}
		intTrie.clear();
		sc.close();
	}

	@Test
	public void deletionStressTest(){
		Scanner sc = null;
		try {
			sc = new Scanner(new File("data/leipzig300k.txt"));
		} catch (FileNotFoundException e) {
			fail("Could not open text file for reading.");
		}
		while(sc.hasNextLine()){ 
			for(String word: sc.nextLine().split(" ")){ // For every word
				try{
					intTrie.insert(word, r.nextInt());
				}catch(Throwable t){
					fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when inserting word:" + word);
				}
			}	
		}
		// Read the file again, delete all Strings from the trie.
		try {
			sc = new Scanner(new File("data/leipzig300k.txt"));
		} catch (FileNotFoundException e) {
			fail("Could not open text file for reading.");
		}
		while(sc.hasNextLine()){
			for(String word: sc.nextLine().split(" ")){ // For every word
				try{
					intTrie.delete(word);
				}catch(Throwable t){
					fail("Caught a " + t.getClass() + " with message: " + t.getMessage() + " when deleting word:" + word);
				}
			}
		}
		System.out.println(intTrie.size());
		assertTrue(intTrie.isEmpty());
		intTrie.clear();
	}

}
