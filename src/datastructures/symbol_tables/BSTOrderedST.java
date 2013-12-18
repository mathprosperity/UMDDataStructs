package datastructures.symbol_tables;
import java.util.Iterator;

import datastructures.trees.*;
import datastructures.queues.*;

/**
 * <p><tt>BSTOrderedST</tt> is a {@link <tt>OrderedST</tt>} implemented
 * using a {@link <tt>datastructures.trees.BinarySearchTree</tt>}.
 * The <tt>get()</tt> and put() operations are thus implemented in
 * O(logN). This constitutes an improvement over {@link <tt>DualArrayOrderedST</tt>},
 * where every new insertion required shifting in the worst case N keys and N values
 * to the right of the arrays contained within the object. A disadvantage
 * is that other operations, such as min() and max(), which used to take constant
 * time in that implementation, now take logarithmic time.</p>
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <Key> The Comparable type of key held by the symbol table.
 * @param <Value> The type of value mapped to by the key.
 */
public class BSTOrderedST<Key extends Comparable<Key>, Value> extends OrderedST<Key, Value> {


	/* A Comparable class that will represent the objects 
	 *  that we insert in the BST.
	 */
	protected class KeyValuePair<K extends Comparable<K>,V> 
			implements Comparable<KeyValuePair<K, V>>{
		K key;
		V value;
		public KeyValuePair(K keyIn, V valIn){
			key = keyIn;
			value = valIn;
		}

		@Override
		public int compareTo(KeyValuePair<K, V> other) {
			return key.compareTo(other.key);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object other){
			if(other == null || !other.getClass().equals(getClass()))
				return false;
			KeyValuePair<K, V> otherCasted = null;
			try {
				otherCasted = (KeyValuePair<K, V>)other;
			} catch(ClassCastException clc){
				return false;
			}
			// TODO: Specify whether this is what you want for KeyValuePairs
			return otherCasted.key.equals(key) && otherCasted.value.equals(value);
		}
	}

	protected BinarySearchTree<KeyValuePair<Key, Value>> pairs;

	/**
	 * Default constructor simply initializes the data structure.
	 */
	public BSTOrderedST(){
		pairs = new LinkedBinarySearchTree<KeyValuePair<Key, Value>>();
	}

	/**
	 * ADT-Level copy construction.
	 * 
	 * @param other The {@link <tt>OrderedST<Key, Value></tt>} to base 
	 * 	the current object's construction on.  
	 */
	public BSTOrderedST(OrderedST<Key, Value> other){
		if(other == null)
			throw new RuntimeException("Cannot create object from null reference.");
		pairs = new LinkedBinarySearchTree<KeyValuePair<Key, Value>>();
		for(Key key: other.keys())
			put(key, other.get(key));
	}

	/**
	 * Standard equals() method.
	 * 
	 * @return true if the object is a carbon copy of the parameter,
	 * 	false otherwise.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other){
		if(other == null || !other.getClass().equals(getClass()))
			return false;
		OrderedST<Key, Value> otherCasted = null;
		try {
			otherCasted = (OrderedST<Key, Value>)other;
		} catch(ClassCastException exc){
			return false;
		}
		if(size() != otherCasted.size())
			return false;
		for(Key okey : keys()){
			Value currVal = otherCasted.get(okey);
			if(currVal == null || !currVal.equals(otherCasted.get(okey)))
				return false;
		}
		return true;
	}

	@Override
	public Key min() throws EmptySymbolTableException {
		try {
			return pairs.getMin().key;
		} catch (EmptyTreeException e) {
			throw new EmptySymbolTableException("min(): table is empty!");
		}
	}

	@Override
	public Key max() throws EmptySymbolTableException {
		try {
			return pairs.getMax().key;
		} catch (EmptyTreeException e) {
			throw new EmptySymbolTableException("min(): table is empty!");
		}
	}

	@Override
	public Key floor(Key key) throws EmptySymbolTableException {
		if(pairs.isEmpty())
			throw new EmptySymbolTableException("floor(): table is empty!");
		Key prev = null;
		for(KeyValuePair<Key, Value> kvp : pairs ){
			if(kvp.key.compareTo(key) == 0) // Found the key, in which case we need to return it.
				return kvp.key;
			else if(kvp.key.compareTo(key) > 0) // Surpassed the key, which means we need to return the previous key, or null if it doesn't exist.
				return prev;
			prev = kvp.key; // Update previous key.
		}
		return prev;
	}

	@Override
	public Key ceiling(Key key) throws EmptySymbolTableException {
		if(pairs.isEmpty())
			throw new EmptySymbolTableException("ceiling(): table is empty!");
		for(KeyValuePair<Key, Value> kvp : pairs)
			if(kvp.key.compareTo(key) >= 0)
				return kvp.key;
		return null; // The provided key would be the maximum.
	}

	@Override
	public int rank(Key key) {
		int rank = 0;
		for(KeyValuePair<Key, Value> kvp: pairs){ // O(logn)
			if(kvp.key.compareTo(key) == 0)
				break;
			rank++;
		}
		return rank;
	}

	@Override
	public Key select(int k) throws BadRankException {
		if(k < 0 || k >= size())
			throw new BadRankException("select(int k): The value k=" + k + " was invalid.");
		int counter = 0;
		Key retVal = null;
		for(KeyValuePair<Key, Value> kvp: pairs){
			if(counter++ == k){
				retVal = kvp.key;
				break;
			}
		}
		return retVal;
	}

	@Override
	public Iterable<Key> keys(Key lo, Key hi) {
		LinkedQueue<Key> kq = new LinkedQueue<Key>();
		for(KeyValuePair<Key, Value> kvp: pairs){
			if(kvp.key.compareTo(hi) > 0)
				break;
			else if(kvp.key.compareTo(lo) >= 0 && kvp.key.compareTo(hi) <= 0)
				kq.enqueue(kvp.key);
		}
		return kq;
	}

	@Override
	public void put(Key key, Value value) throws KeyIsNullException {
		if(key == null)
			throw new KeyIsNullException("put(Key key, Value value): key is null.");
		KeyValuePair<Key, Value> toAdd = new KeyValuePair<Key, Value>(key, value);
		KeyValuePair<Key, Value> el = null;
		try {
			el = pairs.find(toAdd);
		} catch (EmptyTreeException e) { // If the tree is empty, just add the pair and return.
			pairs.add(toAdd);
			return;
		}
		if(el != null)
			el.value = value; // Just replace the value.
		else
			pairs.add(toAdd); // Add a new node.
	}

	@Override
	public Value get(Key key) {
		// Using Tree.find(T el) would not necessarily
		// do what we want, because we only care about the Key
		// part of the KeyValuePair<Key, Value> class: not the
		// Value part.
		if(isEmpty())
			return null;
		for(KeyValuePair<Key, Value> kvp: pairs)
			if(kvp.key.compareTo(key) == 0)
				return kvp.value;
		return null;
	}

	@Override
	public int size() {
		return pairs.size();
	}

	@Override
	public void clear() {
		pairs.clear();
	}

	/**
	 * For <tt>BSTOrderedSt</tt>s, we implement "hard deletions".
	 * @param key The key of the (key, value) pair to remove from 
	 * the collection.
	 */
	@Override
	public void delete(Key key){
		for(KeyValuePair<Key, Value> kvp : pairs){
			if(kvp.key.compareTo(key) == 0){
				try {
					pairs.remove(kvp);
				} catch (EmptyTreeException e) {}
				break; // To avoid concurrent modification issues
			}
		}
	}
}
