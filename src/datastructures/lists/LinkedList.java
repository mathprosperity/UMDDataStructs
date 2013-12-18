package datastructures.lists;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/** LinkedList is a simple (single) linked list. A linked list
 * is the most common list out there. The main benefit compared to an array-based
 * implementation is that it's a dynamic data structure which never has to 
 * expand its capacity to accommodate new elements. The main
 * drawbacks compared to an array-based implementation is that certain methods, such as
 * accessing or deleting methods based on indices, are not that efficient.
 *  
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since September 2013
 *
 */
public class LinkedList<T> implements List<T> {

	private Node<T> head; // head of the list
	protected boolean modificationFlag; // helps with fail-fast iteration

	// Keeping a count of the number of list elements as we insert 
	// them will make querying for the size more efficient.
	protected int size; 

	/** Constructor only initializes the head node reference to null.*/
	public LinkedList(){
		head = null; 
		size = 0;
		modificationFlag = false;
	}

	/** Copy constructor. Adds all elements of "other" to our own list. 
	 * @param other the LinearList to copy the elements from.
	 */
	public LinkedList(List<T> other){
		if(other == null)
			return;
		for(T element: other)
			pushBack(element);
		modificationFlag = false;
	}

	@Override
	public Iterator<T> iterator() {
		return new LinkedListIterator<T>();
	}

	/* Inner class which implements a fail-fast Iterator.
	 * Objects of this class are returned by LinkedList.iterator(). 
	 */

	@SuppressWarnings("unchecked")
	class LinkedListIterator<T2> implements Iterator<T2>{
		
		private Node<T2> current, previous; 
		
		public LinkedListIterator(){
			modificationFlag = false;
			previous = (Node<T2>) null;
			current = (Node<T2>) head;
		}

		public boolean hasNext(){
			return current != null;
		}

		public T2 next() throws ConcurrentModificationException{
			if(modificationFlag) // fail fast
				throw new ConcurrentModificationException("next(): Attempted to traverse a list after removal.");
			T2 currData = current.getData();
			previous = current;
			current = current.next;
			return currData;
		}

		public void remove() throws IllegalStateException{
			if(previous != null){ // You need at least one call to next() in order for removal to make sense.
				removeNodeAt(previous);	
				size--;
			} else 
				throw new IllegalStateException("Need at least one call to next() before attempting removal.");
		}
		
		private void removeNodeAt(Node<T2> target){
			if(target == head)
				head = head.next;
			else{
				Node<T2> iterator = (Node<T2>)head;
				while(iterator.next != target)
					iterator = iterator.next;
				iterator.next = target.next;
			}
		}
	};

	@Override
	public void pushFront(T element) {
		Node<T> newHead = new Node<T>(element, head);
		head = newHead;
		size++;
		modificationFlag = true;
	}

	@Override
	public void pushBack(T element) {
		if(head == null){
			head = new Node<T>(element);
			size++;
			return;
		}
		Node<T> current = head;
		while(current.next != null)
			current = current.next;
		current.next = new Node<T>(element);
		size++;
		modificationFlag = true;
	}

	@Override
	public T getFirst() throws EmptyListException {
		T retVal = null;
		try {
			retVal = get(0);
		} catch (IllegalListAccessException e) {
			// Impossible to have this exception thrown
			// when given an argument of 0, but compiler was
			// complaining about it, so dummy catchblock.
		}
		return retVal;
	}

	@Override
	public T getLast() throws EmptyListException {
		T retVal = null;
		try {
			retVal = get(size() - 1);
		} catch (IllegalListAccessException e) {
			// Impossible to have this exception thrown
			// when given an argument of size() -1, but compiler was
			// complaining about it, so dummy catchblock.
		}
		return retVal;
	}

