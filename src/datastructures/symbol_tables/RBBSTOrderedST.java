package datastructures.symbol_tables;

import datastructures.trees.RedBlackBinarySearchTree;

public class RBBSTOrderedST<Key extends Comparable<Key>, Value> 
				extends BSTOrderedST<Key, Value> {
	
	/* By designing the superclass for inheritance, the only thing
	 * that we need to change is essentially the constructor and copy
	 * constructor, such that the underlying "pairs" reference refers
	 * to a Red-Black Binary Search Tree instead of a standard Binary
	 * Search Tree. The rest of the methods can borrow the superclass'
	 * implementations.
	 */
	
	/**
	 * Constructor initializes the underlying data structure.
	 */
	public RBBSTOrderedST(){
		pairs = new RedBlackBinarySearchTree<KeyValuePair<Key, Value>>();
	}
	
	/**
	 * ADT-Level copy construction.
	 * 
	 * @param other The {@link <tt>OrderedST<Key, Value></tt>} to base 
	 * 	the current object's construction on.  
	 */
	public RBBSTOrderedST(OrderedST<Key, Value> other){
		if(other == null)
			throw new RuntimeException("Cannot create object from null reference.");
		pairs = new RedBlackBinarySearchTree<KeyValuePair<Key, Value>>();
		for(Key key: other.keys())
			put(key, other.get(key));
	}
}
