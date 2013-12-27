/**
 * 
 */
package datastructures.symbol_tables;

import datastructures.trees.EmptyTreeException;
import datastructures.trees.Trie;

/**
 * A <tt>StringST</tt> is a symbol table with {@link CharSequence} keys
 * and <tt>Value</tt> elements. It is based on an underlying {@link datastructures.trees.Trie}
 * data structure, making insertion, deletion and lookup dependent on key length, instead of
 * element count.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * @param Key a {@link CharSequence} that 
 * @param Value the type of value for the symbol table.
 */
public class StringST<Key extends CharSequence, Value> extends ST<Key, Value> {

	private Trie<Value> trie;
	
	/**
	 * Default constructor simply initializes the data structure.
	 */
	public StringST(){
		trie = new Trie<Value>();
	}
	
	/**
	 * This constructor allows the user of the class to supply the StringST's
	 * alphabet size.
	 * @param alphabetSize
	 */
	public StringST(int alphabetSize){
		trie = new Trie<Value>(alphabetSize);
	}
	
	@Override
	public void put(Key key, Value value) throws KeyIsNullException {
		if(key == null)
			throw new KeyIsNullException("put(Key, Value): key is null.");
		trie.insert(key, value);
	}

	@Override
	public Value get(Key key) {
		try {
			return trie.find(key);
		} catch (EmptyTreeException e) {
			return null;
		}
	}

	@Override
	public int size() {
		return trie.size();
	}

	@Override
	public Iterable<Key> keys() {
		return (Iterable<Key>) trie.keysWithPrefix(""); // gets all keys
	}

	@Override
	public void clear() {
		trie.clear();
	}
	
	@Override
	public void delete(Key key){
		trie.delete(key);
	}
	
}
