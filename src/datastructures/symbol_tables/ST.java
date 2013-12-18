/**
 * 
 */
package datastructures.symbol_tables;

/**
 * 
 * <p><tt>ST</tt> is a symbol table (key-value store) ADT. It provides methods
 * for inserting a key-value pair in the table, getting the value associated with
 * a key, deleting entries, and querying the structure for the containment of a key
 * or for its size and emptiness. Note that symbol tables are not required to
 * be ordered (i.e their keys are not <tt>Comparable</tt>).</p>
 * 
 * <p>There are no duplicate keys in a symbol table. Inserting a key-value pair
 * where the key is already contained within the table results in the value
 * being overwritten.</p>
 * 
 * <p>We define ST as an abstract class because some of its methods have default
 * implementations we would like to stick to.</p>
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since October 2013
 * 
 * @param Key the type of Key held by the symbol table.
 * @param Value the type of Value held by the symbol table.
 */

public abstract class ST<Key, Value>{
	
	/**
	 * Insert a key-value pair in the symbol table. If the key is already
	 * in the table, it updates the value of the key-value pair.
	 * @param key the key to insert
	 * @param value the value associated with the key.
	 * @throws KeyIsNullException if the key provided is null. We do not allow for null keys.
	 */
	public abstract void put(Key key, Value value) throws KeyIsNullException; 
	
	/**
	 * Gets the value associated with the key provided.
	 * @param key the key to search the value of.
	 * @return The associated value, or null if the key is not in the store.
	 */
	public abstract Value get(Key key);
	
	/**
	 * Delete the entry corresponding to the key
	 * @param key the key that dictates which entry we should delete.
	 */
	public void delete(Key key){
		put(key, null);
	}
	
	/**
	 * Checks if the symbol table contains the specified key.
	 * @param key The key to search for.
	 * @return true if the symbol table contains the key, false otherwise.
	 */
	public boolean contains(Key key){
		return get(key) != null;
	}
	
	/**
	 * Queries the structure for emptiness.
	 * @return true if the structure is empty, false otherwise.
	 */
	public boolean isEmpty(){
		return size() == 0;
	}
	
	/**
	 * Queries the structure for its size.
	 * @return the number of key-value pairs contained in the structure.
	 */
	public abstract int size();
	
	/**
	 * Provide key iteration capability.
	 * @return An Iterable object containing the keys in the structure.
	 */
	public abstract Iterable<Key> keys();
	
	/**
	 * Clear the data structure of elements.
	 */
	public abstract void clear();
}
