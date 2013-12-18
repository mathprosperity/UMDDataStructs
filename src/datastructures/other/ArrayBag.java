package datastructures.other;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.naming.directory.ModificationItem;

/**
 * ArrayBag is a Bag based on a static array which is resized as required
 * to fit more elements.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since October 2013
 *
 */
@SuppressWarnings("unchecked")
public class ArrayBag<T> implements Bag<T> {

	private T[] data;
	private int current;
	private static final int INIT_CAPACITY = 20;
	protected boolean modificationFlag;

	private void expandCapacity(){
		T[] newStore = (T[])(new Object[2*data.length]);
		for(int i = 0; i < data.length; i++)
			newStore[i] = data[i];
		data = newStore;
	}

	/**
	 * Simple constructor. Initializes the Bag with 
	 * the default capacity.
	 */
	public ArrayBag(){
		data = (T[])(new Object[INIT_CAPACITY]);
		current = 0;
		modificationFlag = false;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayBagIterator<T>();
	}
	// To demonstrate that the order of element accessing is irrelevant
	// in a Bag, we will iterate over elements at even positions first
	// and elements at odd positions second.
	
	class ArrayBagIterator<T2> implements Iterator<T2>{
		private int index;
		private boolean switchedToOdd;

		public ArrayBagIterator(){
			switchedToOdd = false;
			modificationFlag = false;
			index = 0;
			
		}
		public boolean hasNext() {
			return !(switchedToOdd && index >= current); 
		}

		public T2 next() throws NoSuchElementException, ConcurrentModificationException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): Attempted to traverse the Bag after a modification to it.");
			if(index >= current){
				if(switchedToOdd == false){
					switchedToOdd = true;
					index = 1;
				}
				else
					throw new NoSuchElementException("No more elements in ArrayBag.");
			}
			T retVal = data[index];
			index += 2;
			return (T2)retVal;
		}

		@Override
		public void remove() throws UnsupportedOperationException{
			throw new UnsupportedOperationException("Cannot remove elements from a Bag!");		
		}
	}



	@Override
	public void add(T element) {
		if(current == data.length)
			expandCapacity();
		data[current++] = element;
		modificationFlag = true;
	}


	@Override
	public void clear() {
		for(int i = 0; i < data.length; i++)
			data[i] = null;
		current = 0;
		System.gc();
		modificationFlag = true;
	}


	@Override
	public boolean isEmpty() {
		return current == 0;
	}

	@Override
	public int size(){
		return current;
	}

	@Override
	public boolean contains(T element) {
		for(T el: this)
			if(el.equals(element))
				return true;
		return false;
	}

	@Override
	public String toString(){
		String s ="[";
		for(int i = 0; i < data.length - 1; i++)
			s += (data[i] + ", ");
		s += data[data.length - 1] + "]";
		return s;
	}

}
