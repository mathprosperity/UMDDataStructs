package datastructures.symbol_tables;

/**
 * <p><tt>OrderedST</tt> is an ordered symbol table ADT. An ordered symbol table
 * induces a partial ordering among its keys, and this is the reason for which
 * we've made keys <tt>Comparable</tt> in this interface. Ordered symbol tables
 * provide for a richer interface than classic symbol tables, as suggested by
 * the key ordering. 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since October 2013
 *
 * @param <Key> The type for the key member in the <key, value> pairs of this symbol table.
 * @param <Value> The type for the value member in the <key, value> pairs of this symbol table.
 */
public abstract class OrderedST<Key extends Comparable<Key>, Value> extends ST<Key, Value> {
	
	/**
	 * Find and return the minimum key in the table.
	 * @return The minimum key in the table.
	 * @throws EmptySymbolTableException if there is no min key. 
	 */
	public abstract Key min() throws EmptySymbolTableException;
	
	/**
	 * Find and return the maximum key in the table.
	 * @return The maximum key in the table.
	 */
	public abstract Key max() throws EmptySymbolTableException;
	
	/**
	 * Find the largest key that is smaller than or equal to the given key.
	 * If no such key exists, the method should return null.
	 * @param key The key to serve as the upper bound in our search.
	 * @return The largest key that satisfies the aforementioned criteria.
	 */
	public abstract Key floor(Key key) throws EmptySymbolTableException;
	
	/**
	 * Find the smallest key that is larger than or equal to the given key.
	 * If no such key exists, the method should return null. 
	 * @param key The key to serve as the lower bound in our search.
	 * @return The smallest key that satisfies the aforementioned criteria.
	 */
	public abstract Key ceiling(Key key) throws EmptySymbolTableException;
	
	/**
	 * Return the rank of the key in the sorted key list.
	 * @param key The key to return the rank of.
	 * @return The rank of the key in the sorted key list.
	 */
	public abstract int rank(Key key);
	
	/**
	 * Find and return the key at rank k.
	 * @param k The rank of the key to search for.
	 * @return The key at the provided rank in the sorted key list.
	 */
	public abstract Key select(int k) throws BadRankException; // TODO: Determine what to do if there is no key at that exact rank
	
	/**
	 * Delete the entry corresponding to the minimum key in the table.
	 */
	public void deleteMin() throws EmptySymbolTableException{
		delete(min());
	}
	
	/**
	 * Delete the entry corresponding to the maximum key in the table.
	 */
	public void deleteMax() throws EmptySymbolTableException{
		delete(max());
	}
	
	/**
	 * Find and return the number of elements between keys lo and hi
	 * inclusive.
	 * @param lo The lower bound key to start the search from.
	 * @param hi The higher bound key to end the search at.
	 * @return The number of elements in the interval [lo, hi].
	 */
	public int size(Key lo, Key hi){
		if(lo.compareTo(hi) > 0  || !contains(lo))
			return 0;
		// Note that since keys are unique, it is not 
		// possible that compareTo() will return 0
		else{ 
			// If hi is actually in the symbol table, then the range
			// is [lo. hi]
			if(contains(hi))
				return rank(hi) - rank(lo) + 1;
			else // If not, the range is [lo, current_last]
				return rank(hi) - rank(lo);
				
		}
	}
	
	/**
	 * Provide iteration of all keys between lo and hi inclusive.
	 * Since the symbol table is ordered, the order of the keys should
	 * also be sorted. "hi" and "lo" are not required to be in the table.
	 * 
	 * 
	 * @param lo The lower bound of the key interval to iterate over.
	 * @param hi The higher bound of the key interval to iterate over.
	 * @return An Iterable object containing all keys in [lo, hi].
	 */
	public abstract Iterable<Key> keys(Key lo, Key hi);
	
	
	@Override
	public Iterable<Key> keys(){
		return keys(min(), max());
	}
}
