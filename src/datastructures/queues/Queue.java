package datastructures.queues;

/**
 * An interface for queues. Queues allow for enqueueing  an element in the back,
 * dequeueing it from the front, as well as querying for the size of the queue,
 * the first element, whether the queue is empty, and clearing the queue. 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @since September 2013
 * @param <T> The type contained by this Queue.
 */
public interface Queue<T> extends Iterable<T> {

	/** Inserts an element in the back of the queue.
	 * 
	 * @param element The element to be inserted in the queue.
	 */
	public void enqueue(T element);
	
	/**
	 * Removes and returns the element at the front of the queue.
	 * @return The element at the front of the queue.
	 * @throws EmptyQueueException If the queue is empty.
	 */
	public T dequeue() throws EmptyQueueException;
	
	/**
	 * Returns but doesn not remove the first element from the front of the queue.
	 * @return The element at the front of the queue.
	 * @throws EmptyQueueException If the queue is empty.
	 */
	public T first() throws EmptyQueueException;
	
	/**
	 * Returns the number of elements currently in the queue.
	 * @return The number of elements currently in the queue.
	 */
	public int size();
	
	/**
	 * Queries the queue for emptiness.
	 * @return true if the queue is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Clears the queue.
	 */
	public void clear();
}
