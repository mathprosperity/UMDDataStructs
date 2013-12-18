package datastructures.trees;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.InvalidCapacityException;

/**
 * ArrayMinHeap is a MinHeap based on an internal array.
 * @author jason
 * @since October 2013
 */
@SuppressWarnings("unchecked")
public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	private Object[] data;
	private int last;
	private static final int INIT_CAPACITY = 10;
	protected boolean modificationFlag;

	/**
	 * Expands the capacity of the ArrayMinHeap. This method is typically
	 * called by add(T element) when we're trying to add an element in an already
	 * full heap. 
	 * {@link #add(Comparable)}
	 */
	private void expandCapacity(){
		Object[] temp = new Object[2*data.length];
		for(int i = 0; i < data.length; i++)
			temp[i] = data[i];
		data = temp;
	}
	
	/**
	 * Default constructor initializes the data structure with the default
	 * capacity.
	 */
	public ArrayMinHeap(){
		data = new Object[INIT_CAPACITY];
		last = 0;
		modificationFlag = false;
	}
	
	/**
	 * Second constructor initializes the data structure with the provided capacity.
	 * @param capacity The capacity to initialize the ArrayMinHeap with.
	 * @throws InvalidCapacityException If the capacity provided is negative.
	 */
	public ArrayMinHeap(int capacity) throws InvalidCapacityException{
		if(capacity < 0)
			throw new InvalidCapacityException("Invalid capacity provided!");
		data = new Object[capacity];
		last = 0;
		modificationFlag = false;
	}
	
	/** 
	 * Copy constructor initializes the current MinHeap as a carbon
	 * copy of the parameter. 
	 * 
	 * @param other The MinHeap object to base construction of the current object on.
	 */
	public ArrayMinHeap(MinHeap<T> other){
		if(other== null)
			return;
		if(other.size() == 0){
			data = new Object[INIT_CAPACITY];
			last = 0;
		}else
			for(T el: other)
				add(el);
		modificationFlag = false;
	}
	
	/**
	 * Standard equals() method.
	 * @return true if the current object and the parameter object
	 * are equal, with the code providing the equality contract. 
	 */
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap<?>))
			return false;
		MinHeap<?> oheap = null;
		try {
			oheap = (MinHeap<?>)other;
		} catch(ClassCastException cce){
			return false;
		}
		Iterator<?> itthis = iterator();
		Iterator<?> ito = oheap.iterator();
		while(itthis.hasNext())
			if(!itthis.next().equals(ito.next()))
				return false;
		return true;
	}
	
	/**
	 * Returns the maximum node among the two nodes provided as parameters. 
	 * @param indLeft the index of the left child 
	 * @param indRight the index of the right child
	 * @return the index of the maximum child.
	 */
	private int findMinChild(int indLeft, int indRight){
		int retVal;
		if(indLeft >= last)
			retVal = -1;
		else if(indRight >= last)
			retVal = indLeft;
		else{
			if(((T) data[indLeft]).compareTo((T) data[indRight]) > 0)
				retVal = indRight;
			else
				retVal = indLeft;
		}
		return retVal;
	}
	
	@Override
	public void add(T element) {
		// To add an element in the minheap, we add it as the last element,
		// and then we keep swapping it with its parent until the minheap
		// identity is maintained.
		if(last == data.length)
			expandCapacity();
		data[last] = element;
		int current = last, parent = (current - 1) / 2;
		// While you need to switch, switch
		while(((T) data[parent]).compareTo((T)data[current]) > 0){ 
			Object temp = data[current]; 
			data[current] = data[parent];
			data[parent] = temp;
			current = parent;
			parent = (parent - 1) / 2;
		}
		last++;
	}

	@Override
	public T removeMin() throws EmptyHeapException {
		// To remove the minimum element, we remove the root,
		// and then swap the smallest child of the root with the root
		// and keep on doing that until the heap identity is maintained.
		if(data[0] == null)
			throw new EmptyHeapException("removeMin(): Heap is empty!");
		T retVal = (T)data[0];
		data[0] = data[last - 1];
		int current = 0, minChild = findMinChild(1, 2);
		// While you have to switch, switch.
		while(minChild != -1 && ((T) data[minChild]).compareTo((T)data[current]) < 0){
			Object temp = data[current];
			data[current] = data[minChild];
			data[minChild] = temp;
			current = minChild;
			minChild = findMinChild(2*current, 2*current + 1);
		} 
		data[--last] = null;
		return retVal;
	}

	@Override
	public T getMin() throws EmptyHeapException {
		if(data[0] == null)
			throw new EmptyHeapException("getMin(): heap is empty!");
		return (T)data[0];
	}

	@Override
	public int size() {
		return last;
	}

	@Override
	public boolean isEmpty() {
		return last == 0;
	}

	@Override
	public void clear() {
		for(int i = 0; i < last; i++)
			data[i] = null;
		last = 0;		
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayMinHeapIterator<T>();
	}
	
	/**
	 * An implementation of a fail-fast max-first Iterator for MinHeaps.
	 * @author Jason Filippou (jasonfil@cs.umd.edu)	 
	 * @param <T2> the type of element accessed by the Iterator.
	 */
	class ArrayMinHeapIterator<T2 extends Comparable<T2>> implements Iterator<T2>{

		private MinHeap<T2> tempHeap;

		public ArrayMinHeapIterator(){
			tempHeap = new ArrayMinHeap<T2>();
			for(Object el: data)
				if(el != null) // Recall that an array-based Heap might have null references...
					tempHeap.add((T2)el);
			modificationFlag = false;
		}

		@Override
		public boolean hasNext() {
			return !tempHeap.isEmpty();
		}

		@Override
		public T2 next() throws ConcurrentModificationException, NoSuchElementException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): "
						+ "attempted to traverse the heap through an Iterator after extraneous modifications.");
			T2 retVal = null;
			try {
				retVal = tempHeap.removeMin();
			} catch(EmptyHeapException e){
				throw new NoSuchElementException("next(): heap is empty."); 
			}
			return (T2) retVal;
		}

		/**
		 * remove() is an unsupported operation. It does not make sense to provide
		 * a MaxHeap with the ability to remove an arbitrary element. 
		 * @throws UnsupportedOperationException always.
		 */
		@Override
		public void remove() throws UnsupportedOperationException{
			throw new UnsupportedOperationException("Removal of arbitrary elements is not supported for MaxHeaps.");

		}

	}
}
