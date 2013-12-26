package datastructures.trees;
import java.util.Iterator;

/**
 * A standard interface for a Tree data structure. No assumption is made about
 * whether the Tree is n-ary with a particular choice of n, whether it is balanced,
 * complete, full, etc. Classes implementing this interface will specify
 * the specific functionality and state of the Tree as well as its internal data storage
 * mechanisms. 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The type of Object held by the Tree data structure.
 * 
 * @since October 2013
 */
public interface Tree<T> extends Iterable<T> {
	
	/**
	 * Return the element at the root of the tree.
	 * @return The element at the root of the tree.
	 * @throws EmptyTreeException if the tree is empty.
	 */
	public T getRoot() throws EmptyTreeException;
	
	/**
	 * Queries the tree for emptiness
	 * @return true if the tree contains no elements
	 */
	public boolean isEmpty();
	
	
	/**
	 * Query the tree for its size (number of elements)
	 * @return The number of elements in the tree.
	 */
	public int size();
		
	/**
	 * Find the element in the tree. Throws an exception if it doesn't find it.
	 * @param element The element to be found
	 * @return A reference to the element in the tree and null if it's not there.
	 * @throws EmptyTreeException if the tree is empty.
	 */
	public T find(T element) throws EmptyTreeException;
	
	/**
	 * Generates a preorder traversal for the tree.
	 * @return An iterator which traverses the tree in preorder
	 * fashion. (left subtree-> node -> right subtree).
	 * @throws EmptyTreeException if the heap is empty.
	 */
	public Iterator<T> preorder() throws EmptyTreeException;
	
	/**
	 * Generates an inorder traversal for the tree.
	 * @return An iterator which traverses the tree in inorder
	 * fashion. (left subtree->node->right subtree).
	 * @throws EmptyTreeException if the heap is empty.
	 */
	public Iterator<T> inOrder() throws EmptyTreeException;
	
	/**
	 * Generates a postorder traversal for the tree.
	 * @return An iterator which traverses the tree in postorder
	 * fashion. (left subtree->right subtree -> node).
	 * @throws EmptyTreeException if the heap is empty.
	 */
	public Iterator<T> postOrder() throws EmptyTreeException;
	
	/**
	 * Generates a breadth-first search (BFS) of the tree (level-order
	 * traversal).
	 * @return An iterator which traverses the tree breadth-first.
	 * @throws EmptyTreeException if the heap is empty.
	 */
	public Iterator<T> levelOrder() throws EmptyTreeException; 
	
	/**
	 * Clears the tree of all its elements.
	 */
	public void clear();
}
