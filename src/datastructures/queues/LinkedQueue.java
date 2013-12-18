package datastructures.queues;
import java.util.Iterator;

import datastructures.lists.*;


/**
 * A LinkedQueue is a queue which uses a DoublyLinkedList as the underlying implementation.
 * Such a queue never needs data resizing and can grow as long as the system allows it to.
 * We use a DoublyLinkedList instead of a LinkedList for the underlying implementation such that we have 
 * constant time enqueueings and dequeuings (a singly linked list would provide linear time for one
 * of the two operations).
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The type of Object contained within the LinkedQueue.
 */

public class LinkedQueue<T> implements Queue<T>{

	private DoublyLinkedList<T> data;

	/**
	 * Constructor simply initializes the data structure.
	 */
	public LinkedQueue(){
		data = new DoublyLinkedList<T>();
	}

	/**
	 * Copy constructor initializes the current object as a carbon copy
	 * of the parameter object.
	 * @param oqueue The queue to copy the elements from.
	 */
	public LinkedQueue(Queue<T> oqueue){
		/* Since we allow copying from any type that implements the
		 * Queue<T> interface, we cannot straightforwardly do a downcasting of the parameter
		 * and call the copy constructor for DoublyLinkedLists, because the parameter is not
		 * guaranteed to be a DoublyLinkedList (the downcasting is unsafe).
		 */
		data = new DoublyLinkedList<T>();
		for(T el: oqueue)
			enqueue(el);
	}
	
	/**
	 * Standard equals() method. Checks whether two Queues have the exact same elements
	 * at the same order.
	 * @param other The Object to compare this queue to.
	 * @return true If the two queues are equal as per the contract established above.
	 */
	@Override
	public boolean equals(Object other){
		if(other == null)
			return false;
		if(!(other instanceof Queue<?>))
			return false;
		@SuppressWarnings("unchecked")
		Queue<T> oqueue = (Queue<T>)other;
		if(size() != oqueue.size())
			return false;
		Iterator<T> ito = oqueue.iterator(), itc = iterator();
		while(ito.hasNext())
			if(!ito.next().equals(itc.next()))
				return false;
		return true;
	}

	/** Overriding of the iterator() method, required for Iterables.
	 * In this implementation of iterator(), the accessors of the method returned may throw
	 * a ConcurrentModificationException if the user tries to further traverse the queue
	 * after removing an element. This is similar to what the Java Standard Library Collections are
	 * doing and it's there because typically removal of an element leaves it in an inconsistent state
	 * w.r.t further traversing its elements.
	 * @return An Iterator<T> which can be used to scan the elements in the queue in a linear order.
	 */
	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
	
	/**
	 * Standard toString() method. Returns all the elements of the queue in a Stringified representation.
	 * @return A String-like representation of the object.
	 */
	@Override 
	public String toString(){
		return data.toString(); // We use the exact same Stringified representation for Queues.
	}

	@Override
	public void enqueue(T element) {
		data.pushBack(element);
	}

	@Override
	public T dequeue() throws EmptyQueueException {
		T retVal = null;
		try {
			retVal = data.getFirst();
			data.remove(0);
		} catch (EmptyListException e) {
			throw new EmptyQueueException("dequeue(): Queue is empty.");
		} catch(IllegalListAccessException exc){
			// Dummy catchblock, since this type of exception will never be generated.
		}
		return retVal;
	}

	@Override
	public T first() throws EmptyQueueException {
		try {
			return data.getFirst();
		}catch(EmptyListException exc){
			throw new EmptyQueueException("first(): Queue is empty.");
		}
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return (data.size() == 0);
	}

	@Override
	public void clear() {
		data.clear();
	}

}
