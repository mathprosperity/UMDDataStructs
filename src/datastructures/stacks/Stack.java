package datastructures.stacks;
import java.util.Iterator;
/*** 
 * A standard stack ADT interface that will be implemented by various kinds
 * of stack implementations. A stack should allow for the following operations:
 * push, pop, peek/top, retrieve count of elements, check for emptiness, clear the 
 * stack and toString().
 * 
 * Stacks will be made Iterable. While this might be counter-intuitive when thinking of Stacks
 * as LIFO ADTs, it will make certain operations, such as copy-construction and equality check possible.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * @since September 2013 
 * @param <T> the type of object that the Stack will hold.
 */

public interface Stack<T> extends Iterable<T>{
	
	/**
	 * Remove and return top element of stack
	 * @return the element on the top of the stack
	 * @throws EmptyStackException if the stack is empty
	 */
	public T pop() throws EmptyStackException;
	
	/**
	 * Add element to the top of the stack
	 * @param element the element to push to the top of the stack
	 */
	public void push(T element);
	
	/**
	 * Return top element without removing it from the stack
	 * @return the element currently on the top of the stack
	 * @throws EmptyStackException if the stack is empty.
	 */
	public T peek() throws EmptyStackException;
	
	/**
	 * Return the size of the stack (number of elements in it)
	 * @return the count of elements in the stack
	 */
	public int size();
	
	/**
	 * Check whether the stack is empty.
	 * @return true if the stack is empty.
	 */
	
	public boolean empty();
	
	/**
	 * Delete all elements in the stack, returning it in its original state.
	 */
	public void clear();
	
	/**
	 * Return a fail-fast iterator which will access the elements in proper order.
	 * 
	 * @return An iterator over the elements held by the Stack.
	 */
	public Iterator<T> iterator();
	
}
