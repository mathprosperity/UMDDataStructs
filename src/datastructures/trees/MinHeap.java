package datastructures.trees;
import java.util.Iterator;

/**
 * MinHeaps are complete binary search trees whose node contents are always smaller than
 * the contents of their children nodes. MinHeaps inherit neither the Tree interface nor
 * the BinarySearchTree interface. This is because they are much simpler data structures
 * and it doesn't make sense for us to provide definitions for operations such as particular
 * tree traversals, or retrieving the maximum element from the tree. We thus make heaps
 * "atomic" tree structures in the inheritance sense.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The Comparable type of Object that will be held by the MinHeap.
 */
public interface MinHeap<T extends Comparable<T>> extends Iterable<T>{

	/**
	 * Add an element in the MinHeap.
	 * @param element The element to add to the MinHeap.
	 */
	public void add(T element);
	
	/**
	 * Removes and returns the maximum element from the MinHeap.
	 * @return The maximum element of the MinHeap.
	 * @throws EmptyHeapException if the MinHeap is empty. 
	 */
	public T removeMin() throws EmptyHeapException;

	/**
	 * Returns the maximum element of the MinHeap.
	 * @return The maximum element of the MinHeap.
	 * @throws EmptyHeapException If the MinHeap is empty.
	 */
	public T getMin() throws EmptyHeapException;

	/**
	 * Returns the number of elements in the MinHeap.
	 */
	public int size();
	
	/**
	 * Queries the MinHeap for emptiness.
	 * @return true if the MinHeap is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Clears the MinHeap of all elements.
	 */
	public void clear();
	
	/**
	 * MinHeaps are endowed with fail-fast Iterators which return the elements
	 * in ascending order.
	 * @return A fail-fast Iterator<T> which loops through the MinHeap's contents in ascending
	 * order.
	 */
	public Iterator<T> iterator();
}
