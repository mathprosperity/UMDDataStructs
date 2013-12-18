package datastructures.lists;
import java.util.Iterator;

/***
 * A LinearList is a linear ADT which inserts elements in O(n), deletes them in O(n)
 * and access them in O(n). Elements can only be added to the front or the back. Note
 * that this interface does not require that the implementation is a LinkedList, even
 * though that would be what makes most sense. It does not even specify whether the LinearList,
 * if linked, will have single or double links per node.
 * 
 * It is a requirement that the type T is Comparable, or else we will not be able to 
 * implement methods such as contains() and remove(). The interface itself extends the Iterable
 * interface, which means that we can use foreach() statements to easily access its elements.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * @since September 2013 
 * @param <T> The type of object that the LinearList will hold.
 */

public interface List<T> extends Iterable<T>{

	/**
	 * Insert an element at the start of the list.
	 * @param element The element to be inserted.
	 */
	public void pushFront(T element);
	
	/**
	 * Insert an element at the end of the list.
	 * @param element The element to be inserted.
	 */
	public void pushBack(T element);
	
	/**
	 * Get the first element of the list. This method does *not* remove this element
	 * (call "remove" to remove elements).
	 * @return A copy of the element at the top of the list.
	 * @throws EmptyListException If the list is empty.
	 */
	public T getFirst() throws EmptyListException;
	
	/**
	 * Get the last element of the list. This method does *not* remove this element
	 * (call "remove" to remove elements).
	 * @return A copy of the element at the top of the list.
	 * @throws EmptyListException
	 */
	public T getLast() throws EmptyListException;

	/**
	 * Get the element at the specified index of the list.
	 * @param index The index of the element to return.
	 * @return The element at the index-th position.
	 * @throws EmptyListException If the list is empty upon calling.
	 * @throws IllegalListAccessException If the index is invalid in the current list's context.
	 */
	public T get(int index) throws EmptyListException, IllegalListAccessException;
	
	/**
	 * Determines whether the list contains at least one occurrence of element.
	 * @param element the element to be searched.
	 * @return true if element is in list.
	 */
	public boolean contains(T element);
	
	/**
	 * Removes *one* instance of element from the list. 
	 * @param element the element to be removed.
	 * @return true if the element was removed.
	 */
	public boolean remove(T element); 
	
	/** Removes the element at position "index" of the list.
	 * @param index The index of the element to be removed.
	 * @throws IllegalListAccessException if the index is below 0 or above the list's length.
	 */
	
	public void remove(int index) throws IllegalListAccessException;
	
	/**
	 * Removes *all* instances of element from the list.
	 * @param element The element to be removed.
	 * @return true if the element was removed at least once.
	 */
	public boolean removeAll(T element);
	
	/**
	 * Returns the number of elements in the list.
	 * @return the number of elements in the list.
	 */
	public int size();
	
	
	/**
	 * Empties the list.
	 */
	public void clear();
	
	/**
	 * Queries the list for emptiness.
	 * @return true if the list is empty.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns a "fail-fast" iterator for the Iterable List.
	 * Fail-fast iterators will throw a ConcurrentModificationException
	 * if the collection is modified by means other than the Iterator.remove()
	 * method during scanning of the collection.
	 * 
	 * @return an Iterator over the elements of the collection, in proper (linear) order.
	 */
	public Iterator<T> iterator();
}
