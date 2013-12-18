package datastructures.trees;
import datastructures.lists.*;
import java.util.Iterator;
/**
 * <p> <tt>RedBlackBinarySearchTree</tt> is a binary tree-based implementation
 * of 2-3 trees. It supports both addition of nodes as well as removal of nodes.
 * </p>
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 *
 * @param <T> the type of element held by the RB_BST.
 * 
 * @since November 2013
 * 
 * @see "Algorithms, 4th ed. by Sedgewick and Wayne."
 */
public class RedBlackBinarySearchTree<T extends Comparable<T>> 
extends LinkedBinarySearchTree<T>{

	private enum Color { RED, BLACK };

	class BSTNode<T2 extends Comparable<T2>> extends Node<T2>{

		protected Color color; // Only new element in BSTNode

		public BSTNode(T2 element, Color color) {
			super(element); // takes care of the pointers
			this.color = color;
		}

	}

	/* Rotation methods necessary for RB_BSTs */ 
	private BSTNode<T> rotateLeft(BSTNode<T> root){
		BSTNode<T> x = (BSTNode<T>)root.right;
		root.right = x.left;
		x.left = root;
		x.color = root.color;
		root.color = Color.RED;
		return x;
	}

	private BSTNode<T> rotateRight(BSTNode<T> root){
		BSTNode<T> x = (BSTNode<T>)root.left;
		root.left = x.right;
		x.right = root;
		x.color = root.color;
		root.color = Color.RED;
		return x;
	}

	/* Is a node connected to via a red link? */
	private boolean isRed(BSTNode<T> node){
		return (node == null || node.color == Color.BLACK) ? false : true;
	}

	
	@Override
	public void add(T element){
		if(root == null)
			root = new BSTNode<T>(element, Color.BLACK);
		else{
			root = add(root, element);
			((BSTNode<T>)root).color = Color.BLACK; 
		}
		count++;
	}
	
	/* Recursive version of add. Behaves in pretty much the same
	 * way as classic recursive add for binary search trees, except
	 * for the fact that it balances the tree on the way up, to 
	 * maintain the invariants of an RB-BST.
	 */

	private BSTNode<T> add(Node<T> root, T element){
		if(root == null) // reached a null link
			return new BSTNode<T>(element, Color.RED);
		if(element.compareTo(root.getElement()) < 0)
			root.left = add(root.left, element); // recursive call down left subtree
		else
			root.right = add(root.right, element);

		// Three cases w.r.t possible rotations

		// (1): Right child is red, left child is black. Rotate left
		// to keep the tree's red links leaning to the left.
		if(isRed((BSTNode<T>)root.right) && !isRed((BSTNode<T>)root.left)) // left could also be null
			root = rotateLeft((BSTNode<T>)root);

		// (2) If (1) (which was executed in a different stack frame)
		// left us with two successive red links on the left, we need to rotate right.
		// TODO: why is the left child BLACK here???
		if(isRed((BSTNode<T>)root.left) && isRed((BSTNode<T>)root.left.left))
			root = rotateRight((BSTNode<T>)root);

		// (3): If (2) and (3) left both children red, in this case
		// we need to make them both black and make the root red.
		// We make a call to flipColors for that.
		if(isRed((BSTNode<T>)root.left) && isRed((BSTNode<T>)root.right))
			splitFourNode((BSTNode<T>)root);
		return (BSTNode<T>)root;
	}

	private void splitFourNode(BSTNode<T> node){
		// The following two lines are guaranteed to not generate a 
		// NullPointerException because splitFourNode() is only
		// called from within a context which requires the node to have
		// two red children.
		((BSTNode<T>)node.left).color = Color.BLACK;
		((BSTNode<T>)node.right).color = Color.BLACK;
		node.color = Color.RED;
	}

	public T remove(T element) throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("remove(T element): cannot remove from an empty tree");
		T el = find(element); // TODO: This can be made more efficient by implementing a version of "find()" that returs a Node object, such that we can immediately find the node we want to delete.
		if(el == null)
			return null;
		if(!isRed((BSTNode<T>)root.left) && !isRed((BSTNode<T>)root.right))
			((BSTNode<T>)root).color = Color.RED;
		root = remove(root, element);
		count--;
		return el;
	}

	/* Recursive removal method. Called by remove(T element). 
	 * Selects the subtree to traverse in exactly
	 * the same fashion as in BSTs. In addition, maintains the invariant
	 * that the current node is not a two-node, calling moveRedRight()
	 * or moveRedLeft() in the process. Calls getInorderSuccessor()
	 * and removeMin() whenever it finds an element identical to the element
	 * sought. Returns null if it does not find the element. 
	 */
	private Node<T> remove(Node<T>root, T element){
		if(element.compareTo(root.getElement()) < 0){ // Need to recurse to the left.
			// If the root to the left is a two-node, enhance it
			// to maintain our invariant.
			if(!isRed((BSTNode<T>)root.left) && !isRed((BSTNode<T>)root.left.left))
				root = moveRedLeft(root);
			root.left =  remove(root.left, element);
		} else{ // Look at the right or at the current node.
			
			// The following if condition makes it possible to create temporary
			// four nodes, if required, down our way on the right subtree.
			if(isRed((BSTNode<T>)root.left))
				root = rotateRight((BSTNode<T>)root);
			
			// Found the element and there's nothing to the right. Done.
			if(element.compareTo(root.getElement()) == 0 && root.right == null) 
				return null;
			
			// Or maybe we didn't find it yet and there's stuff on the right.
			// In this case, we need to maintain the invariant.
			if(!isRed((BSTNode<T>)root.right) && !isRed((BSTNode<T>)root.right.left))
				root = moveRedRight(root);
			
			// Or maybe we found it and there's still stuff on the right, in
			// which case we need to find the inorder successor of the node
			// and then call removeMin() for the right subtree of the node.
			if(element.compareTo(root.getElement()) == 0){
				Node<T> inSucc = getInorderSuccessor(root);
				root.data = inSucc.getElement();
				root.right = removeMin(root.right);
			// In any other case, we simply need to recurse to the right
			} else
				root.right = remove(root.right, element);
		}
		// Split any four-nodes on our way up and make sure that
		// the RB_BST leans to the left.
		return balance(root);
	}

	private Node<T> getInorderSuccessor(Node<T> root){
		Node<T> current = root.right;
		while(current.left != null)
			current = current.left;
		return current;
	}

	/*
	 * Removes the minimum element from a Red-Black BST.
	 * Maintains the invariant that the current node is NOT 
	 * a 2-node. More details at Sedgewick and Wayne, 4th ed.
	 */
	private Node<T> removeMin(Node<T> root){
		// If we've reached a leaf node, we can simply erase it.
		if(root.left == null)
			return null;
		// If the left child is a 2-node, we need to move an element from
		// the immediate right sibling to that child.
		if(!isRed((BSTNode<T>)root.left) && !isRed((BSTNode<T>)root.left.left))
			root = moveRedLeft(root);
		root.left = removeMin(root.left); // Recurse to the left
		return balance(root); // On our way up the tree, we need to balance any temporary 4-nodes
	}

	/* Used by removeMin() to help maintain the invariant that the current
	 * node scanned is not a 2-node. Essentially moves an element from the right
	 * sibling of a 2-node to that 2-node, making it into a 3-node. If that's not
	 * possible, merges both children with the parent, creating a 4-node that will
	 * be balanced after the recursive process.
	 */

	private Node<T> moveRedLeft(Node<T> parent){
		createFourNode((BSTNode<T>)parent); // Make a temporary 4-node 
		if(isRed((BSTNode<T>)parent.right.left)){ // Was the right sibling a three-node?
			parent.right = rotateRight((BSTNode<T>)parent.right);//If so, move the smallest element to the root and the root element to the left child, via rotations.
			parent = rotateLeft((BSTNode<T>)parent.left);
		}
		return parent;
	}

	/* Remove the maximum node from an RB-BST. Method symmetrical
	 * to removeMin(Node<T>). Maintains the invariant that the current node
	 * is not a 2-node, by creating temporary 4-nodes on the way down 
	 * the tree or by moving elements from the immediate left sibling
	 * of the rightmost node to the node itself.
	 */

	@SuppressWarnings("unused")
	private Node<T> removeMax(Node<T> root){
		if(isRed((BSTNode<T>)root.left))
			root = rotateRight((BSTNode<T>)root);
		if(root.right == null) // reached maximum node
			return null;
		// If the right child is a 2-node, we need to move an element from
		// the immediate left sibling to that child.
		if(!isRed((BSTNode<T>)root.right) && !isRed((BSTNode<T>)root.right.left))
			root = moveRedRight(root);
		root.right = removeMax(root.right); // Recurse to the right
		return balance(root); // On our way up the tree, we need to balance any temporary 4-nodes
	}

	/* Move an element from a three-node to the two-node on its right,
	 * or create a temporary four-node and leave it like that. Symmetrical
	 * to moveRedLeft(Node<T> n).
	 */

	private Node<T> moveRedRight(Node<T> parent){
		createFourNode((BSTNode<T>)parent);
		if(!isRed((BSTNode<T>)parent.left.left))
			parent = rotateRight((BSTNode<T>)parent);
		return parent;
	}

	/* The symmetrical method of splitFourNode() creates temporary
	 * 4-nodes as we go down the tree. Those 4-nodes will be split
	 * by the balance() method after we remove the minimum node.
	 */
	private void createFourNode(BSTNode<T> node){
		node.color = Color.BLACK;
		((BSTNode<T>)node.left).color = Color.RED;
		((BSTNode<T>)node.right).color = Color.RED;
	}

	/* This method balances the nodes by (i) making sure the RB-BST
	 * leans to the left and (ii) any 4-nodes created during the deletion
	 * process get split on its way up to the root.
	 */
	private Node<T> balance(Node<T> node){
		if(isRed((BSTNode<T>)node.right))
			node = rotateLeft((BSTNode<T>)node);

		// Three recursive cases from the recursive "add" method.
		if(isRed((BSTNode<T>)node.right) && !isRed((BSTNode<T>)node.left)) // left could also be null
			node = rotateLeft((BSTNode<T>)node);
		if(isRed((BSTNode<T>)node.left) && isRed((BSTNode<T>)node.left.left))
			node = rotateRight((BSTNode<T>)node);
		if(isRed((BSTNode<T>)node.left) && isRed((BSTNode<T>)node.right))
			splitFourNode((BSTNode<T>)node);

		return node;
	}
	
	/**
	 * <p>Returns <tt>true</tt> if the current tree is an RB-BST, 
	 * <tt>false otherwise.</p>
	 * <p> To answer the question of whether the binary tree is an RB-BST, 
	 * <tt>isRBBST(BSTNode<T> root)</tt> checks to see if the tree preserves
	 * perfect black balance. The total ordering property can be checked against
	 * by checking the node traversals defined for @link{LinkedBinarySearchTree}
	 * after removing or adding elements.
	 * @throws EmptyTreeException if the tree is empty.
	 * @return <tt>true</tt> if the tree is an RB-BST.
	 */
	public boolean isRBBST() throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("isRBBST(): tree is empty!");
		LinkedList<Integer> pathLengths = new LinkedList<Integer>();
		// All black lengths should be the same, otherwise the RB-BST 
		// will not be preserving perfect black height. The call to 
		// "gatherLengths" fills in the list with the number of black edges
		// for every single path from tree root to null. Subsequently,
		// we assert that the list essentially replicates the same Integer;
		// if not, this means that some path differentiated itself and the
		// tree does not retain perfect black height.
		gatherLengths((BSTNode<T>)root, 0, pathLengths);

		// We are guaranteed that there's gonna be at least one element
		// in the list, because if there weren't, that would mean that
		// the tree is empty and the previous exception would've been thrown.
		Iterator<Integer> it = pathLengths.iterator();
		Integer currVal = it.next();
		while(it.hasNext()){
			Integer nextVal = it.next();
			if(!nextVal.equals(currVal))
				return false;
			currVal = nextVal;
		}
		return true;
	}
	
	private void gatherLengths(BSTNode<T> root, int currBlackHeight, 
				List<Integer> pathLengths){
		
		// Let's check the left subtree first
		if(root.left != null)
			if(!isRed((BSTNode<T>)root.left))
				gatherLengths((BSTNode<T>)root.left, currBlackHeight + 1, pathLengths);
			else
				gatherLengths((BSTNode<T>)root.left, currBlackHeight, pathLengths);
		else
			pathLengths.pushBack(currBlackHeight);
		
		// And the right second.
		if(root.right != null)
			if(!isRed((BSTNode<T>)root.right))
				gatherLengths((BSTNode<T>)root.right, currBlackHeight + 1, pathLengths);
			else
				gatherLengths((BSTNode<T>)root.right, currBlackHeight, pathLengths);
		else
			pathLengths.pushBack(currBlackHeight);
	}
}




