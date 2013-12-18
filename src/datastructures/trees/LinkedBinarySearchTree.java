package datastructures.trees;

import java.util.Iterator;
import java.util.ArrayList; // We will use an ArrayList in our pre/in/post/level-order traversals.
import datastructures.queues.*; // Needed for BFS

/**
 * LinkedBinarySearchTree is an implementation of a BinarySearchTree using links.
 * It is by far the most commonly encountered binary tree implementation. Compared to
 * an array-based implementation, there is no memory waste when the tree is not full.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T>
 */
public class LinkedBinarySearchTree<T extends Comparable<T>>  implements BinarySearchTree<T>{

	protected Node<T> root;
	protected int count; // To facilitate size queries.

	/**
	 * Default constructor initializes data structure.
	 */
	public LinkedBinarySearchTree(){
		root = null;
		count = 0;
	}

	/**
	 * ADT-level copy constructor initializes the tree with 
	 * all the elements of the provided tree.
	 * @param other The root of the tree to copy elements from.
	 */
	public LinkedBinarySearchTree(BinarySearchTree<T> other){
		if(other == null)
			throw new RuntimeException("Cannot copy-construct from a null reference");
		for(T el: other)
			add(el);
	}
	
	/**
	 * Standard equals() method. Returns true if the parameter object
	 * is a carbon copy of the current object.
	 * 
	 * @param other the Object to compare with <tt>this</tt>.
	 * 
	 * @return <tt>true</tt> if <tt>other</tt> and <tt>this</tt>
	 * are carbon copies of one another.
	 */
	@Override
	public boolean equals(Object other){
		if(other == null)
			return false;
		if(other.getClass() != getClass())
			return false;
		LinkedBinarySearchTree<T> ocasted = null;
		try {
			ocasted = (LinkedBinarySearchTree<T>)other;
		}catch(ClassCastException exc){
			return false;
		}
		if(ocasted.size() != size())
			return false;
		Iterator<T> currEls = iterator(), otherEls = ocasted.iterator();
		while(otherEls.hasNext())
			if(!otherEls.next().equals(currEls.next()))
				return false;
		return true;
	}
	
	
	@Override
	public T getRoot() throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("getRoot(): Tree is empty.");
		return root.getElement();
	}

	@Override
	public boolean isEmpty() {
		return count == 0;
	}


	@Override
	public int size() {
		return count;
	}

	@Override
	public T find(T element) throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("find(T element): Tree is empty.");
		T el = root.find(element);
		if(el == null)
			return null;
		return el;
	}

	@Override
	public Iterator<T> preorder() throws EmptyTreeException{
		if(root == null)
			throw new EmptyTreeException("preorder(): Tree was empty.");
		ArrayList<T> elementList = new ArrayList<T>();
		root.preorderTraversal(elementList);
		return elementList.iterator();
	}

	@Override
	public Iterator<T> inOrder() throws EmptyTreeException{
		if(root == null)
			throw new EmptyTreeException("inorder(): Tree was empty.");
		ArrayList<T> elementList = new ArrayList<T>();
		root.inorderTraversal(elementList);
		return elementList.iterator();
	}

	@Override
	public Iterator<T> postOrder() throws EmptyTreeException{
		if(root == null)
			throw new EmptyTreeException("postorder(): Tree was empty.");
		ArrayList<T> elementList = new ArrayList<T>();
		root.postorderTraversal(elementList);
		return elementList.iterator();
	}

	@Override
	public Iterator<T> levelOrder() throws EmptyTreeException{
		if(root == null)
			throw new EmptyTreeException("levelorder(): Tree was empty.");
		ArrayList<T> elementList = new ArrayList<T>();
		try {

			LinkedQueue<Node<T>> nodeQueue = new LinkedQueue<Node<T>>();
			nodeQueue.enqueue(root);
			while(!nodeQueue.isEmpty()){
				Node<T> current = nodeQueue.dequeue();
				elementList.add(current.getElement());
				if(current.left != null)
					nodeQueue.enqueue(current.left);
				if(current.right != null)
					nodeQueue.enqueue(current.right);
			}
		} catch(EmptyQueueException exc){
			// Dummy catchblock because dequeue() is complaining.
		}
		return elementList.iterator();
	}

	// In a BinarySearchTree, the left-most element is the smallest one. 
	@Override
	public T getMin() throws EmptyTreeException{
		if(root == null)
			throw new EmptyTreeException("getMin(): tree is empty.");
		Node<T> current = root;
		while(current.getLeft() != null)
			current = current.getLeft();
		return current.getElement();
	}


	// In a BinarySearchTree, the right-most element is the largest one.
	@Override
	public T getMax() throws EmptyTreeException {
		if(root == null)
			throw new EmptyTreeException("getMax(): tree is empty.");
		Node<T> current = root;
		while(current.getRight() != null)
			current = current.getRight();
		return current.getElement();
	}

	@Override
	public void clear() {
		root = null;
		count = 0;
		System.gc();
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> retVal = null;
		try {
			retVal = inOrder();
		} catch(EmptyTreeException exc){
			return null;
		}
		return retVal;
	}

	@Override
	public void add(T element) {
		if(root == null)
			root = new Node<T>(element);
		else
			root.add(element);
		count++;
	}

	/* Removal of a node consists of two things: First, finding it and returning it
	 * to the caller, and, second, the actual removal of it. The second part will be 
	 * attained by Node.remove.
	 */
	@Override
	public T remove(T element) throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("remove(): Tree is empty.");
		T retVal = root.find(element);
		if(retVal == null)
			return null;
		root = root.remove(retVal);
		count--;
		return retVal; 
	}
	
	public int height(){
		return root.height();
	}

	/* The book complicates things unnecessarily here, because it also assumes the
	 * BinaryTree class which has a Node type of its own, declared protected and extended
	 * by the node type of this class. We ditchthe idea of a non-search BinaryTree altogether 
	 * because we find it unrealistic in practice (it does not allow for a well-defined way to 
	 * add or remove elements,rather it depends on combining "singleton" trees).
	 * 
	 * All methods that require some sort of depth-first traversal will be implemented as methods
	 * of the Node<T> class.
	 */
	protected class Node<Type extends Comparable<Type>>{
		protected Type data;
		protected Node<Type> left, right;

		public Node(Type element){
			data = element;
			left = right = null;
		}

		public Node<Type> getLeft(){
			return left;
		}

		public Node<Type> getRight(){
			return right;
		}

		public void setLeft(Node<Type> left){
			this.left = left;
		}

		public void setRight(Node<Type> right){
			this.right = right;
		}

		public Type getElement(){
			return data;
		}

		public int getCount(){
			int count = 1;
			if(left != null)
				count += left.getCount();
			if(right != null)
				count += right.getCount();
			return count;
		}

		public Type find(Type element){
			// Preorder traversal makes most sense when wanting to find an element.
			Type retVal = null;
			if(data.compareTo(element) == 0)
				retVal = data;
			else // Check recursively over elements. Could be null, eventually.
				if(element.compareTo(data) < 0 && left != null)
					retVal=left.find(element);
				else if(element.compareTo(data) > 0 && right != null)
					retVal=right.find(element);
			return retVal;
		}

		/* Binary search trees allow us to add nodes at specific places. The
		 * new node inserted will always be a leaf node.*/
		public void add(Type element){

			// Case 1: If the element to insert is smaller than the currently encountered
			// node, insert it at the left subtree of the node.
			if(element.compareTo(data) < 0){
				if(left == null)
					left = new Node<Type>(element);
				else
					left.add(element);

				// Case 2: If the element to insert is larger than or equal to the currently
				// encountered node, insert it at the right subtree of the node.
			} else if(element.compareTo(data) >= 0){
				if(right == null)
					right = new Node<Type>(element);
				else
					right.add(element);
			}
		}

		/* The following method removes a specific node from the tree
		 * and returns the node that replaces it. It is recursive in nature.
		 */
		public Node<Type> remove(Type element){
			Node<Type> retVal = this;
			if(element.compareTo(data) == 0){// Current node should be removed

				// Case #1: Node is a leaf: return null.
				if(left == null && right == null)
					retVal = null;

				// Case #2: Node is a pre-leaf with left child null: return the right child.
				else if(left == null && right != null)
					retVal = right;

				// Case #3: Node is a pre-leaf with right child null: return the left child.
				else if(left != null && right == null)
					retVal = left;

				// Case #4: Node is an inner node with rooted subtrees. Find its inorder successor
				//		and replace the node with it. Set the left child to whatever was left before
				//		and the right child to whatever was right before.
				else {
					retVal = getInorderSuccessor(); // Guaranteed to exist, because "this" is an inner node.
					retVal.left = left;
					retVal.right = right;
				}
			} else if(element.compareTo(data) < 0){// Element on the left sub-tree
				if(left != null) // if left is null, then the element is simply not in the tree.
					left = left.remove(element);
			} else{ // Element on the right sub-tree
				if(right != null) // same
					right = right.remove(element);
			}
			return retVal;
		}

		// The inorder successor of an inner node is the leftmost node of its right subtree.
		private Node<Type> getInorderSuccessor(){
			Node<Type> current = right;
			while(current.left != null)
				current = current.left;
			right.remove(current.getElement()); // The successor is a leaf node, so it boils down to a previous case.
			return current;
		}

		/* Preorder, inorder and postorder traversal methods. */

		public void preorderTraversal(ArrayList<Type> elements){
			elements.add(data);
			if(left != null)
				left.preorderTraversal(elements);
			if(right != null)
				right.preorderTraversal(elements);
		}

		public void inorderTraversal(ArrayList<Type> elements){
			if(left != null)
				left.inorderTraversal(elements);
			elements.add(data);
			if(right != null)
				right.inorderTraversal(elements);
		}

		public void postorderTraversal(ArrayList<Type> elements){
			if(left != null)
				left.postorderTraversal(elements);
			if(right != null)
				right.postorderTraversal(elements);
			elements.add(data);
		}
		
		public int height(){
			int leftHeight = 1, rightHeight = 1;
			if(left != null)
				leftHeight = left.height() + 1;
			if(right != null)
				rightHeight = right.height() + 1;
			// return the maximum of heights
			return (leftHeight > rightHeight) ? leftHeight : rightHeight;
		}
	}
}
