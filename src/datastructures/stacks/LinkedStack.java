package datastructures.stacks;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import datastructures.lists.*;

/** LinkedStack is a Stack generic ADT which uses a LinkedLinearList
 * as its underlying implementation. It is thus a purely dynamic data
 * structure which does not rely on expensive resizing of the underlying
 * storage. We improve efficiency of the basic stack operations by assuming
 * that the top of the Stack is located at the front of the underlying List.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @since September 2013
 */
public class LinkedStack<T> implements Stack<T> {

	private List<T> data;
	protected boolean modificationFlag;
	
	/** Constructor initializes the underlying store. */
	public LinkedStack(){
		data = new LinkedList<T>();
		modificationFlag = false;
	}
	
	/** Copy constructor. Initializes the current element with the data contained
	 * in the parameter object.
	 * @param other The Stack<T> object to compare ourselves to. Notice that we allow for copying
	 * any kind of Stack.
	 */
	public LinkedStack(Stack<T> other){
		if(other == null)
			return;
		data = new LinkedList<T>();
		for(T el: other)
			data.pushFront(el);
		modificationFlag = false;
	}
	
	/**
	 * Standard equals() method. Two ArrayStacks are considered to be equal
	 * if they contain the exact same elements at the exact same positions.
	 * @param other The object to compare the current object to.
	 * @return true if the objects are considered "equal", with equality established
	 * by the contract above.
	 */
	@Override
	public boolean equals(Object other){
		if(!(other instanceof Stack<?>))
			return false;
		@SuppressWarnings("unchecked")
		Stack<T> ostack = (Stack<T>)other;
		if(size() != ostack.size())
			return false;
		// Stacks, being Iterables, expose iterators!
		Iterator<T> ito = ostack.iterator(), itc = this.iterator();
		while(ito.hasNext())
			if(!ito.next().equals(itc.next()))
				return false;
		return true;
	}
	@Override
	public T pop() throws EmptyStackException {
		T top = null;
		try {
			top = data.getFirst();
			data.remove(0);
		} catch(EmptyListException exc){
			throw new EmptyStackException("pop(): Stack was empty!");
		} catch(IllegalListAccessException exc){
			// The compiler complains if we do not add a dummy catchblock for remove().
		}
		modificationFlag = true;
		return top;
	}

	@Override
	public void push(T element) {
		data.pushFront(element); // O(1) operation
		modificationFlag = true;
	}

	@Override
	public T peek() throws EmptyStackException {
		T top = null;
		try {
			top = data.getFirst();
		} catch(EmptyListException exc){
			throw new EmptyStackException("peek(): Stack was empty!");
		}
		return top;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean empty() {
		return data.size() == 0;
	}

	@Override
	public void clear() {
		data.clear();
		modificationFlag = true;
	}

	@Override
	public String toString(){
		String answer = "";
		for(T el: data)
			answer += ("|" + el + "|\n");
		answer += "___"; // Good practice to never add extra newline in toString(), since people might println() the object anyway...
		return answer;
	}

	@Override
	public Iterator<T> iterator() {
		return new LinkedStackIterator<T>();
	}
	
	class LinkedStackIterator<T2> implements Iterator<T2>{
		
		private int currInd;
		
		LinkedStackIterator(){
			currInd = data.size() - 1;
			modificationFlag = false;
		}
		@Override
		public boolean hasNext() {
			// We have another element coming our way
			// if we query for it and don't receive an
			// exception from the underlying List.
			try {
				data.get(currInd);
			} catch(EmptyListException | IllegalListAccessException e ) { // Thank you Java 7
				return false;
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T2 next() throws ConcurrentModificationException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): Stack was modified while accessing it through iterator.");
			T2 retVal = null;
			try {
				retVal =  (T2) data.get(currInd--);
			} catch (EmptyListException | IllegalListAccessException e) { 
				// Dummy
			}
			return retVal;
		}

		@Override
		public void remove() throws IllegalStateException{
			if(currInd == data.size() - 1)
				throw new IllegalStateException("Need at least one call to next() before attempting removal.");
			try {
				data.remove(currInd + 1);
			} catch (IllegalListAccessException e) {}
		}
		
	}

}
