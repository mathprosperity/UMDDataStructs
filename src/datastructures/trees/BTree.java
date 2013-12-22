package datastructures.trees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import datastructures.queues.*;

/**
 * <p><tt>BTree</tt> is an implementation of a B-tree. B-trees are 
 * parameterized by an integer <emph>M</emph>, which dictates the node
 * width, i.e the number of keys in a node. They generalize binary
 * trees and are guaranteed to be balanced. Key rotations, node splits
 * and merges are used to maintain the balance.</p> 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @param T the Comparable type for the B-tree's keys.
 * 
 *  @since December 2013
 */
public class BTree<T extends Comparable<T>> implements Tree<T> {

	/**
	 * A class to represent a B-Tree node. Holds information for
	 * the number of children in this node, as well as the keys
	 * and actual children of the node.
	 * 
	 * For efficiency and readability purposes, we will allow the nodes to hold two "slack"
	 * references, one bogus key reference and one bogus subtree reference.
	 * The reason for this is that, when having to create temporary overflowing
	 * nodes, the presence of bogus references will make the relevant operations
	 * a lot faster. Therefore, a user of this class should know that an approximate
	 * 8*N byte overhead is to be expected, where N is the number of nodes
	 * in the BTree. Java ArrayLists are used to implement the key and children arrays,
	 * for code readability and efficiency purposes.
	 * @author Jason Filippou (jasonfil@cs.umd.edu)
	 *
	 * @param <T2> The Comparable type held by the object.
	 */
	private class BTreeNode<T2 extends Comparable<T2>>{

		ArrayList<T2> keys;
		ArrayList<BTreeNode<T2>> children;
		private final static int KEY_NOT_FOUND = -1;

		/**
		 * A constructor that initializes a new BTreeNode
		 * with a starting key.
		 * 
		 * @param startingKey The first key in the node.
		 */
		BTreeNode(T2 startingKey) {

			keys = new ArrayList<T2>(M); // One too many
			children = new ArrayList<BTreeNode<T2>>(M + 1); // One too many
			keys.add(startingKey);
			keys.trimToSize();
			children.trimToSize();
		}

		/**
		 * A default constructor that simply allocates
		 * space for a new node.
		 */
		BTreeNode(){
			keys = new ArrayList<T2>(M); // One too many
			children = new ArrayList<BTreeNode<T2>>(M + 1); // One too many
			keys.trimToSize();
			children.trimToSize();
		}

		/**
		 * Returns true if the current node is a leaf of the tree.
		 * 
		 * @return true if the node is a leaf of the tree, i.e it does
		 * 	not have any children.
		 */
		boolean isLeaf(){ return children.isEmpty();}

		/**
		 * Returns true if the current node is full, i.e it cannot take
		 * any more keys.
		 * 
		 * @return true if the number of keys in the node exceeds the maximum of M - 1. 
		 */
		boolean overFlows(){ return keys.size() > M - 1; } // M - 1 keys max per node

		/**
		 * Returns true if the current node underflows, i.e it contains less than
		 * floor(M/2) keys.
		 * @return true if the node has less than ceil(M/2) - 1 keys.
		 */
		boolean underFlows() { return (M % 2 == 0)?  keys.size() < M/2 - 1 : keys.size() < M / 2; }

		/**
		 * Returns true if the node is full, i.e it contains exactly M - 1 keys. Used by key
		 * rotations to check whether it is possible to rotate a key to the current node.
		 * @return true if the number of keys in the node is exactly M - 1.
		 */
		boolean isFull() { return keys.size() == M - 1; }
		
		/**
		 * Returns true if the node is at its minimum capacity. Used by the rotation methods
		 * to determine whether a left or right rotation from the current node is possible.
		 * 
		 * @return true if the node has exactly ceil(M/2) - 1 keys.
		 */
		boolean minCapacity(){ return (M % 2 == 0)?  keys.size() == M/2 - 1 : keys.size() == M / 2;}

		/**
		 * Return the middle key in the array of keys.
		 * @return The key at keys[numKeys / 2].
		 */
		T2 middleKey(){ return keys.get(keys.size() / 2); }

