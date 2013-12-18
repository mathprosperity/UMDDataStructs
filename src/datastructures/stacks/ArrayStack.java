package datastructures.stacks;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import datastructures.InvalidCapacityException;

/**
 *  An implementation of a stack ADT which uses an array internally.
 *  The ADT is actually dynamic, because we choose to expand the array when
 *  the stack reaches capacity.
 *  
 *  @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 *  @since September 2013
 */

@SuppressWarnings("unchecked")
public class ArrayStack<T> implements Stack<T> {

	private T[] data;
	private final static int INIT_CAPACITY = 50; // Default capacity of 50 elements
	private int top; // serves as both top and count indicator.
	protected boolean modificationFlag;

	/**
	 *  Constructor. Initializes the stack with the default element capacity. 
	 */
	public ArrayStack(){
		top = -1; // Not really needed, but let's put it in there for consistency.
		data = (T[])(new Object[INIT_CAPACITY]); // Suppressed the type safety warning for this downcasting
		modificationFlag = false;
	}

	/** Constructor. Initializes the stack with the provided element capacity.
	 * @param capacity The element capacity to initialize the object with.
	 * @throws InvalidCapacityException If the capacity provided is negative.
	 */
	public ArrayStack(int capacity) throws InvalidCapacityException{
		if(capacity < 0)
			throw new InvalidCapacityException("Invalid capacity provided!");
		top = -1;
		data = (T[])(new Object[capacity]);
		modificationFlag = false;
	}

	/** Copy constructor. Initializes the current element with the data contained
	 * in the parameter object.
	 * @param other The Stack<T> object to compare ourselves to. Notice that we allow for copying
	 * any kind of Stack.
	 */
	public ArrayStack(Stack<T> other){
		if(other == null)
			return;
		data = (T[])(new Object[other.size()]);
		top = other.size() - 1;
		// We will need to be inserting the elements in the same position 
		// that they are in in the parameter Stack. Therefore, calling push()
		// for every element is not gonna cut it, because it'll actually insert them
		// in the reverse order!
		int counter = 0;
		for(T el: other){
			if(el == null) 
				break;
			data[counter++] = el;
		}
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
		if(other == null)
			return false;
		if(!(other instanceof Stack<?>))
			return false;
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

	/* Methods inherited from Stack<T> interface: */

	@Override
	public T pop() throws EmptyStackException {
		if(top == -1)
			throw new EmptyStackException("Stack is empty.");
		T topEl = data[top];
		data[top--] = null; // To allow for garbage collection asap.
		modificationFlag = true;
		return topEl;
	}

	@Override
	public void push(T element) {
		if(top == data.length - 1){ // Double the stack's capacity. This is tedious in an array-based implementation.
			T[] newStore = (T[]) (new Object[2*data.length]);
			for(int i = 0; i < data.length; i++) // Copy over all old elements.
				newStore[i] = data[i];
			data = newStore; // Make sure to update reference to point to new store!
		}
		data[++top] = element; 
		modificationFlag = true;
	}

	@Override
	public T peek() throws EmptyStackException{
		if(top == -1 )
			throw new EmptyStackException("Stack is empty.");
		return data[top];
	}

	@Override
	public int size() {
		return top + 1; // Zero-indexing...
	}

	/* It's funny how Eclipse didn't auto-generate the skeleton for toString() 
	 * from the interface, because it's already implicitly implemented by 
	 * virtue of extending Object...
	 */
	@Override
	public String toString(){
		String answer = "";
		for(int i = top; i > -1; i--){
			answer += ("|" + data[i] + "|\n");
		}
		answer += "___"; // Good practice to never add extra newline in toString(), since people might println() the object anyway...
		return answer;
	}

	@Override
	public boolean empty() {
		return top == -1;
	}

	/* Small helpful method to empty the stack if required. */
	@Override 
	public void clear(){
		// Nullify references to call garbage collector asap 
		for(int i = 0; i < top + 1; i++)
			data[i] = null;
		System.gc(); // Hint to the JVM that we have objects not referenced to.
		top = -1;
		modificationFlag = true;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayStackIterator<T>();
	}
	
	/* Inner class which implements a fail-fast iterator
	 * for ArrayStacks.
	 */

	class ArrayStackIterator<T2> implements Iterator<T2> {

		private int current = 0;

		public ArrayStackIterator(){
			modificationFlag = false;
		}
		
		@Override
		public boolean hasNext(){
			return current < data.length && data[current] != null;
		}

		@Override
		public T2 next()  throws ConcurrentModificationException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): Attempted to traverse Stack after removal of an element.");
			return (T2)data[current++];
		}

		@Override
		public void remove() throws IllegalStateException{
			if(current == 0)
				throw new IllegalStateException("Need at least one call to next() before attempting removal.");
			// Shift all references to the right of that reference
			// one cell to the left, decrementing "top".
			for(int i = current; i <= top; i++)
				data[i - 1] = data[i];
			data[top--] = null;
			current--; // Need to do this, lest I will miss an element.
		}

	}
	
}

