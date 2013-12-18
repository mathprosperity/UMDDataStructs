/**
 * 
 */
package datastructures.queues;

/**A PriorityQueue is an extension of a Queue. Instead of classical
 * FIFO processing, a PriorityQueue inserts elements with a higher priority
 * first, where "higher" is typically interpreted as "lower" in the arithmetic
 * sense, e.g 1 is "higher" priority than 2. Elements with the same priority
 * are inserted in a FIFO fashion.
 * 
 *  The PriorityQueue<T> interface extends the Queue<T> interface by
 *  overriding the enqueue() method to allow for the addition of an extra
 *  parameter, which encodes the priority of the node in the data structure.
 * 
 * @param T The type of element held by the container.
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * @since October 2013
 *
 */
public interface PriorityQueue<T> extends Queue<T> {

	/**
	 * Enqueue the element in the PriorityQueue.
	 * @param element The element to enqueue.
	 * @param priority The priority of the element that will be enqueued.
	 */
	public void enqueue(T element, int priority);
}
