package datastructures.trees;
import java.util.Iterator;
/**
 * MaxHeaps are complete binary search trees whose node contents are always larger than
 * the contents of their children nodes. MaxHeaps inherit neither the Tree interface nor
 * the BinarySearchTree interface. This is because they are much simpler data structures
 * and it doesn't make sense for us to provide definitions for operations such as particular
 * tree traversals, or retrieving the minimum element from the tree. We thus make heaps
 * "atomic" tree structures in the inheritance sense.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The Comparable type of Object that will be held by the MaxHeap.
 */
public interface MaxHeap<T extends Comparable<T>> extends Iterable<T>{

	/**
	 * Add an element in the MaxHeap.
	 * @param element The element to add to the MaxHeap.
	 */
	public void add(T element);
	
	/**
	 * Removes and returns the maximum element from the MaxHeap.
	 * @return The maximum element of the MaxHeap.
	 * @throws EmptyHeapException if the MaxHeap is empty. 
	 */
	public T removeMax() throws EmptyHeapException;

	/**
	 * Returns the maximum element of the MaxHeap.
	 * @return The maximum element of the MaxHeap.
	 * @throws EmptyHeapException If the MaxHeap is empty.
	 */
	public T getMax() throws EmptyHeapException;

	/**
	 * Returns the number of elements in the MaxHeap.
	 */
	public int size();
	
	/**
	 * Queries the MaxHeap for emptiness.
	 * @return true if the MaxHeap is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Clears the MaxHeap of all elements.
	 */
	public void clear();
	
	/**
	 * MaxHeaps will provide for an Iterator which will
	 * provide the intuitive max-min traversal.
	 * @return A {@link java.util.Iterator} over the elements of the collection.
	 */
	public Iterator<T> iterator();
	
}
