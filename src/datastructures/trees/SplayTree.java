package datastructures.trees;


/**
 * A <tt>SplayTree</tt> is a {@link datastructures.trees.LinkedBinarySearchTree} which strives to maintain
 * amortized logarithmic complexity. That is, the total cost of m insertions, deletions or searches
 * will be O(mlogn), where n is the maximum number of nodes in the tree at any time. Unlike {@link datastructures.trees.AVLBinarySearchTree},
 * it doesn't need to store height information in its nodes.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The <tt>Comparable</tt> type held by the container.
 */
public class SplayTree<T extends Comparable<T>> extends LinkedBinarySearchTree<T>{

	/**
	 * Splaying is the chief operation in splay trees. It searches for the node that contains the element
	 * provided. If it finds it, it ascends it to the top of the tree. If it does not, then the node that
	 * contains either the preceding or following key in the sorted key list ascends to the top of the tree.
	 *  
	 * @param root The root of the subtree currently examined.
	 * @param element The element that we are searching for.
	 * @return The node that contains either the element itself or its preceding or following element in the 
	 * sorted key list.
	 */
	private Node<T> splay(Node<T> root, T element){
		if(element.compareTo(root.data) < 0){
			if(root.left == null)
				return root; // The element is not in the tree; ascend its successor
			else{
				root.left = splay(root.left, element); // The element might be in the tree; keep looking.
				return rotateRight(root); // Rotate the current root to the right to make the element ascend to the tree's root.
			}
		} else if(element.compareTo(root.data) > 0){
			if(root.right == null)
				return root;
			else{
				root.right = splay(root.right, element); // Symmetric case
				return rotateLeft(root);
			}
		} else // Found the element; simply return the current node.
			return root;
	}

	private Node<T> rotateLeft(Node<T> node){
		Node<T> x = node.right;
		node.right = x.left;
		x.left = node;
		return x;
	}

	private Node<T> rotateRight(Node<T> node){
		Node<T> x = node.left;
		node.left = x.right;
		x.right = node;
		return x;
	}

	@Override
	public T find(T element) throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("find(T): splay tree is empty.");
		root = splay(root, element);
		if(root.data.compareTo(element) == 0)
			return root.data;
		else
			return null;
	}

	@Override
	public T remove(T element){
		if(isEmpty())
			return null;
		root = splay(root, element);
		if(root.data.compareTo(element) == 0){ // The element ascended is indeed the element to be deleted.
			if(root.left == null) // element was the smallest key in the tree already.
				root = root.right; // Simply make the root point to its right child.
			else{
				root.left = splay(root.left, element); // Will ascend the immediate predecessor to the left child of the root.
				Node<T> prevRight = root.right; // The new root will have that predecessor as its left child and the same right child.
				root = root.left;
				root.right = prevRight;
			}
			count--;
			return element;
		} 
		return null; // If we didn't find the element, just return null.
	}

	@Override 
	public void add(T element){
		if(isEmpty())
			root = new Node<T>(element);
		else{
			root = splay(root, element);
			Node<T> oldRoot = root;
			if(element.compareTo(root.data) < 0){ // The root contains the immediate successor of our element.
				Node<T> oldRootLeft = root.left;
				root = new Node<T>(element);
				root.right = oldRoot;
				root.right.left = null;
				root.left = oldRootLeft;
			} else{ // The root contains either the element itself or an immediate predecessor. The symmetric case occurs.
				Node<T> oldRootRight = root.right;
				root = new Node<T>(element);
				root.left = oldRoot;
				root.left.right = null;
				root.right = oldRootRight;
			}
		}
		count++;
	}
}
