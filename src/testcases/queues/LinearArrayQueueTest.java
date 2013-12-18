package testcases.queues;

import static org.junit.Assert.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import datastructures.queues.*;

import org.junit.Test;

public class LinearArrayQueueTest {

	private int DEFAULT_CAPACITY = 20;
	private Queue<Integer> integerQueue = new LinearArrayQueue<Integer>();
	private Queue<String> stringQueue = new LinearArrayQueue<String>();
	private String[] veggies = {"Lettuce", "Cucumbers", "Apricots", "Olives", "Tomatoes", "Carrots"};
	
	@Test
	public void testExpandCapacity(){
		for(int i = 0; i < DEFAULT_CAPACITY; i++)
			integerQueue.enqueue(i);
		try {
			integerQueue.enqueue(200); // Just a random int, doesn't matter
			assertTrue(true);
		} catch(IndexOutOfBoundsException exc){// Shouldn't be throwing that
			fail("Should not be throwing an IndexOutOfBounds exception");
		}
		integerQueue.clear();
	}
	
	@Test
	public void testCopyConstructorAndEquals(){
		for(String s: veggies)
			stringQueue.enqueue(s);
		
		// Phase 1: Compare equality between queues of the same type.
		Queue<String> linearArrayQueueCopy = new LinearArrayQueue<String>(stringQueue);
		assertEquals(linearArrayQueueCopy, stringQueue);
		
		// Phase 2: Copy construct a CircularArrayQueue from a LinearArrayQueue.
		Queue<String> circularArrayQueueCopy = new CircularArrayQueue<String>(stringQueue);
		assertEquals(circularArrayQueueCopy, stringQueue);
		
		// Phase 3: Copy construct a LinkedQueue from a LinearArrayQueue.
		Queue<String> linkedQueueCopy = new LinkedQueue<String>(stringQueue);
		assertEquals(linkedQueueCopy, stringQueue);
		stringQueue.clear();
	}
	
	@Test
	public void testFirst(){
		stringQueue.enqueue("Soda");
		try {
			assertEquals(stringQueue.first(), "Soda");
			assertEquals(stringQueue.size(), 1); // Size should not've been decremented.
		} catch (EmptyQueueException e) { // This should not've been thrown
			fail("EmptyQueueException should not've been thrown.");
		}
		stringQueue.clear();
	}
	
	@Test
	public void testDequeue(){
		stringQueue.enqueue("Soda");
		try {
			assertEquals(stringQueue.dequeue(), "Soda");
			assertEquals(stringQueue.size(), 0); // Size SHOULD've been decremented.
		} catch (EmptyQueueException e) { // This should not've been thrown
			fail("EmptyQueueException should not've been thrown.");
		}
		stringQueue.clear();
		
		for(int i = 0; i < 100; i++)
			integerQueue.enqueue(i);
		
		for(int i = 0; i < 100; i++)
			try {
				integerQueue.dequeue();
			} catch (EmptyQueueException e) {
				fail("Empy queue exception should not've been thrown (i = " + i + ")");
			}
		assertTrue(integerQueue.isEmpty());
		integerQueue.clear();
	}
	
	@Test
	public void testEnqueue(){
		assertEquals(integerQueue.size(), 0);
		for(int i = 0; i < 100; i++)
			integerQueue.enqueue(i);
		assertEquals(integerQueue.size(), 100);
		try {
			assertEquals(integerQueue.first(), new Integer(0));
		} catch(EmptyQueueException exc){
			fail("EmptyQueueException should not've been thrown.");
		}
		integerQueue.clear();
	}
	
	@Test
	public void testIterator(){
		Iterator<String> its = stringQueue.iterator();
		for(String s: veggies)
			stringQueue.enqueue(s);
		try {
			its.next();
			fail("A ConcurrentModificationException should've been thrown.");
		} catch(ConcurrentModificationException cme){}
		catch(Throwable t){
			fail("Instead of a ConcurrentModificationException, a " + t.getClass() + 
					" was thrown, with message: " + t.getMessage() + ".");
		}
		its = stringQueue.iterator(); // reset
		int counter = 0;
		while(its.hasNext()) // verifying correct default output here
			assertEquals(its.next(), veggies[counter++]);

		for(int i = 10; i < 20; i++)
			integerQueue.enqueue(i);
		Iterator<Integer> iti = integerQueue.iterator();
		try {
			iti.remove();
			fail("An IllegalStateException should've been thrown at this point.");
		} catch(IllegalStateException ile){	}
		catch(Throwable t){
			fail("Instead of an IllegalStateException, a " + t.getClass() + 
					" was thrown, with message: " + t.getMessage() + ".");
		}
		iti.next();
		try{
			iti.remove();
		} catch(IllegalStateException ile){ // This should be thrown
			fail("An IllegalStateException should not have been thrown from this call.");
		}

		// Let's now check to see if removal of elements via iterators
		// results in queue consistency.
		its = stringQueue.iterator();
		its.next();
		its.remove();
		for(int i = 1; i < veggies.length; i++) // Starting from 1 because we removed one element.
			assertEquals(its.next(), veggies[i]);
		integerQueue.clear();
		stringQueue.clear();
	}
	
	@Test
	public void testEmpty(){
		assertTrue(stringQueue.isEmpty() && integerQueue.isEmpty());
		integerQueue.enqueue(3);
		assertFalse(integerQueue.isEmpty());
		integerQueue.clear();
	}
	
	@Test
	public void testToString(){
		for(String s: veggies)
			stringQueue.enqueue(s);
		assertEquals("[Lettuce, Cucumbers, Apricots, Olives, Tomatoes, Carrots]", stringQueue.toString());
		stringQueue.clear();
	}
	
}