		/**
		 * Recursively searches for an element in the B-Tree.
		 * 
		 * @param element The element to search for.
		 * @return A reference to the element when found, or null if it is not found.
		 */
		T2 search(T2 element){
			int start = 0, end = keys.size() - 1;
			// This while loop will perform binary
			// search to find the key in the current node.
			// In the worst case scenario, after "numKey" - many 
			// iterations, it will converge to one key, which
			// may or may not be equal to our element. In the latter
			// case, a recursive call may be due.
			while(start < end){
				int mid = start + (end - start) / 2;
				int cmp = element.compareTo(keys.get(mid)); 
				if(cmp == 0)
					return keys.get(mid);
				else if(cmp < 0)
					end = mid - 1; // search left subarray
				else
					start = mid + 1; // search right subarray
			}
			// If we reach the end of the while loop, this means
			// That we have converged at a single key. If the key
			// is equal to the element, we simply return it.
			// If not, we need to check if the relevant child node is null,
			// in which case we return null. If it is not null, we apply
			// a recursive call to that child node.
			int cmp = element.compareTo(keys.get(start));
			if(cmp == 0)
				return keys.get(start);
			else if(cmp < 0)
				try {
					return children.get(start).search(element);
				}catch(IndexOutOfBoundsException exc){ // If the element at start is larger than or equal to the arraylist's size
					return null;
				}
			else
				try {
					return children.get(start + 1).search(element);
				} catch(IndexOutOfBoundsException exc){ // Same
					return null;
				}
		}

		/**
		 * Recursively adds the element in the B-tree. Rotates keys
		 * and splits nodes as necessary in order to maintain balance
		 * in the tree and prevent node overflowing.
		 * 
		 * @param element The element to add in the tree.
		 */
		void add(T2 element){
			// If the current node is a leaf, just insert the element
			// in the appropriate spot by shifting keys around as necessary.
			if(isLeaf()){
				int newElIndex = findKeyIndex(element);
				keys.add(newElIndex, element);
			} else {
				// In the subsequent context, findKeyIndex(T element)
				// will actually return an index which is suitable
				// for recursing into a child node.
				int childIndex = findKeyIndex(element);
				children.get(childIndex).add(element);
				// If the addition caused the child to overflow, we 
				// first check to see whether we can perform a left
				// or right rotation. If we can't do either, we need
				// to perform a node split.
				if(children.get(childIndex).overFlows())
					if(canRotateKeyLeft(childIndex))
						rotateKeyLeft(childIndex);
					else if(canRotateKeyRight(childIndex))
						rotateKeyRight(childIndex);
					else
						splitChild(childIndex);
			}
			keys.trimToSize();
			children.trimToSize();
		}

		/* The following helper method is used by add().
		 * It performs binary search to find the appropriate 
		 * index to add the element to. Whether the key was found
		 * or not in the array, we always find an appropriate index
		 * to insert they new key to.
		 */

		private int findKeyIndex(T2 element){
			int start = 0, end = keys.size() - 1;
			while(start < end){ 
				int mid = start + (end - start) / 2;
				int cmp = element.compareTo(keys.get(mid)); 
				if(cmp == 0)
					return mid + 1; // done			
				else if(cmp < 0)
					end = mid - 1; // search left subarray
				else
					start = mid + 1; // search right subarray
			}
			if(element.compareTo(keys.get(start)) < 0)
				return start;
			else
				return start + 1;
		}

		/* Split the child pointed to by "childIndex", making
		 * the child at "childIndex" point to its lower half and the index at "childIndex+1"
		 * point to its upper half. The two new nodes will be separated by the middle key
		 * of the old child node.
		 */
		private void splitChild(int childIndex){
			T2 midChildKey = children.get(childIndex).middleKey();
			//shiftRight(childIndex, numKeys, keys); // Make space for a new key and a new child in the current node.
			//shiftRight(childIndex + 1, numChildren, children);
			keys.add(childIndex, midChildKey);
			BTreeNode<T2> oldChildNode = children.get(childIndex);
			children.remove(childIndex); // Remove the previous node
			children.add(childIndex, oldChildNode.getSmallerHalf()); // And add the two new ones
			children.add(childIndex + 1, oldChildNode.getLargerHalf());
		}

