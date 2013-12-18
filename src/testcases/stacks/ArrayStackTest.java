package testcases.stacks;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Scanner;
/* The funny thing about the test cases present in this file is that I'm actually
 * gonna copy and paste them verbatim over the other test cases, then change the types of the objects,
 * and the tests should still be passing. The implementation differs, but the interface shouldn't!
 * Polymorphism through interfaces will be used to call the methods common in every stack.
 */











import static org.junit.Assert.*;

import org.junit.Test;

import datastructures.stacks.ArrayListStack;
import datastructures.stacks.ArrayStack;
import datastructures.stacks.EmptyStackException;
import datastructures.stacks.LinkedStack;
import datastructures.stacks.Stack;

public class ArrayStackTest {

	// The choice of having those two objects global to the methods is questionable,
	// because a jUnit test that doesn't clean up after itself (i.e empty the stacks) could leave them
	// hanging. If we believe that we can be careful enough with that, it does lead to
	// cleaner code, because you don't have to re-declare the two Stack objects
	// inside every single jUnit test case.
	
	private Stack<String> stringStack = new ArrayStack<String>();
	private Stack<Integer> integerStack = new ArrayStack<Integer>();
	private String[] strings = {"Oranges", "Blueberries", "Apples", "Cherries", "Pears"};
	
	@Test
	public void testCopyConstructorAndEquals(){
		for(int i = 0; i < 20; i++)
			integerStack.push(i);
		
		// First, check whether copy construction works well for Stacks of the
		// type tested in this jUnit test.
		Stack<Integer> integerStack2 = new LinkedStack<Integer>(integerStack);
		assertEquals(integerStack, integerStack2);
		assertEquals(integerStack2, integerStack);
		
		// Now, check if it works well across Stacks.
		
		// Case 1: Copying into an ArrayListStack, testing for equality.
		Stack<Integer> arrayListStackCopy = new ArrayListStack<Integer>(integerStack);
		assertEquals(arrayListStackCopy, integerStack);
		assertEquals(integerStack, arrayListStackCopy); // Should work both ways
		
		// Case 2: Copying into an ArrayStack, testing for equality.
		Stack<Integer> arrayStackCopy = new ArrayStack<Integer>(integerStack);
		assertEquals(arrayStackCopy, integerStack);
		assertEquals(integerStack, arrayStackCopy); // Should work both ways
		
		integerStack.clear();
		stringStack.clear();
	}
	
	@Test
	public void simpleTestPush1() throws EmptyStackException{
		stringStack.push("thing.");
		assertEquals(stringStack.pop(), "thing.");
	}
	
	@Test
	public void simpleTestPush2(){
		stringStack.push("Two");
		stringStack.push("One");
		assertEquals(stringStack.size(), 2);
		stringStack.clear();
	}
	@Test
	public void simpleTestPush3() {
		for(int i = 0; i < 50; i++){
			stringStack.push(Integer.toString(i));
		}
		// An additional push should work just fine (useful for ArrayStacks, where the
		// original capacity is hardcoded to 50).
		try {
			stringStack.push("another");
		} catch(IndexOutOfBoundsException i){
			fail();
		}
		// Any other exception should be reported to the output.
		stringStack.clear();
	}
	
	@Test
	public void simpleTestPop() throws EmptyStackException {
		for(int i = 0; i < 100; i++)
			integerStack.push(i);
		for(int i = 0; i < 100; i++)
			assertEquals(integerStack.pop(), new Integer(100 - i - 1)); // Interesting error message if I remove the Integer constructor call
		assertTrue(integerStack.empty());
	}
	
	@Test 
	public void simpleTestPop2(){
		try {
			integerStack.pop();
		} catch(EmptyStackException e){
			assertTrue(true);
		}
	}
	
	@Test 
	public void testEmpty(){
		assertTrue(integerStack.empty() && stringStack.empty());
	}
	
	@Test
	public void testToString(){
		String s = "thing. horrible a did man old The";
		Scanner sc = new Scanner(s);
		while(sc.hasNext())
			stringStack.push(sc.next());
		assertEquals("|The|\n|old|\n|man|\n|did|\n|a|\n|horrible|\n|thing.|\n___", stringStack.toString());
		stringStack.clear();
		sc.close();
	}
	
	@Test
	public void testClear(){
		integerStack.push(9);
		integerStack.push(-1);
		integerStack.clear();
		assertTrue(integerStack.empty());
	}
	
	@Test
	public void testIterator(){
		Iterator<String> it1 = stringStack.iterator();
		try {
			it1.remove();
			fail("Call should've thrown an IllegalStateException.");
		} catch(IllegalStateException ile){}
		catch(Throwable t){
			fail("Threw a " + t.getClass() + " with message " + t.getMessage() + 
					" instead of a " + new IllegalStateException().getClass()+ ".");
		}
		for(String s: strings)
			stringStack.push(s);
		try {
			it1.next();
			fail("Call should've thrown a ConcurrentModificationException.");
		} catch(ConcurrentModificationException cme){
			// good
		} catch(Throwable t){ // not good
			fail("Threw a " + t.getClass() + " with message " + t.getMessage() + " instead of a " + new ConcurrentModificationException().getClass()+ ".");
		}
		it1 = stringStack.iterator(); // Resetting iterator.
		it1.next();
		try {
			it1.remove(); // this should not throw.
		} catch(IllegalStateException ile){
			fail("Call should not have thrown an IllegalStateException.");
		}
		int currInd = 1; // Beginning from the second element, because we removed the first one previously.
		while(it1.hasNext()) // should be yielding the elements in the linear order.
			assertEquals(it1.next(), strings[currInd++]);
		stringStack.clear();
	}
}
