package datastructures.lists;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hamcrest.core.IsEqual;

/** 
 * A DoublyLinkedList is a {@link LinkedList} whose nodes have two references,
 * one pointing to the previous and one to the next node.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The data type held by the list.
 */
public class DoublyLinkedList<T> extends LinkedList<T>{


	private DoublyLinkedNode<T> head;
	
	public DoublyLinkedList(){
		size = 0;
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

	@Override
	public Iterator<T> iterator(){
		return new DoublyLinkedListIterator<T>();
	}

	/* Inner class which implements a fail-fast iterator
	 * for DoublyLinkedLists.*/
	@SuppressWarnings("unchecked")
	class DoublyLinkedListIterator<T2> implements Iterator<T2> {
		
		private DoublyLinkedNode<T2> current;
		private boolean fullCircle;

		public DoublyLinkedListIterator(){
			current = (DoublyLinkedNode<T2>)head;
			modificationFlag = false;
			fullCircle = false;
		}

		public boolean hasNext(){
			return current != null && !fullCircle;
		}

		public T2 next()  throws ConcurrentModificationException, NoSuchElementException{
			if(modificationFlag)
				throw new ConcurrentModificationException("next(): List was modified while traversing it through iterator.");
			if(fullCircle)
				throw new NoSuchElementException("next(): Iterator exhausted elements.");
			T2 currData = current.getData();
			current = current.next;
			if(current == head)				
				fullCircle = true;
			return currData;
		}

		public void remove() throws IllegalStateException{
			if(current == head && !fullCircle)
				throw new IllegalStateException("Need at least one call to next() before attempting removal.");
			if(isEmpty())
				return;
			if(size() == 1){
				head = null;
				fullCircle = true;
			} else {
				if(current.previous == head) // Special case where we need to update head as well.
					head = (DoublyLinkedNode<T>) current;
				current.previous.previous.next = current;
				current.previous = current.previous.previous;	
			}
			if(--size == 1)
				fullCircle = true;
		}
	};

	@Override
	public void pushBack(T element){
		modificationFlag = true; // will always push back.
		if(head == null){ // recall that "head" is a "protected" data member
			head = new DoublyLinkedNode<T>(element);
			head.next = head;
			head.previous = head;
			size++;
			return;
		}
		/* Inserting at the end of a DoublyLinkedLinearList is faster
		 * than in the case of a LinkedLinearList.
		 */
		DoublyLinkedNode<T> newNode = new DoublyLinkedNode<T>(element, head.previous, head);
		newNode.previous.next = newNode;
		newNode.next.previous = newNode; 
		size++;
		// Reference to head node remains unaffected.
	}

	@Override 
	public void pushFront(T element){
		modificationFlag = true; // will always push front.
		if(head == null){
			head = new DoublyLinkedNode<T>(element);
			head.next = head;
			head.previous = head;
			size++;
			return;
		}
		DoublyLinkedNode<T> newNode = new DoublyLinkedNode<T>(element, head.previous, head);
		newNode.previous.next = newNode;
		newNode.next.previous = newNode;
		head = newNode; // Touching head reference.
		size++;
	}

	@Override
	public T getFirst() throws EmptyListException{
		if(head == null)
			throw new EmptyListException("getFirst(): list is empty.");
		return head.getData();
	}

	@Override 
	public T getLast() throws EmptyListException{
		if(head == null)
			throw new EmptyListException("getLast(): list is empty.");
		return head.previous.getData();
	}

	/**
	 * Overriding of get(int index) for DoublyLinkedLists. This takes
	 * advantage of the structure of the list in order to optimize the search
	 * if the index of the element to be found is closer to either the end
	 * or the start of the list.
	 * 
	 * @param index The index of the element to remove.
	 * @return The index-th element of the list.
	 */
	@Override
	public T get(int index) throws EmptyListException, IllegalListAccessException{
		if(isEmpty())
			throw new EmptyListException("get(" + index + "): list is empty!");
		if(index < 0 || index >= size())
			throw new IllegalListAccessException("get(): index of " + index + " is out-of-bounds for this List.");
		int counter = 0;
		DoublyLinkedNode<T> current = head;
		// We can optimize the search to go forward or backward depending
		// on whether the index of the element to be found is above or below
		// the mid-point of the list.
		if(index < size() / 2) // Go forward
			while(counter++ < index)
				current = current.next;
		else // go backward
			while(counter++ < index)
				current = current.previous;

		return current.getData();


	}

	@Override
	public boolean contains(T element){
		if(head == null)
			return false;
		DoublyLinkedNode<T> current = head;
		while(current.next != head){ // Iterate up until one node before the head
			if(current.getData().equals(element))
				return true;
			current = current.next;
		}
		if(current.getData().equals(element)) // last node before head (maybe head itself, if there's only one node)
			return true;
		return false;
	}

	@Override
	public boolean remove(T element){
		DoublyLinkedNode<T> current = head;
		while(current.next != head){ // Again, iterate until one node before the head.
			if(current.getData().equals(element)){
				if(current == head){// Special case of head of the list
					if(head.next == head) // Special case of single element in the list.
						head = null;
					else{
						head.previous.next = head.next;
						head.next.previous = head.previous;	
						head = current.next; // essentially, we are moving all references away from "current"
					}
					size--;
					modificationFlag = true;
					return true;
				}
				current.previous.next = current.next; // severing the connection
				current.next.previous = current.previous;
				size--;
				modificationFlag = true;
				return true;
			}
			current = current.next;
		}
		if(current.getData().equals(element)){
			head.previous = current.previous;
			current.previous.next = head;
			size--;
			modificationFlag = true;
			return true;
		}
		return false;
	}

	@Override 
	public boolean removeAll(T element){
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
	public void remove(int index) throws IllegalListAccessException{
		if(index < 0 || index >= size)
			throw new IllegalListAccessException("remove(int): Index " + index + " is not a valid list index.");
		modificationFlag = true; // If the method doesn't throw an exception, it will always remove an element, thus modifying the container.
		// Special case of removing the first element of the list:

		if(index == 0){
			if(head.next == head)// Special case of only one element in the list
				head = null;	
			else{
				head.previous.next = head.next;
				head.next.previous = head.previous;
				head = head.next; // Could be null, signifying that only one element was contained in the list.			
			}
			size--;
			return;
		}
		DoublyLinkedNode<T> current = head;
		for(int i = 0; i < index; i++)
			current = current.next;
		current.previous.next = current.next;
		current.next.previous = current.previous;
		size--;
	}


	@Override 
	public String toString(){
		String retVal = "[";
		DoublyLinkedNode<T> current = head;
		while(current.next != head){
			retVal += current.getData();
			retVal += ", ";
			current = current.next;
		}
		retVal += current.getData() + "]";
		return retVal;
	}


	/* Inner class which describes the type of node held by a
	 * DoublyLinked list.
	 */
	private class DoublyLinkedNode<Type>{

		Type data;
		public DoublyLinkedNode<Type> previous, next;

		/* Constructors... */
		public DoublyLinkedNode(Type data, DoublyLinkedNode<Type> previous, DoublyLinkedNode<Type> next){
			this.data = data;
			this.next = next;
			this.previous = previous;
		}

		public DoublyLinkedNode(Type data, DoublyLinkedNode<Type> previous){
			this(data, previous, null);
		}

		public DoublyLinkedNode(Type data){
			this(data, null);
		}

		@SuppressWarnings("unused")
		public void setData(Type data){
			this.data = data;
		}

		public Type getData(){
			return data;
		}
	};
}



