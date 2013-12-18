package datastructures.queues;

import java.util.Iterator;

import datastructures.trees.EmptyHeapException;
import datastructures.trees.LinkedMinHeap;
import datastructures.trees.MinHeap;

import java.util.ArrayList;
/**
 * HeapPriorityQueue is a priority queue implemented as a heap. Adhering
 * to the specifications introduced by the book, we achieve a solution to the
 * FIFO ordering problem by creating our own Comparable node class and overriding
 * the compareTo() method.
 * 
 * We choose to use encapsulation of a MinHeap instead of inheritance 
 * of a MinHeap in this class, because inheritance would require us to 
 * make the data type held by the container Comparable. Instead, in the current
 * implementation, we do not require of T to be Comparable; its inner wrapper
 * will be, though.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The Type held by the container.
 */
public class HeapPriorityQueue<T> implements PriorityQueue<T>{

	private int orderCounter;
	private MinHeap<PriorityQueueNode<T>> data;

	public HeapPriorityQueue(){
		orderCounter = 0;
		data = new LinkedMinHeap<PriorityQueueNode<T>>(); // Any MinHeap can be used.
	}

	@Override
	public void enqueue(T element) {
		throw new UnsupportedOperationException("enqueue(T) is not valid for PriorityQueue objects.");
	}

	@Override
	public void enqueue(T element, int priority) {
		data.add(new PriorityQueueNode<T>(element, priority));
	}


	@Override
	public T dequeue() throws EmptyQueueException {
		try {
			return data.removeMin().data;
		}catch(EmptyHeapException e){
			throw new EmptyQueueException("dequeue(): Queue is empty!");
		}
	}

	@Override
	public T first() throws EmptyQueueException {
		// TODO Auto-generated method stub
		try {
			return data.getMin().data;
		}catch(EmptyHeapException e){
			throw new EmptyQueueException("first(): Queue is empty!");
		}
	}

	@Override
	public Iterator<T> iterator() {
		
		// We will do this in a really dumb way. Like, really dumb.
		// A more efficient iterator would require
		// re-declaring heaps as iterables, and we're not gonna
		// do that, because it introduces significant complications.
		
		// First, make a carbon copy of our contained heap.
		MinHeap<PriorityQueueNode<T>> temp = new LinkedMinHeap<PriorityQueueNode<T>>(data);
		
		// Then, collect every element of the contained heap and put them
		// in an ArrayList.
		ArrayList<T> collection = new ArrayList<T>();
		while(!data.isEmpty()){
			try {
				collection.add(data.removeMin().data);
			} catch (EmptyHeapException e) {
				// Dummy catchblock to make removeMin() happy
			}
		}
		
		// Finally, restore the original MinHeap and return an 
		// iterator to the ArrayList.
		data = temp; 
		return collection.iterator();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return data.size() == 0;
	}

	@Override
	public void clear() {
		data.clear();
		orderCounter = 0;
	}
	
	/**
	 * A PriorityQueueNode is a Comparable type which is used to wrap around
	 * the (data, priority) pairs. Its overriding of the compareTo() method
	 * allows the contained MinHeap in the priority queue to disambiguate between
	 * the same priority elements, thus establishing a strict ordering in the heap,
	 * such that the root is always uniquely defined.
	 * 
	 * @author Jason Filippou (jasonfil@cs.umd.edu)
	 *
	 * @param <T> The Type of element contained in the PriorityQueueNode.
	 */
	@SuppressWarnings("hiding")
	private class PriorityQueueNode<T> implements Comparable<PriorityQueueNode<T>>{

		private T data;
		private int priority;
		private int orderInserted;

		public PriorityQueueNode(T data, int priority){
			this.data = data;
			this.priority = priority;
			orderInserted = orderCounter++;
		}

		public PriorityQueueNode(T data){
			this(data, -1);
		}

		public PriorityQueueNode(){
			this(null);
		}

		@Override
		public int compareTo(PriorityQueueNode<T> o) {
			// Remember that a numerically smaller priority
			// is actually considered larger in priority
			// queue terms. Also recall that we are using a 
			// MinHeap, so the smallest elements ascend to the top,
			// not the largest.
			if(priority < o.priority)
				return -1;
			else if(priority > o.priority)
				return 1;
			else {
				if(orderInserted < o.orderInserted)
					return -1;
				else
					return 1;
			}
		}

	}
}
