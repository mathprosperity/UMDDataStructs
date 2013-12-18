package datastructures.trees;

/**
 * <p><tt>AVLBinarySearchTree</tt> is an implementation of an <b>A</b>delskon - 
 * <b>V</b>elskii - <b>L</b>andis (AVL) BST. These trees use rotation operations
 * to maintain the invariant that after every insertion or deletion, the siblings
 * of the tree are at most one level apart of one another. Such trees therefore
 * approximate logn height.
 *  
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> The Comparable Type held by the <tt>AVLBinarySearchTree</tt> container.
 */
public class AVLBinarySearchTree<T extends Comparable<T>> 
extends LinkedBinarySearchTree<T>{

	/* A type for an AVL tree node */
	protected class AVLNode<T2 extends Comparable<T2>> extends Node<T2>{

		protected int height; // AVL nodes need a height.

		// Constructors
		public AVLNode(T2 element, int heightIn) {
			super(element); // takes care of the pointers
			height = heightIn;
		}

		public AVLNode(T2 element){ 
			this(element, 0); // leaf nodes have height 0 
		}

		@Override
		public int height(){
			return height;
		}

	}

	private int height(Node<T> node){
		return (node == null) ? -1 : ((AVLNode<T>)node).height();
	}

	@Override
	public void add(T element) {
		if(root == null)
			root = new AVLNode<T>(element);
		else
			root = add((AVLNode<T>)root, element); 
		count++;
	}

	/* Recursive add() method. Returns an AVLNode<T> type which points
	 * to the root of the updated subtree at every stack frame. Calls
	 * rotation methods after the appropriate node insertion to preserve
	 * the AVL tree invariant.
	 */
	private AVLNode<T> add(AVLNode<T> node, T element){

		if(node == null)
			return new AVLNode<T>(element);
		if(element.compareTo(node.data) < 0){
			node.left = add((AVLNode<T>)node.left, element);

			// Did our addition cause an imbalance?			
			if(height(node.left) - height(node.right) == 2){

				// What was the source of the imbalance? To find it,
				// we need to understand exactly in which subtree
				// we inserted the element; left-left or left-right?
				if(element.compareTo(node.left.data) < 0)
					node = rotateRight(node);
				else{
					node.left = rotateLeft((AVLNode<T>)node.left);
					node = rotateRight(node);
				}
			}
			// Update the current node's height.
			int maxHeight = height(node.left) > height(node.right) ? height(node.left) : height(node.right);
			node.height = maxHeight + 1;
		} else{ // Symmetric cases
			node.right = add((AVLNode<T>)node.right, element);
			if(height(node.right) - height(node.left) == 2){
				if(element.compareTo(node.right.data) > 0)
					node = rotateLeft(node);
				else{
					node.right = rotateRight((AVLNode<T>)node.right);
					node = rotateLeft(node);
				}	
			}
			// Update the current node's height.
			int maxHeight = height(node.left) > height(node.right) ? height(node.left) : height(node.right);
			node.height = maxHeight + 1;
		}
		return node;
	}

	/* Rotation methods. Fun! */
	private AVLNode<T> rotateLeft(AVLNode<T> node){
		AVLNode<T> x = (AVLNode<T>)node.right;
		node.right = x.left;
		x.left = node;
		x.height = node.height;
		node.height--;
		return x;
	}

	private AVLNode<T> rotateRight(AVLNode<T> node){
		AVLNode<T> x = (AVLNode<T>)node.left;
		node.left = x.right;
		x.right = node;
		x.height = node.height;
		node.height--;
		return x;
	}

	@Override
	public T remove(T element) throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("remove(): Tree is empty.");
		T retVal = root.find(element);
		if(retVal == null)
			return null;
		root = remove((AVLNode<T>)root, retVal);
		count--;
		return retVal; 
	}

	/* Recursive removal method. Returns an AVLNode<T> representing
	 * the subtree which may or may not have been balanced after the
	 * removal of a node. Calls rotation methods to achieve this.
	 * 
	 * When the element to be removed is found in the tree, the method
	 * behaves just as in a regular BST: we have a bunch of different cases,
	 * and we may have to scan the tree for the inorder successor of the node
	 * to be removed. It is after the recursive calls down a left or right
	 * subtree that we need additional work to preserve the balance condition
	 * of AVL trees.
	 */

	private Node<T> remove(AVLNode<T> node, T element){
		if(element.compareTo(node.data) == 0){
			if(node.left == null && node.right == null)
				return null;
			else if(node.left == null && node.right != null){
				//((AVLNode<T>)node.right).height = node.height;
				return node.right; 
			}
			else if(node.right == null && node.left != null){
				//((AVLNode<T>)node.left).height = node.height;
				return node.left; 
			}
			// inorder successor case
			Node<T> inSucc = getInorderSuccessor(node);
			node.right = remove((AVLNode<T>)node.right, inSucc.getElement());
			inSucc.left = node.left;
			inSucc.right = node.right;
			((AVLNode<T>)inSucc).height = node.height;
			return inSucc;
		} else if(element.compareTo(node.data) < 0){
			node.left = remove((AVLNode<T>)node.left, element);
			// Do we need to balance the current subtree after the removal?
			if(height(node.right) - height(node.left) == 2){
				int rightSubTreeBalance = height(node.right.right) - height(node.right.left); 
				if(rightSubTreeBalance == -1){ // Is our right subtree left-leaning?
					node.right = rotateRight((AVLNode<T>)node.right);
					node = rotateLeft(node);
				}else // 0 or 1
					node = rotateLeft(node);
			}
			// Whether we balanced or not, we need to update 
			// the current node's height.
			int maxHeight = height(node.left) > height(node.right) ? height(node.left) : height(node.right);
			node.height = maxHeight + 1;
		} else if(element.compareTo(node.data) > 0){
			node.right = remove((AVLNode<T>)node.right, element);
			// Symmetric case of previous else if condition.
			if(height(node.left) - height(node.right) == 2){
				int leftSubTreeBalance = height(node.left.right) - height(node.left.left);
				if(leftSubTreeBalance == -1)// left-leaning left subtree
					node = rotateRight(node);
				else{ // perfectly balanced or right-leaning left subtree
					node.left = rotateLeft((AVLNode<T>)node.left);
					node = rotateRight(node);
				}
			}
			int maxHeight = height(node.left) > height(node.right) ? height(node.left) : height(node.right);
			node.height = maxHeight + 1;
		}
		return node;
	}
	
	private Node<T> getInorderSuccessor(Node<T> node){
		// right child guaranteed to exist by caller 
		Node<T> temp = node.right;
		while(temp.left != null)
			temp = temp.left;
		return temp;
	}

}
