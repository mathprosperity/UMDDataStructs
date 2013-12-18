/**
 * 
 */
package datastructures.symbol_tables;
import datastructures.lists.*;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * <tt>LinearST</tt> is an implementation of a classic (unordered) symbol table
 * through a linked list. 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @param Key the type of key held by the table.
 * @param Value the type of value held by the table.
 *
 * @since October 2013.
 */

public class LinearST<Key, Value> extends ST<Key, Value> {

	// Private data members:
	private class KeyValuePair{
		Key key;
		Value value;
		KeyValuePair(Key k, Value v){
			key = k;
			value = v;
		}
		@Override
		public boolean equals(Object kvp){
			if(kvp == null)
				return false;
			if(kvp.getClass() != this.getClass())
				return false;
			@SuppressWarnings("unchecked")
			KeyValuePair kvpC = (KeyValuePair)kvp;
			return kvpC.key.equals(key) && kvpC.value.equals(value);
		}
	}
	
	private List<KeyValuePair> data; 
	
	/**
	 * Constructor simply initializes the underlying data structure.
	 */
	public LinearST(){
		data = new LinkedList<KeyValuePair>(); 
	}
	
	/**
	 * ADT-level copy constructor. Takes any {@link ST} as the parameter 
	 * and constructs <tt>this</tt> based on the parameter.
	 * 
	 * @param other The {@link ST} to use as the basis for constructing
	 * the current element.
	 */
	public LinearST(ST<Key, Value> other){
		data = new LinkedList<KeyValuePair>();
		for(Key key: other.keys())
			put(key, other.get(key));
	}
	
	/**
	 * Standard equals() method.
	 * 
	 * @param other The Object to compare <tt>this</tt> to.
	 * @return true if <tt>this</tt> is instance-equal to the parametern.
	 */
	@Override
	public boolean equals(Object other){
		if(other == null)
			return false;
		if(other.getClass() != this.getClass())
			return false;
		LinearST<Key, Value> ocasted = null;
		try{ 
			ocasted = (LinearST<Key, Value>)other;
		} catch(ClassCastException exc){
			return false;
		}
		if(size() != ocasted.size())
			return false;
		Iterator<KeyValuePair> thisData = data.iterator(),
					oData = ocasted.data.iterator();
		while(oData.hasNext()){
			if(!thisData.hasNext())
				return false;
			if(!oData.next().equals(thisData.next()))
				return false;
		}
		return true;
	}
	
	@Override
	public void put(Key key, Value value) throws KeyIsNullException {
		if(key == null)
			throw new KeyIsNullException("put(): key is null!");
		// search for key linearly. Lists are Iterable, so we are saved by that.
		for(KeyValuePair kvp : data){
			if(kvp.key.equals(key)){
				kvp.value = value;
				return;
			}
		}
		data.pushFront(new KeyValuePair(key, value));
	}

	@Override
	public Value get(Key key){
		for(KeyValuePair kvp: data)
			if(kvp.key.equals(key))
				return kvp.value;
		return null;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Iterable<Key> keys() {
		/* Recall that the order of the keys in this symbol table
		 * does not matter: It is implementation-dependent.*/
		ArrayList<Key> keys = new ArrayList<Key>();
		for(KeyValuePair kvp: data)
			keys.add(kvp.key);
		return keys;
	}

	@Override
	public void clear() {
		data.clear();		
	}
	
	/**
	 * For linear symbol tables, we override the default behavior of
	 * delete(Key k) to make it erase the entry from the symbol table.
	 * @param k The key of the entry to be removed from the table.
	 */
	@Override
	public void delete(Key k){
		for(KeyValuePair kvp : data){
			if(kvp.key.equals(k)) {
				// Our implementation allows us to remove an element from a list,
				// as long as we no longer iterate over it. This is the purpose 
				// of the immediate "return" afterwards.
				data.remove(kvp); 
				return;
			}
		}
	}

	
	

}