		/*
		 * Ask the question of whether we can rotate a key to a left sibling.
		 */
		private boolean canRotateKeyLeft(int pivotChild){
			return (pivotChild > 0) && !children.get(pivotChild).minCapacity()  
					&& !children.get(pivotChild - 1).isFull();
		}

		/*
		 * Ask the question of whether we can rotate a key to a right sibling.
		 */
		private boolean canRotateKeyRight(int pivotChild){
			return (pivotChild < children.size() - 1) && !children.get(pivotChild).minCapacity() 
					&& !children.get(pivotChild + 1).isFull();
		}

		/* Actually rotate the key to the left. Key j - 1 splits children j - 1 and j.*/
		private void rotateKeyLeft(int sourceChild){
			ArrayList<T2> rightKeys = children.get(sourceChild).keys,
					leftKeys = children.get(sourceChild - 1).keys;
			ArrayList<BTreeNode<T2>> rightChildren = children.get(sourceChild).children,
					leftChildren = children.get(sourceChild - 1).children;
			T2 currKey = keys.remove(sourceChild - 1);
			T2 rightKey = rightKeys.remove(0);
			leftKeys.add(currKey);
			keys.add(sourceChild -1, rightKey);
			if(rightChildren.size() > 0)
				leftChildren.add(rightChildren.remove(0));
		}

		/* Actually rotate the key to the right. key j splits children j and j + 1 */
		private void rotateKeyRight(int sourceChild){
			ArrayList<T2> rightKeys = children.get(sourceChild + 1).keys,
					leftKeys = children.get(sourceChild).keys;
			ArrayList<BTreeNode<T2>> rightChildren = children.get(sourceChild + 1).children,
					leftChildren = children.get(sourceChild).children;
			T2 currKey = keys.remove(sourceChild);
			T2 leftKey = leftKeys.remove(leftKeys.size() - 1);
			rightKeys.add(0, currKey);
			keys.add(sourceChild, leftKey);
			if(leftChildren.size() > 0)
				rightChildren.add(0, leftChildren.remove(leftChildren.size() - 1));

		}
		/* Retrieve the bottom half portion of the current B-Tree node. 
		 * Neither the bottom nor the top half will contain the middle key 
		 * of the old node, since that has already been retrieved by the
		 * caller method, by calling middleKey().
		 */
		private BTreeNode<T2> getSmallerHalf(){
			BTreeNode<T2> retVal = new BTreeNode<T2>();
			List<T2> halfKeys = keys.subList(0, keys.size() / 2);
			retVal.keys.addAll(halfKeys);
			if(!children.isEmpty()){
				List<BTreeNode<T2>> halfChildren = children.subList(0, (children.size() %2 == 1) ? children.size() / 2 + 1 : children.size() / 2);
				retVal.children.addAll(halfChildren);
			}
			return retVal;
		}

		/* Same as above, but retrieving the top half portion of a node. */
		private BTreeNode<T2> getLargerHalf(){
			BTreeNode<T2> retVal = new BTreeNode<T2>();
			List<T2> halfKeys = keys.subList(keys.size() / 2 + 1, keys.size());
			retVal.keys.addAll(halfKeys);
			if(children.size() > 2){
				List<BTreeNode<T2>> halfChildren = children.subList((children.size() %2 == 1) ? children.size() / 2 + 1 : children.size() / 2, children.size());
				retVal.children.addAll(halfChildren);
			}
			return retVal;
		}

