package testcases.queues;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import datastructures.queues.EmptyQueueException;
import datastructures.queues.PriorityQueue;
import datastructures.queues.*;
public class HeapPriorityQueueTest {

	/* We have copied the entire jUnit test case file from
	 * LinearPriorityQueue verbatim, replacing the object
	 * being tested into a HeapPriorityQueue. If everything's ok
	 * with the HeapPriorityQueue class, then the same tests should
	 * be passing.
	 */
	private PriorityQueue<String> greekPublicSectorQueue = 
			new HeapPriorityQueue<String>();
	
	@Test
	public void testSimpleConstructorAndSize(){
		assertTrue(greekPublicSectorQueue.isEmpty());
		assertEquals(greekPublicSectorQueue.size(), 0);
		greekPublicSectorQueue.enqueue("ASD", 10);
		assertEquals(greekPublicSectorQueue.size(), 1);
		greekPublicSectorQueue.clear();
		assertTrue(greekPublicSectorQueue.isEmpty());
		assertEquals(greekPublicSectorQueue.size(), 0);
	}
	
	@Test
	public void testOrderOfInsertedElements(){
		greekPublicSectorQueue.enqueue("Filippou", 2);
		greekPublicSectorQueue.enqueue("Vasilakopoulos", 2);
		assertEquals(greekPublicSectorQueue.size(), 2);
		try {
			assertEquals(greekPublicSectorQueue.first(), "Filippou");
		} catch (EmptyQueueException e) {
			fail("EmptyQueueException should not've been thrown.");
		}
		assertEquals(greekPublicSectorQueue.size(), 2);
		try {
			assertEquals(greekPublicSectorQueue.dequeue(), "Filippou");
		} catch (EmptyQueueException e) {
			fail("EmptyQueueException should not've been thrown.");
		}
		assertEquals(greekPublicSectorQueue.size(), 1);
		try {
			assertEquals(greekPublicSectorQueue.first(), "Vasilakopoulos");
		} catch (EmptyQueueException e) {
			fail("EmptyQueueException should not've been thrown.");
		}
		greekPublicSectorQueue.enqueue("Papandreou", 1);
		greekPublicSectorQueue.enqueue("Mitsotakis", 1);
		try {
			assertNotEquals(greekPublicSectorQueue.first(), "Vasilakopoulos"); // No longer the first
			assertEquals(greekPublicSectorQueue.dequeue(), "Papandreou");
			assertEquals(greekPublicSectorQueue.dequeue(), "Mitsotakis");
			assertEquals(greekPublicSectorQueue.dequeue(), "Vasilakopoulos");
		}catch(EmptyQueueException e){
			fail("EmptyQueueException should not've been thrown.");
		}
		assertTrue(greekPublicSectorQueue.isEmpty());
		greekPublicSectorQueue.clear();
	}
	
	@Test
	public void testIterator(){
		String[] strings = {"Karathodori", "Stergiou", "Tasou", "Pipinis", "Papandreou", "Mitsotakis"};
		for(int i = 0; i < strings.length; i++)
			greekPublicSectorQueue.enqueue(strings[i], strings.length - 1 - i);
		Iterator<String> it = greekPublicSectorQueue.iterator();
		assertTrue(it.hasNext());
		/*assertEquals(it.next(), "Mitsotakis");
		assertEquals(it.next(), "Papandreou");
		assertEquals(it.next(), "Pipinis");
		*/try {
			for(int i = strings.length - 1; i > -1; i--)
				assertEquals(it.next(), strings[i]);
		}catch(NoSuchElementException e){
			fail("A NoSuchElementException should not've been thrown.");
		}
		assertFalse(it.hasNext());
		greekPublicSectorQueue.clear();
	}

}
