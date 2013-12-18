package datastructures.symbol_tables;

import datastructures.lists.LinkedList;

/**
 * <p><tt>LinearProbingST</tt> is a symbol table based on hashing with
 * linear probing through open addressing. Any hash function can be used,
 * but hash functions with nice theoretical properties (uniform key distribution,
 * efficiency of calculation, potential caching, etc) are preferred. The inner
 * representation is a pair of arrays consisting of keys and values, with a 1-1
 * correspondence between them. Collisions are resolved by moving from the
 * hashed index to any index that has space for a new key-value pair. Array resizing
 * is absolutely necessary for this kind of symbol table to create space for new keys,
 * because if we don't resize the arrays when the load factor becomes 1, we'll end up
 * with an infinite loop while scanning for open positions in the key array. As with
 * {@link SeparateChainingST}, we also shrink the array after deletions in order to
 * avoid memory wasting.
 * </p>
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <Key> The type for the key of the symbol table.
 * @param <Value> The type for the value of the symbol table.
 */
@SuppressWarnings("unchecked")
public class LinearProbingST<Key, Value> extends ST<Key, Value> {

	private Key[] keys;
	private Value[] vals;
	private int count;
	private static final int DEFAULT_CAPACITY = 97;

	/**
	 * Constructor initializes the data structure with the 
	 * provided capacity as the size of the symbol table.
	 * 
	 * @param capacity The number of entries in the symbol table.
	 */
	public LinearProbingST(int capacity){
		keys = (Key[]) new Object[capacity];
		vals = (Value[]) new Object[capacity];
		count = 0;
	}

	/**
	 * Default constructor initializes the data structure with
	 * a default capacity.
	 */
	public LinearProbingST(){
		this(DEFAULT_CAPACITY);
	}

	/**
	 * ADT-level copy constructor.
	 * @param other The {@link ST} to base the current
	 * object's construction on. 
	 */
	public LinearProbingST(ST<Key, Value> other){
		// We will not initialize the key and value arrays
		// based on the parameter's size, because that would
		// immediately make our load factor 1. Instead, we will
		// initialize them with triple the parameter's size.
		keys = (Key[]) new Object[3*other.size()];
		vals = (Value[]) new Object[3*other.size()];
		count = 0;
		for(Key key : other.keys())
			put(key, other.get(key)); 
	}

	public boolean equals(Object other){
		if(other == null || other.getClass() != getClass())
			return false;
		LinearProbingST<Key, Value> ocasted = null;
		try {
			ocasted = (LinearProbingST<Key, Value>)other;
		} catch(ClassCastException clc){
			return false;
		}
		if(keys.length != ocasted.keys.length || 
				vals.length != ocasted.vals.length)
			return false;
		for(int i = 0; i < keys.length; i++)
			if(!keys[i].equals(ocasted.keys[i]) || 
					!vals[i].equals(ocasted.vals[i]))
				return false;
		return true;
	}

	private int hash(Key key){
		return (key.hashCode() & 0x7ffffff) % keys.length;
	}

	@Override
	public void put(Key key, Value value) throws KeyIsNullException {
		if(key == null)
			throw new KeyIsNullException("put(): Key is null.");
		int index = hash(key);
		while(keys[index] != null){// Need to move forward
			if(keys[index].equals(key)){ // If you find the key...
				vals[index] = value; // just update the value and return
				return;
			}
			index = (index + 1) % keys.length;
		}
		keys[index] = key;
		vals[index] = value;
		count++;
		if(count >= keys.length / 2)
			resize(2*keys.length);
	}

	@Override
	public Value get(Key key) {
		// Find the hashed index, and keep moving forward
		// until you find the key. If you don't, return null.
		for(int i = hash(key); keys[i] != null; i = (i + 1)% keys.length)
			if(keys[i].equals(key))
				return vals[i];
		return null;
	}

	/*
	 * Hard-deleting an element from a linear probing symbol table is not
	 * as straightforward a process as setting its corresponding entry to null, because
	 * that might create a "hole" in an otherwise perfectly operating key cluster.
	 * The corresponding key and value entries will be set to null but, in addition,
	 * all subsequent key-value pairs in the current key-value clusters will be re-inserted
	 * in the table.    
	 */

	@Override 
	public void delete(Key key){
		int index = hash(key);
		while(keys[index] != null){ // Scan entire key cluster
			if(!keys[index].equals(key)){
				index = (index + 1) % keys.length;
			} else {
				keys[index] = null;
				vals[index] = null;
				count--;
				index = (index + 1) % keys.length;
				break;
			}
		}
		for(int i = index; keys[i] != null; i = (i+1)%keys.length){ // If there's more of the cluster to scan...
			Key keyToReAdd = keys[i];
			Value valToReAdd = vals[i];
			keys[i] = null;
			vals[i] = null;
			count--;
			put(keyToReAdd, valToReAdd);
		}
		if(count <= keys.length / 8)
			resize(keys.length / 2);
	}

	private void resize(int newCapacity){
		// Create a new LinearProbingST, insert all <key, value> pairs
		// in this in the new ST, and make this'
		// data references point to the new ST's objects.
		LinearProbingST<Key, Value> temp = new LinearProbingST<Key, Value>(newCapacity);
		for(int i = 0; i < keys.length; i++)
			if(keys[i]!=null)
				temp.put(keys[i], vals[i]);
		keys = temp.keys;
		vals = temp.vals;
		count = temp.count;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public Iterable<Key> keys() {
		LinkedList<Key> list = new LinkedList<Key>();
		for(Key key: keys)
			if(key != null)
				list.pushFront(key);
		return list;
	}


	@Override
	public void clear() {
		keys = (Key[]) new Object[keys.length];
		vals = (Value[]) new Object[vals.length];	
		count = 0;
	}

}