		/* Removes an element from the B-Tree. Performs rotations
		 * and node mergins as necessary to preserve the invariants
		 * of the data structure.
		 */
		T2 remove(T2 element){
			int matchIndex;
			// If the node is a leaf, perform binary search for the element.
			// At this point, you may or may not find it.
			if(isLeaf()){
				matchIndex = searchForElement(element);
				if(matchIndex == KEY_NOT_FOUND)
					return null; // Element not in tree.
				else{
					T2 retVal = keys.remove(matchIndex);
					keys.trimToSize();
					return retVal;
				}
			}
			/* If the key to be found is in the current non-leaf node, we need to
			 * find its inorder successor, replace the key with the inorder
			 * successor, and call remove(inorder_successor) for the appropriate subtree,
			 * taking care of splits and merges appropriately. 
			 */
			T2 retVal = null, elToRemove = element;
			matchIndex = searchForElement(element); // Searches for the element in the current node.
			if(matchIndex != KEY_NOT_FOUND){ // Found the element in the current node.
				T2 inSucc = children.get(matchIndex + 1).getInorderSuccessor(); // The inorder successor will be the smallest key in the right subtree of the key we found.
				keys.set(matchIndex, inSucc);
				elToRemove = inSucc; // We will now need to remove the inorder successor, so we change our removal target.
				retVal = element;
			}
			/* If, on the other hand, it is not in the current node, we simply find
			 * the appropriate subtree to recurse to, and we still take care of
			 * splits and merges as appropriate.
			 */
			int childIndex = findKeyIndex(elToRemove); // Finds the appropriate child index to recurse to for either the original element or the inorder successor.
			T2 result = children.get(childIndex).remove(elToRemove); 
			if(result == null) // Did not find the key. This is only possible when elToRemove == element (an inorder successor is guaranteed to exist)
				return null;
			else if(retVal == null) // Found the key down the tree, not on the current node
				retVal = result;
			// Did our removal make the child node underflow?
			if(children.get(childIndex).underFlows()){
				// See whether we can rotate a key from either
				// the left or the right sibling.
				boolean rotatedRight = false, rotatedLeft = false;
				if(childIndex > 0)
					if((rotatedRight = canRotateKeyRight(childIndex - 1)) == true)
						rotateKeyRight(childIndex -1);
				if(childIndex < children.size() - 1)
					if((rotatedLeft = canRotateKeyLeft(childIndex + 1)) == true)
						rotateKeyLeft(childIndex + 1);
				if(!rotatedRight && !rotatedLeft) // If no rotations occurred, only option is to merge the nodes.
					if(childIndex > 0)
						mergeNodes(childIndex - 1, childIndex);
					else
						mergeNodes(childIndex, childIndex + 1);
			}
			keys.trimToSize();
			children.trimToSize();
			return retVal;
		}

		// Search for a key in a leaf node through binary search.
		private int searchForElement(T2 element){
			int start = 0, end = keys.size() - 1;
			int matchIndex = KEY_NOT_FOUND;
			while(start <= end && matchIndex == KEY_NOT_FOUND){
				int mid = start + (end - start) / 2;
				int cmp = element.compareTo(keys.get(mid)); 
				if(cmp == 0)
					matchIndex = mid;
				else if(cmp < 0)
					end = mid - 1; // search left subarray
				else
					start = mid + 1; // search right subarray
			} 
			return matchIndex;
		}

		// Return the left-most key in the subtree rooted at the parameter index.
		private T2 getInorderSuccessor(){
			BTreeNode<T2> current = this;
			while(!current.isLeaf())
				current = current.children.get(0);
			return current.keys.get(0);			
		}

		/* Merge the nodes at indices "childIndex" and "childIndex -1",
		 * creating a single node which contains the keys of the node at
		 * childIndex -1, followed by the key at keys[childIndex -1], and
		 * then followed by the keys of the child at "childIndex". Children
		 * are copied as necessary. The new node  will be pointed to by the 
		 * reference "children[firstChild]".
		 */
		private void mergeNodes(int firstChild, int secondChild){
			BTreeNode<T2> mergedNode = new BTreeNode<T2>();
			mergedNode.keys.addAll(children.get(firstChild).keys);
			mergedNode.keys.add(keys.remove(firstChild));
			mergedNode.keys.addAll(children.get(secondChild).keys);
			mergedNode.children.addAll(children.get(firstChild).children);
			mergedNode.children.addAll(children.get(secondChild).children);
			children.set(firstChild, mergedNode);
			children.remove(secondChild); // remove the old, useless child.
		}

