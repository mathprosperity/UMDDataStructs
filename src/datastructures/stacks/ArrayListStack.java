package datastructures.stacks;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import datastructures.InvalidCapacityException;

/** ArrayListStack is a Stack based on an underlying ArrayList. 
 * The one thing that is markedly different from ArrayStack is 
 * the fact that the ArrayList performs capacity resizing faster, 
 * when the data structure reaches capacity. Other minor changes 
 * are also there, such as the way in which the ADT is cleared.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since September 2013
 */

public class ArrayListStack<T> implements Stack<T> {

	/* Private data members. The "top" index, present in
	 * ArrayStack, is no longer needed. */

	private ArrayList<T> data;
	private final static int INIT_CAPACITY = 50; // Default capacity of 50 elements
	protected boolean modificationFlag;

	/**
	 * Constructor. Initializes the data structure with the default capacity.
	 */
	public ArrayListStack(){
		data = new ArrayList<T>(INIT_CAPACITY);
		modificationFlag = false;
	}

	/**
	 * Constructor. Initializes the data structure with the provided capacity.
	 * @param capacity The initial space to allocate for contained objects.
	 * @throws InvalidCapacityException If the capacity provided is negative.
	 */
	public ArrayListStack(int capacity) throws InvalidCapacityException{
		if(capacity < 0)
			throw new InvalidCapacityException("Invalid capacity provided!");
		data = new ArrayList<T>(capacity);
		modificationFlag = false;
	}

	/**
	 * Copy constructor. Allows the creation of an ArrayListStack from any
	 * other kind of Stack.
	 * @param other The stack to create the current object from.
	 */
	public ArrayListStack(Stack<T> other){
		if(other == null)
			return;
		data = new ArrayList<T>(other.size());
		for(T el: other)
			data.add(el);
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

	/* Methods inherited from Stack<T> interface: */

	@Override
	public T pop() throws EmptyStackException {
		if(data.isEmpty() )
			throw new EmptyStackException("Stack is empty.");
		T top = peek();
		data.remove(data.size() - 1);
		modificationFlag = true;
		return top;
	}

	@Override
	public void push(T element) {
		data.add(element); // Simple, fast, and effective. Thank you ArrayList!
		modificationFlag = true;
	}

	@Override
	public T peek() throws EmptyStackException{
		if(data.isEmpty())
			throw new EmptyStackException("Stack is empty.");
		return data.get(data.size() - 1);
	}

	@Override
	public int size() {
		return data.size(); 
	}

	/* It's funny how Eclipse didn't auto-generate the skeleton for toString() 
	 * from the interface, because it's already implicitly implemented by 
	 * virtue of extending Object...
	 */
	@Override
	public String toString(){
		String answer = "";
		for(int i = data.size() - 1; i > -1; i--){
			answer += ("|" + data.get(i) + "|\n");
		}
		answer += "___"; // Good practice to never add extra newline in toString(), since people might println() the object anyway...
		return answer;
	}

	@Override
	public boolean empty() {
		return data.isEmpty();
	}

	/* I may be overkilling it here, but the assumption is that a Stack used to service a particular
	 * number of elements is likely to service a similar number of elements in the future,
	 * and having the capacity appropriate will make pushes more efficient. */
	@Override 
	public void clear(){
		data.trimToSize();
		int currentCapacity = data.size(); 
		data.clear();
		data.ensureCapacity(currentCapacity);
		modificationFlag = true;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayListStackIterator<T>();
	}

	class ArrayListStackIterator<T2> implements Iterator<T2>{
		private int currIndex;

		public ArrayListStackIterator(){
			currIndex = 0;
			modificationFlag = false;
		}
		
		@Override
		public boolean hasNext() {
			return (currIndex < data.size());
		}

		@SuppressWarnings("unchecked")
		@Override
		public T2 next()  throws ConcurrentModificationException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): Attempted to traverse the stack after removal.");
			return (T2)data.get(currIndex++);
		}

		@Override
		public void remove() throws IllegalStateException{
			if(currIndex == 0)
				throw new IllegalStateException("Need at least one call to next() before attempting removal.");
			data.remove(--currIndex);
		}
	}
}

