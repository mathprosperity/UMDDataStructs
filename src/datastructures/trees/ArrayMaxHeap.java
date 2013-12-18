package datastructures.trees;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.InvalidCapacityException;

/**
 * An implementation of a MaxHeap using an underlying array. It is, in some ways, much easier
 * to implement than a LinkedMaxHeap, but it might lead to significant memory waste
 * (2^d - 1 nodes, where d is the depth of the heap). 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The Comparable type of object held by this MaxHeap.
 */
@SuppressWarnings("unchecked")
public class ArrayMaxHeap<T extends Comparable<T>> implements MaxHeap<T> {

	private Object[] data; // with castings necessary, because of the Comparability of T
	private int last;
	private static final int INIT_CAPACITY = 10;
	protected boolean modificationFlag;

	/**
	 * Doubles the capacity of the ArrayMaxHeap.
	 */
	private void expandCapacity(){
		Object[] newData = (T[])(new Object[2*data.length]); 
		for(int i = 0; i < data.length; i++)
			newData[i] = data[i];
		data = newData;
	}

	/**
	 * Creates an empty ArrayMaxHeap with the default capacity.
	 */
	public ArrayMaxHeap(){
		data = new Object[INIT_CAPACITY];
		last = 0;
		modificationFlag = false;
	}

	/**
	 * Creates an empty ArrayMaxHeap with the provided capacity.
	 * @param capacity The initial capacity of the ArrayMaxHeap.
	 * @throws InvalidCapacityException If the capacity provided is negative.
	 */
	public ArrayMaxHeap(int capacity) throws InvalidCapacityException{
		if(capacity < 0)
			throw new InvalidCapacityException("Invalid capacity provided!");
		data = new Object[capacity];
		last = 0;
		modificationFlag = false;
	}
	
	/**
	 * Copy constructor. Creates the current object as a carbon copy of the parameter.
	 * @param other the parameter MaxHeap to base the current object's construction on.
	 */
	public ArrayMaxHeap(MaxHeap<T> other){
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
		if(other == null || !(other instanceof MaxHeap<?>))
			return false;
		MaxHeap<?> oheap = null;
		try {
			oheap = (MaxHeap<?>)other;
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
	private int findMaxChild(int indLeft, int indRight){
		int retVal;
		if(indLeft >= last)
			retVal = -1;
		else if(indRight >= last)
			retVal = indLeft;
		else{
			if(((T) data[indLeft]).compareTo((T) data[indRight]) < 0)
				retVal = indRight;
			else
				retVal = indLeft;
		}
		return retVal;
	}

	/* To add an element in the heap, we add it as the last leaf, 
	 * and then we move the element upward until
	 * the heap identity is maintained. 
	 */
	@Override
	public void add(T element) {
		// Some preliminary checks first...
		if(last == data.length)
			expandCapacity();
		data[last] = element;
		int current = last, parent = (last - 1) /2;
		while(((T) data[parent]).compareTo((T)data[current]) < 0){ // while you need to switch...
			Object temp = data[current]; // switch.
			data[current] = data[parent];
			data[parent] = temp;
			current = parent;
			parent = (parent - 1) / 2;
		}
		modificationFlag = true;
		last++;
	}

	@Override
	public T removeMax() throws EmptyHeapException {
		if(data[0] == null)
			throw new EmptyHeapException("removeMax(): Heap is empty.");
		T retVal = (T) data[0];
		// To remove the maximum node, we remove the root,
		// make the last leaf node the new root, and then push the new root
		// down the maxheap until the heap identity is maintained.
		data[0] = data[last - 1];
		int current = 0, maxChild = findMaxChild(1, 2);
		// while you need to switch, switch.
		while(maxChild != -1 && ((T) data[maxChild]).compareTo((T) data[current]) > 0){
			Object temp = data[current];
			data[current] = data[maxChild];
			data[maxChild] = temp;
			current = maxChild;
			maxChild = findMaxChild(2*maxChild + 1, 2*maxChild + 2);
		}
		data[--last] = null;
		modificationFlag = true;
		return retVal;
	}

	@Override
	public T getMax() throws EmptyHeapException {
		if(data[0] == null)
			throw new EmptyHeapException("getMax(): Heap is empty.");
		return (T) data[0];
	}

	@Override
	public int size() {
		return last; // In this implementation, also serves as the last counter.
	}

	@Override
	public boolean isEmpty() {
		return last == 0;
	}

	@Override
	public void clear() {
		for(int i = 0; i < data.length && data[i] != null; i++)
			data[i] = null;
		// We won't resize or nulify the heap, because the user would usually call clear()
		// in order to simply clear the elements, not the space in the structure.
		last = 0;
		modificationFlag = true;
	}


	@Override
	public Iterator<T> iterator() {
		return new ArrayMaxHeapIterator<T>();
	}

	/**
	 * An implementation of a fail-fast max-first Iterator for MaxHeaps.
	 * @author Jason Filippou (jasonfil@cs.umd.edu)	 
	 * @param <T2> the type of element accessed by the Iterator.
	 */
	class ArrayMaxHeapIterator<T2 extends Comparable<T2>> implements Iterator<T2>{

		private MaxHeap<T2> tempHeap;

		public ArrayMaxHeapIterator(){
			tempHeap = new ArrayMaxHeap<T2>();
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
				retVal = tempHeap.removeMax();
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