		/* Methods for retrieving minimum and maximum keys. They are pretty straightforward:
		 * To get the minimum (maximum) key, we need to traverse the BTree until the leftmost (rightmost)
		 * key. We implement both methods iteratively.
		 */
		T2 getMin(){
			BTreeNode<T2> current = this;
			while(!current.isLeaf())
				current = current.children.get(0);
			return current.keys.get(0); 
		}

		T2 getMax(){
			BTreeNode<T2> current = this;
			while(!current.isLeaf())
				current = current.children.get(current.children.size() - 1);
			return current.keys.get(current.keys.size() - 1); 
		}

		/* The last methods that we need to implement for the BTreeNode class
		 * are the ones taking care of the various tree traversals. Those are 
		 * trivial to implement. We do not implement inorder traversals,
		 * because these have been defined to be exactly the same as pre-order
		 * traversals.
		 */
		void preOrder(ArrayList<T2> list){
			int i;
			for(i = 0; i < keys.size(); i++){
				list.add(keys.get(i));
				children.get(i).preOrder(list);
			}
			children.get(i).preOrder(list);
		}


		void postOrder(ArrayList<T2> list){
			int i;
			for(i = 0; i < keys.size(); i++){
				children.get(i).postOrder(list);
				list.add(keys.get(i));
			}
			children.get(i).postOrder(list);
		}

		void levelOrder(ArrayList<T2> list){
			Queue<BTreeNode<T2>> queue = new LinkedQueue<BTreeNode<T2>>();
			queue.enqueue(this);
			while(!queue.isEmpty()){
				BTreeNode<T2> currNode = null;
				try {
					currNode = queue.dequeue();
				}catch(EmptyQueueException exc){
					// dummy
				}
				for(T2 key : currNode.keys) // linear order
					list.add(key);
				for(BTreeNode<T2> child: currNode.children)
					queue.enqueue(child);
			}
		}

	} // class BTreeNode closing brace


	/* M = node width = number of children per node. 
	 * The root has [2, M] children, inner nodes have 
	 * [ceil(M/2), M] (non-null) children, leaf children 
	 * have 0 children and [ceil(M/2) - 1, M - 1] keys.
	 */
	private int M;
	private BTreeNode<T> root;
	private int count;
	private int treeHeight; 

	/**
	 * Default constructor creates a 5-tree
	 */
	public BTree(){
		this(5);
	} 

	/**
	 * Constructor which allows the user to specify the node width.
	 * @param M The node width to build the tree with.
	 */
	public BTree(int M) { 
		this.M = M;
		root = null;
		treeHeight = count = 0;
	}

	/**
	 * Copy constructor. The current B-Tree will be created based on the
	 * contents of <tt>other</tt>. Note that this does not mean that the
	 * current B-Tree will be a carbon copy of <tt>other</tt> because the structure
	 * of B-Trees is dependent on the sequence of insertions. The size and height of the
	 * tree will of course end up being the same as <tt>other</tt>'s. 
	 * @param other
	 */
	public BTree(BTree<T> other){
		M = other.M;
		treeHeight = other.treeHeight;
		for(T el : other)
			add(el);
	}

	/**
	 * Standard equals() method. Returns true if the two objects are instance-equal
	 * @param other The {@link: Object} to compare the current object to. 
	 * @return true if the <tt>Object</tt> provided as instance-equal to the current object.
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object other){
		if(other == null || other.getClass() != this.getClass())
			return false;
		BTree<T> ocasted = null;
		try {
			ocasted = (BTree<T>)other;
		} catch(ClassCastException exc){
			return false;
		}
		if(size() != ocasted.size() || this.treeHeight != ocasted.treeHeight || M != ocasted.M)
			return false;
		Iterator<T> thisLOrder, otherLOrder;
		try {
			thisLOrder = levelOrder();
			otherLOrder = ocasted.levelOrder();
		} catch (EmptyTreeException e) {
			return false;
		}
		while(thisLOrder.hasNext()){
			if(!otherLOrder.hasNext())
				return false;
			if(!thisLOrder.next().equals(otherLOrder.next()))
				return false;
		}
		return true;
	}

	public boolean contains(T element){
		try {
			return find(element) != null;
		} catch (EmptyTreeException e) {
			return false;
		}
	}

	@Override
	public Iterator<T> iterator() {
		try {
			return levelOrder();
		} catch (EmptyTreeException e) {
			return null;
		}
	}

	@Override
	public T getRoot() throws EmptyTreeException {
		if(root == null)
			throw new EmptyTreeException("getRoot(): tree is empty.");
		// In a B-tree, the root might have as many as M-1 
		// and as few as 1 key.We will follow the convention 
		// that getRoot() will always return the middle key.
		return root.middleKey();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return count;
	}

	/**
	 * Queries the B-Tree for its height.
	 * @return The height of the B-Tree.
	 */
	public int height(){
		return treeHeight;
	}

