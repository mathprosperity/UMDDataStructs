package datastructures.other;

import java.util.Iterator;

import datastructures.lists.*;

/**
 * A LinkedBag is a linked implementation of a Bag.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The type of data contaiend in the LinkedBag.
 */
public class LinkedBag<T> implements Bag<T> {

	private List<T> data;
	
	/**
	 * Default constructor initializes the data structure.
	 */
	public LinkedBag(){
		data = new LinkedList<T>();
	}
	
	@Override
	public Iterator<T> iterator() {
		return data.iterator(); // since the order doesn't matter, give the insertion order.
	}

	@Override
	public void add(T element) {
		data.pushBack(element);
		
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean contains(T element) {
		return data.contains(element);
	}

}
