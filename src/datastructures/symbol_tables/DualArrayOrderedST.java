package datastructures.symbol_tables;

import datastructures.queues.*;

/**
 * <tt>DualArrayOrderedST</tt> is a {@link <tt>OrderedST</tt>} which uses two 
 * parallel arrays for keys and values. It is based on the implementation
 * used in the book "Algorithms, 4th ed.", by Sedgewick and Wayne.
 *   
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 *@since November 2013
 *
 *@see "Algorithms, 4th ed., Robert Sedgewick and Kevin Wayne."
 */
@SuppressWarnings("unchecked")
public class DualArrayOrderedST<Key extends Comparable<Key>, Value> 
				extends OrderedST<Key, Value> {

	private Object[] keys;
	private Object[] values;
	private int size;
	private final int INIT_CAPACITY = 20;

	/**
	 * Default constructor. Initializes the symbol table with
	 * the default capacity.
	 */
	public DualArrayOrderedST(){
		keys = new Object[INIT_CAPACITY];
		values = new Object[INIT_CAPACITY];
		size = 0;
	}

	/**
	 * Non-default constructor. Initializes the symbol table with
	 * the provided capacity.
	 * @param capacity The capacity to initialize the symbol table with.
	 */
	public DualArrayOrderedST(int capacity){
		keys = new Object[capacity];
		values = new Object[capacity];
		size = 0;
	}

	/**
	 * ADT-level copy-constructor. Initializes the <tt>DualarrayOrderedST</tt>
	 * with any given {@link <tt>OrderedST</tt>} as input.
	 * @param other The <tt>OrderedST</tt> used as a basis to construct the current object off of.
	 */
	public DualArrayOrderedST(OrderedST<Key, Value> other){
		if(other == null)
			return;
		keys = new Object[other.size()];
		values = new Object[other.size()];
		int counter = 0; // Exploit the known structure of the DAOST
		for(Key k: other.keys()){
			keys[counter] = k;
			values[counter++] = other.get(k);
		}
		size = counter;
	}

	/**
	 * ADT-level equals() method.
	 * @param other the Object to compare the current object to.
	 */
	@Override
	public boolean equals(Object other){
		if(other == null)
			return false;
		if(!other.getClass().equals(getClass()))
			return false;
		OrderedST<Key, Value> otherCasted = null;
		try{
			otherCasted = (OrderedST<Key, Value>)other;
		}catch(ClassCastException exc){
			return false;
		}
		if(size != otherCasted.size())
			return false;
		int index = 0;
		for(Key k : otherCasted.keys()){
			if(!keys[index].equals(k))
				return false;
			if(!values[index++].equals(otherCasted.get(k)))
				return false;
		}
		return true;
	}


	@Override
	public Key min() throws EmptySymbolTableException {
		if(isEmpty())
			throw new EmptySymbolTableException("min(): Symbol table is empty!");
		return (Key)keys[0];
	}

	@Override
	public Key max() throws EmptySymbolTableException {
		if(isEmpty())
			throw new EmptySymbolTableException("max(): Symbol table is empty!");
		return (Key)keys[size - 1];
	}

	@Override
	public Key floor(Key key) throws EmptySymbolTableException {
		if(isEmpty())
			throw new EmptySymbolTableException("floor(): Symbol table is empty!");
		else if (size == 1)
			return (Key)keys[0];
		else{
			int rank = rank(key);
			if(rank < size && ((Key)keys[rank]).compareTo(key) == 0)
				return (Key)keys[rank];
			else if(rank >= size) // The key would be the largest if it were in the table
				return (Key)keys[rank - 1];
			else if (rank > 0) // The key would not be the largest, but neither the smallest
				return (Key)keys[rank - 1];
			else // The key would be the smallest 
				return null;
		}
	}


	@Override
	public Key ceiling(Key key) throws EmptySymbolTableException {
		if(isEmpty())
			throw new EmptySymbolTableException("ceiling(): Symbol table is empty!");
		int rank = rank(key);
		if(rank >= size) // They key does not occur in the table.
			return null;
		return (Key)keys[rank(key)];
	}

	/**
	 * Central method for <tt>DualArrayOrderedST</tt>s. This method implements binary 
	 * search for the rank of the provided key. If the key is in the table,
	 * it returns its rank (the number of keys that are "smaller than" they key). If it
	 * is not in the table, it essentially returns the size of the table. 
	 * 
	 * @return the rank of the given key. 
	 */
	@Override
	public int rank(Key key){
		return rank(key, 0, size -1);
	}

	/*
	 * Helper method called from rank(Key key). Implements the Binary Search
	 * algorithm to find the given key.
	 */
	private int rank(Key key, int lo, int hi){
		if(hi < lo) 
			return lo;
		int mid = lo + (hi - lo) / 2;
		int cmpRes = ((Key)(key)).compareTo((Key)keys[mid]);
		if(cmpRes > 0) // Key is on the right half of the array
			return rank(key, mid + 1, hi);
		else if(cmpRes < 0) // key is on the left half of the array
			return rank(key, lo, mid - 1);
		else
			return mid;		
	}

	@Override
	public Key select(int k) throws BadRankException{
		if(k < 0 || k >= size)
			throw new BadRankException("select(): Rank of " + k + " is invalid.");
		return (Key)keys[k];
	}

	@Override
	public Iterable<Key> keys(Key lo, Key hi) {
		Queue<Key> kq = new LinkedQueue<Key>();
		int start = rank(lo), end = rank(hi);
		// The for loop needs to stop one position behind the end,
		// because the key "hi" might not actually be in the symbol
		// table. In this case, rank(hi) will have returned the number
		// of keys smaller than hi.
		for(int i = start; i < end; i++)
			kq.enqueue((Key)keys[i]);
		if(start <= end && end < size && 
				((Key)keys[end]).compareTo(hi) == 0) // The "hi" key is actually contained by the table 
			kq.enqueue(hi);
		return kq;
	}

	@Override
	public void put(Key key, Value value) throws KeyIsNullException {
		if(key == null)
			throw new KeyIsNullException("put(): key is null.");
		if(size == keys.length)
			expandCapacities();
		// To place a new key in the table, we first need to 
		// determine its appropriate rank. By calling rank() for
		// every single insertion and moving all subsequent elements
		// to the right, we ensure that the key list remains sorted.
		// The value of the key will reside in the exact same index,
		// but in the values array.
		int keyRank = rank(key);
		if(keyRank < size && key.compareTo((Key)keys[keyRank]) == 0){ // In this case, just change the value. Cheaper than contains()
			values[keyRank] = value;
			return;
		}
		shiftRight(keyRank);
		keys[keyRank] = key;
		values[keyRank] = value;
		size++;
	}

	/* Expand the capacities of both arrays. Used by put(Key key, Value value)
	 * when the new key insertion would otherwise overflow the keys array.
	 */
	private void expandCapacities(){
		Object[] newKeys = new Object[2*keys.length];
		Object[] newValues = new Object[2*values.length];
		for(int i = 0; i < keys.length; i++){
			newKeys[i] = keys[i];
			newValues[i] = values[i];
		}
		keys = newKeys;
		values = newValues;
	}

	/* Shifts the elements of both arrays to the right by one position,
	 * starting from the specified index.
	 */
	private void shiftRight(int pos){
		for(int i = size; i > pos; i--){
			values[i] = values[i - 1];
			keys[i] = keys[i - 1];
		}
	}


	@Override
	public Value get(Key key){
		int keyRank = rank(key);
		if(keyRank < size && ((Key)key).compareTo((Key)keys[keyRank]) == 0)
			return (Value)values[keyRank];
		return null;
	}

	@Override
	public void clear() {
		// Clear both arrays of data.
		for(int i = 0; i < size; i++)
			keys[i] = values[i] = null;
		size = 0;
	}

	/**
	 * For <tt>DualArrayOrderedST</tt>s, we implement "hard deletes".
	 * 
	 * @param key the key of the (key, value) pair to delete from
	 * the symbol table.
	 */
	@Override
	public void delete(Key key){
		int keyRank = rank(key);
		if(keyRank < size && ((Key)key).compareTo((Key)keys[keyRank]) == 0){
			keys[keyRank] = values[keyRank] = null;
			shiftLeft(keyRank);
		}
		size--;
	}

	/* Called by delete(Key key) to shift all elements in
	 * [pos + 1, size - 1] one position to the left.
	 */
	private void shiftLeft(int pos){
		for(int i = pos + 1; i < size; i++){
			keys[i - 1] = keys[i];
			values[i - 1] = values[i];
		}
		keys[size -1] = values[size - 1] = null;
	}

	@Override
	public int size() {
		return size;
	}

}