	@Override
	public T find(T element) throws EmptyTreeException {
		if(root == null)
			throw new EmptyTreeException("find(): tree is empty!");
		return root.search(element);
	}

	/**
	 * Add an element to a <tt>BTree</tt>.
	 * 
	 * @param element The element to add.
	 */
	public void add(T element){
		if(root == null){
			root = new BTreeNode<T>(element);
			treeHeight = 1;
		}
		else{
			root.add(element);
			if(root.overFlows()){
				root = splitRoot(root);
				treeHeight++;
			}
		}
		count++;
	}

	private BTreeNode<T> splitRoot(BTreeNode<T> oldRoot){
		T midRootKey = oldRoot.middleKey();
		BTreeNode<T> newRoot = new BTreeNode<T>(midRootKey);
		newRoot.children.add(oldRoot.getSmallerHalf());
		newRoot.children.add(oldRoot.getLargerHalf());
		return newRoot;
	}

	/**
	 * Remove an element from a <tt>BTree</tt>.
	 * 
	 * @param element the element to remove.
	 * @return The removed element, or null if we couldn't find it.
	 */
	public T remove(T element){
		if(element == null || root == null)
			return null;
		T retVal = root.remove(element);
		if(root.keys.isEmpty() && !root.children.isEmpty()){ // root underflow
			root = root.children.get(0); // just change the root
			treeHeight--;
		}
		count--;
		return retVal;
	}

	/**
	 * For B-trees, we define pre-order traversal  as follows:
	 * Pre-order traversal consists of visiting the j-th key
	 * in a node first, and then traversing the j'th child. After visiting
	 * all keys in a node, the final subtree is also traversed.
	 * 
	 * @return An Iterator over the keys of the B-tree, in the order specified above.
	 */
	@Override
	public Iterator<T> preorder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("preorder(): tree is empty!");
		ArrayList<T> list = new ArrayList<T>();
		root.preOrder(list);
		return list.iterator();
	}

	/**
	 * For B-trees, we equate inorder and pre-order traversals. 
	 * 
	 * @return An Iterator over the keys of the B-tree, in the order specified above.
	 */
	@Override
	public Iterator<T> inOrder() throws EmptyTreeException {
		return preorder();
	}

	/**
	 * In a B-Tree, post-order traversal consists of visiting the j'th child
	 * first, and the j'th key after that. 
	 * 
	 * @return An Iterator over the keys of the B-tree, in the order specified above.
	 */
	@Override
	public Iterator<T> postOrder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("postOrder(): tree is empty!");
		ArrayList<T> list = new ArrayList<T>();
		root.postOrder(list);
		return list.iterator();
	}

	/**
	 * In a B-Tree, pre-order traversal consists of visiting the j-th key
	 * in a node first, and then traversing the j'th child. After visiting
	 * all keys in a node, the final subtree is also traversed.
	 * 
	 * @return An Iterator over the keys of the B-tree, in the order specified above.
	 */
	@Override
	public Iterator<T> levelOrder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("levelOrder(): tree is empty!");
		ArrayList<T> list = new ArrayList<T>();
		root.levelOrder(list);
		return list.iterator();
	}

	@Override
	public T getMin() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("getMin(): tree is empty!");
		return root.getMin();
	}

	@Override
	public T getMax() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("getMax(): tree is empty!");
		return root.getMax();
	}

	@Override
	public void clear() {
		root = null;
		count = 0;
		treeHeight = 0;
	}

}
