package datastructures.other;

/**
 * A <tt>Bag</tt> is one of the most basic ADTs. It only supports adding
 * elements and not accessing them individually. Bags are assumed to have infinite
 * capacity. From the point of view of the user of this class, the order
 * in which elements are accessed does not matter, as long as two conditions
 * are satisfied:
 * 
 * (1) Every iteration will exhaustively cover the elements in the bag.
 * 
 * (2) Every element is accessed exactly once per iteration.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The type of element contained in a Bag.
 * 
 * @since October 2013
 */
public interface Bag<T> extends Iterable<T>{
	
	/**
	 * Add an element in the Bag.
	 * 
	 * @param element The element to add to the bag.
	 */
	public void add(T element);

	/**
	 * Clear the Bag of all elements.
	 */
	public void clear();
	
	
	/**
	 * Query the Bag for emptiness.
	 * 
	 * @return true If the Bag is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Query the Bag for its size.
	 * 
	 * @return The number of elements in the Bag.
	 */
	public int size();
	
	/**
	 * Query the bag for the existence of a particular element.
	 * @param element The element to search for.
	 * @return true if the element is there.
	 */
	public boolean contains(T element);
	
}