	@Override
	public T get(int index) throws EmptyListException,
	IllegalListAccessException {
		if(isEmpty())
			throw new EmptyListException("get(" + index + "): list is empty!");
		if(index < 0 || index >= size())
			throw new IllegalListAccessException("get(): Index of "+ index + " was beyond appropriate bounds.");
		int currInd = 0;
		Node<T> currNode = head;
		while(currInd++ < index)
			currNode = currNode.next;
		return currNode.getData();
	}

	@Override
	public boolean contains(T element) {
		if(head == null)
			return false;
		Node<T> current = head;
		while(current != null){ // Not current.next!
			if(current.getData().equals(element))
				return true;
			current = current.next;
		}
		return false;
	}

	@Override
	public boolean remove(T element) {
		// In this method, we will need to keep control of the previous node
		// at every step of the iteration.
		Node<T> previous = head, current = head;
		while(current != null){
			if(current.getData().equals(element)){
				if(current == head){// Special case of head of the list
					head = current.next;
					size--;
					modificationFlag = true;
					return true;
				}
				previous.next = current.next; // severing the connection
				size--;
				modificationFlag = true;
				return true;
			}
			previous = current;
			current = current.next;
		}
		return false;
	}

	@Override
	public void remove(int index) throws IllegalListAccessException {
		// Again, we will need a "previous" node reference.
		if(index < 0 || index >= size)
			throw new IllegalListAccessException("remove(int): Index " + index + " is not a valid list index.");
		// Special case of removing the first element of the list:
		if(index == 0){
			head = head.next; // Could be null, signifying that only one element was contained in the list.			
			size--;
			modificationFlag = true;
			return;
		}
		Node<T> previous = head, current = head;
		for(int i = 0; i < index; i++){
			previous = current;
			current = current.next;
		}
		previous.next = current.next;
		size--;
		modificationFlag = true;
	}

	@Override
	public boolean removeAll(T element) {
		boolean response = false;
		while(contains(element)){
			response = true;
			remove(element);
		}
		if(response == true)
			modificationFlag = true;
		return response;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		// Because the list is singly linked, there are no cyclic references. Therefore,
		// setting the head reference to null will cause all of the other references to be
		// detected by the garbage collector as disposable.
		head = null;
		System.gc(); // *Hint* to the JVM that it should run garbage collection.
		size = 0;
		modificationFlag = true;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override 
	public String toString(){
		String retVal = "[";
		Node<T> current = head;
		while(current != null){
			retVal += current.getData();
			if(current.next != null)
				retVal += ", ";
			current = current.next;
		}
		retVal += "]";
		return retVal;
	}

	/**
	 * Standard equals() method. Two LinearLists are equal if they contain
	 * the exact same elements at the same positions. Note that this equals()
	 * method allows for comparing different implementations of LinearLists,
	 * and potentially allowing them to be equal because they maintain the aforementioned
	 * contract! We are interested in ADT comparison, not 1-1 memory allocation comparison.
	 * 
	 * @param other The Object reference to compare to
	 * @return true if the two objects are considered "equal", with equality defined above.
	 */
	@Override 
	public boolean equals(Object other){
		if(other == null)
			return false;
		if(!(other instanceof List<?>))
			return false;
		@SuppressWarnings("unchecked")
		List<T> otherList= (List<T>)other;
		if(otherList.size() != size())
			return false;
		Iterator<T> ito = otherList.iterator(), itc = this.iterator();
		while(ito.hasNext())
			if(!ito.next().equals(itc.next()))
				return false;
		return true;
	}

	protected class Node<Type>{ // Using <Type> instead of <T> to suppress warnings about type shadowing.

		public Node<Type> next; // We keep the "next" reference public for easy accessing.
		protected Type data;

		/* Two constructors */
		public Node(Type data, Node<Type> next){
			this.data = data;
			this.next = next;
		}

		public Node(Type data){ // This constructor is useful for placing a node at the end of the list (pushBack).
			this(data, null);
		}

		public Type getData(){
			return data;
		}

		public void setData(Type data){
			this.data = data;
		}
	}




}
