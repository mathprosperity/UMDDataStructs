/**
 * 
 */
package datastructures.queues;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import datastructures.InvalidCapacityException;

/**A CircularArrayQueue is an implementation of a Queue based on a one-dimensional
 * array which can wrap around. That is, it is possible for the front of the queue
 * to be at index n and the back of the queue at index m, where n > m. Compared to 
 * LinearArrayQueue, this implementation provides for constant time enqueueing and 
 * dequeueing, but it requires more attention when coding.
 *   
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since September 2013
 * 
 *
 */

@SuppressWarnings("unchecked")
public class CircularArrayQueue<T> implements Queue<T> {

	private int front, rear, count;
	private final int DEFAULT_CAPACITY = 50;
	private T[] data;
	protected boolean modificationFlag;

	/* Expanding capacity is tricky in the case of a CircularArrayQueue,
	 * because we have to copy a possibly "wrapped around" queue into a 
	 * "flattened" queue.
	 */
	private void expandCapacity(){
		T[] newData = (T[])(new Object[2*data.length]);
		for(int i = 0; i < count; i++)
			newData[i] = data[(front + i) % data.length];
		front = 0;
		rear = count;
		data = newData;
	}

	/** Default constructor initializes data structure with 
	 * the default capacity of elements.
	 */

	public CircularArrayQueue(){
		data = (T[])(new Object[DEFAULT_CAPACITY]);
		front = rear = count = 0;
		modificationFlag = false;
	}

	/** Non-default constructor initializes data structure with
	 * the provided capacity of elements.
	 * @throws InvalidCapacityException If the capacity provided is negative.
	 */
	public CircularArrayQueue(int capacity) throws InvalidCapacityException{
		if(capacity < 0)
			throw new InvalidCapacityException("Invalid capacity provided!");
		data = (T[])(new Object[capacity]);
		front = rear = count = 0;
		modificationFlag = false;
	}

	/**
	 * Copy constructor initializes the current object with the elements contained
	 * in the parameter object.
	 * @param oqueue The queue to copy the elements from.
	 */
	public CircularArrayQueue(Queue<T> oqueue){
		if(oqueue == null)
			return;
		data = (T[])(new Object[oqueue.size()]);
		for(T el: oqueue)
			enqueue(el);
		front = 0;
		modificationFlag = false;
	}

	/**
	 * Standard equals() method. Checks whether two Queues have the exact same elements
	 * at the same order.
	 * @param other The Object to compare this queue to.
	 * @return true If the two queues are equal as per the contract established above.
	 */
	@Override
	public boolean equals(Object other){
		if(!(other instanceof Queue<?>))
			return false;
		Queue<T> oqueue = (Queue<T>)other;
		if(size() != oqueue.size())
			return false;
		Iterator<T> ito = oqueue.iterator(), itc = iterator();
		while(ito.hasNext())
			if(!ito.next().equals(itc.next()))
				return false;
		return true;
	}


	@Override
	public Iterator<T> iterator() {
		return new CircularArrayQueueIterator<T>();
	}

	class CircularArrayQueueIterator<T2> implements Iterator<T2>{

		private int current;
		boolean calledNextOnce; // This is needed for remove().

		public CircularArrayQueueIterator(){
			current = front; // Simply basing removal possibility on current == front is unsatisfactory, because current == front will be satisfied after n - 1 removals
			calledNextOnce = false; 
			modificationFlag = false;
		}
		@Override
		public boolean hasNext(){
			return current != rear; // We haven't caught up with the rear end of the queue.
		}

		@Override
		public T2 next() throws ConcurrentModificationException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): Attempted to traverse queue after element removal.");
			calledNextOnce = true;
			T retVal = data[current];
			current = (current + 1) % data.length; // May wrap around; it's ok
			return (T2) retVal;
		}

		@Override
		public void remove() throws IllegalStateException{
			
			if(!calledNextOnce)
				throw new IllegalStateException("Need at least one call to next() prior to iterator removal.");
			
			/* When removing an element, all the elements starting from the next element
			 * and up to the rear of the queue need to be shifted one position to the left.
			 * Or, equivalently, all cells, beginning from the one "behind" the one which held the freshly
			 * removed element, need to shift to the right. */
			 			
			// TODO: Test all this...
			for(int i = current; i != rear; i = (i + 1) % data.length)
				if(i == 0)
					data[data.length - 1] = data[i]; // Cover wrap around case.,,
				else
					data[i - 1] = data[i]; 
			//data[(current + 1) % data.length] = null;
			rear = (rear == 0) ? data.length - 1 : rear - 1;
			current = (current == 0) ? data.length - 1 : current - 1; 
			count--;
		}
	};


	/**
	 * Standard toString() method. Returns all the elements of the queue in a Stringified representation.
	 * @return A String-like representation of the object.
	 */
	@Override
	public String toString(){
		String retVal ="[";
		for(int i = front; i < rear; i = (i +1)%data.length){
			retVal += data[i];
			if(i + 1 < rear)// Not second-to-last element
				retVal += ", ";
		}
		retVal += "]";
		return retVal;
	}
	@Override
	public void enqueue(T element) {
		if(count == data.length)
			expandCapacity();
		data[rear] = element;
		rear = (rear + 1)%data.length;
		count++;
		modificationFlag = true;
	}

	@Override
	public T dequeue() throws EmptyQueueException {
		if(isEmpty())
			throw new EmptyQueueException("dequeue(): Queue is empty.");
		T retVal = data[front];
		front = (front + 1) % data.length;
		count--;
		modificationFlag = true;
		return retVal;
	}

	@Override
	public T first() throws EmptyQueueException {
		if(isEmpty())
			throw new EmptyQueueException("first(): Queue is empty.");
		return data[front];
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public boolean isEmpty() {
		return (count == 0);
	}

	@Override
	public void clear() {
		for(int i = front; i < rear; i=(i+1) % data.length)
			data[i] = null;
		System.gc();
		front = rear = count = 0;
		modificationFlag = true;
	}

}
