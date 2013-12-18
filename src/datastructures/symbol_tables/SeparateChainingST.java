package datastructures.symbol_tables;

import datastructures.lists.LinkedList;

/**
 * <p><tt>SeparateChainingST<Key, Value></tt> is a hashing-based
 * implementation of an unordered symbol table. The internal data structure
 * is an array containing references to {@link LinearST} objects.
 * It exposes methods that allow for the control of the size of the table.</p>
 * 
 * <p> We use the solution suggested in Sedgewick and Wayne, chapter 3.4, for computing
 * the hash function for our key type. Namely, we use the hashCode() implementation
 * for the Key type, mask its highest bit to remove the sign, then divide that quantity
 * with the size of the table and take its modulus. It would be most wise to always make sure
 * that <emph>M</emph>, the size of the table, remains prime even after potential resizings.
 * Maintaining <emph>M</emph> prime leads to a hash function that tends to more evenly
 * distribute the keys on the table.</p>
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <Key> The key type for the symbol table.
 * @param <Value> The value type for the symbol table.
 * @see "Algorithms, 4th ed. by Sedgewick and Wayne"
 * @since December 2013
 */
public class SeparateChainingST<Key, Value> extends ST<Key, Value> {

	private LinearST<Key, Value>[] lists;
	private static final int DEFAULT_CAPACITY = 97;
	private int count;
	
	public SeparateChainingST(int capacity){
		lists = (LinearST<Key, Value>[])new LinearST[capacity];
		for(int i = 0; i < lists.length; i++)
			lists[i] = new LinearST<Key, Value>();
		count = 0;
	}
	
	public SeparateChainingST(){
		this(DEFAULT_CAPACITY);
	}
	
	/* Sedgewick and Wayne's suggestion for the hash function: */
	private int hash(Key key){
		return (key.hashCode() & 0x7fffffff) % lists.length;
	}
	
	@Override
	public void put(Key key, Value value) throws KeyIsNullException {
		if(key == null)// we don't allow for null keys, remember
			throw new KeyIsNullException("put(): key was null.");
		int index = hash(key);
		int sizePrior = lists[index].size();
		lists[index].put(key, value);
		if(lists[index].size() == sizePrior + 1){
			count++;
			if(count >= lists.length / 2)
				resize(2*lists.length);
		}
	}

	@Override
	public Value get(Key key) {
		return lists[hash(key)].get(key); // (O(N/M))
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public Iterable<Key> keys() {
		// Store all they keys in an Iterable, return the Iterable.
		// We will use one of our own lists as the Iterable.
		LinkedList<Key> keys = new LinkedList<Key>();
		for(int i = 0; i < lists.length; i++)
			for(Key key : lists[i].keys())
				keys.pushFront(key); // pushFront() for efficiency
		return keys; 
	}

	@Override
	public void clear() {
		for(LinearST<Key, Value> st : lists)
			st.clear();
		count = 0;
	}
	
	@Override
	public void delete(Key key){
		int index = hash(key);
		int sizePrior = lists[index].size();
		lists[index].delete(key);
		if(lists[index].size() == sizePrior - 1){
			count--;
			if(count <= lists.length / 8)
				resize(lists.length / 2);
		}
		
	}
	
	/**
	 * Resize the symbol table to make {@link #put} and {@link #get} more efficient
	 * (in the case of table enlargement) or conserve memory (in the case of 
	 * diminishment).
	 * 
	 * @param newCapacity The new capacity that the symbol table should have.
	 */
	private void resize(int newCapacity){
		SeparateChainingST<Key, Value> temp = new SeparateChainingST<Key, Value>(newCapacity);
		for(LinearST<Key, Value> st: lists)
			for(Key key : st.keys())
				temp.put(key, st.get(key));
		lists = temp.lists; // reference copy just fine
		count = temp.count; 
	}

}
