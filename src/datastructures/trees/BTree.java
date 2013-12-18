package datastructures.trees;

import java.util.Iterator;

import datastructures.lists.*;
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
	 * in the BTree.
	 * @author Jason Filippou (jasonfil@cs.umd.edu)
	 *
	 * @param <T2> The Comparable type held by the object.
	 */
	@SuppressWarnings("unchecked")
	private class BTreeNode<T2 extends Comparable<T2>>{

		int numChildren;
		int numKeys;
		T2[] keys;
		BTreeNode<T2>[] children;
		private final static int KEY_NOT_FOUND = -1;

		/**
		 * A constructor that initializes a new BTreeNode
		 * with a starting key.
		 * 
		 * @param startingKey The first key in the node.
		 */
		BTreeNode(T2 startingKey) {

			keys = (T2[]) new Object[M]; // One too many
			children = (BTreeNode<T2>[]) new Object[M + 1]; // One too many
			numChildren = 0;
			numKeys = 1;
			keys[0] = startingKey;
		}

		/**
		 * A default constructor that simply allocates
		 * space for a new node.
		 */
		BTreeNode(){
			keys = (T2[]) new Object[M];
			children = (BTreeNode<T2>[]) new Object[M + 1];
			numChildren = 0;
			numKeys = 0;
		}

		/**
		 * Returns true if the current node is a leaf of the tree.
		 * 
		 * @return true if the node is a leaf of the tree, i.e it does
		 * 	not have any children.
		 */
		boolean isLeaf(){ return numChildren == 0;}

		/**
		 * Returns true if the current node is full, i.e it cannot take
		 * any more keys.
		 * 
		 * @return true if the number of children in the node is equal
		 * to the node width M.
		 */
		boolean overFlows(){ return numKeys == M - 1; } // M - 1 keys max per node

		/**
		 * Returns true if the current node underflows, i.e it contains less than
		 * floor(M/2) keys.
		 * @return true if the node has less than floor(M/2) keys.
		 */
		boolean underFlows() { return numKeys < M/2; }

		/**
		 * Return the middle key in the array of keys.
		 * @return The key at keys[numKeys / 2].
		 */
		T2 middleKey(){ return keys[numKeys / 2]; }

		/**
		 * Recursively searches for an element in the B-Tree.
		 * 
		 * @param element The element to search for.
		 * @return A reference to the element when found, or null if it is not found.
		 */
		T2 search(T2 element){
			int start = 0, end = numKeys - 1;
			// This while loop will perform binary
			// search to find the key in the current node.
			// In the worst case scenario, after "numKey" - many 
			// iterations, it will converge to one key, which
			// may or may not be equal to our element. In the latter
			// case, a recursive call may be due.
			while(start < end){
				int mid = start + (end - start) / 2;
				int cmp = element.compareTo(keys[mid]); 
				if(cmp == 0)
					return keys[mid];
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
			int cmp = element.compareTo(keys[start]);
			if(cmp == 0)
				return keys[start];
			else if(cmp < 0)
				if(children[start] == null) // j'th child contains values less than j'th key
					return null;
				else
					return children[start].search(element);
			else
				if(children[start + 1] == null) // (j + 1)'th child contains values larger than j'th key
					return null;
				else
					return children[start + 1].search(element);
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
				shiftRight(newElIndex, numKeys, keys);
				keys[newElIndex] = element;
				numKeys++;

			} else {
				// In the subsequent context, findKeyIndex(T element)
				// will actually return an index which is suitable
				// for recursing into a child node.
				int childIndex = findKeyIndex(element);
				children[childIndex].add(element);
				// If the addition caused the child to overflow, we 
				// first check to see whether we can perform a left
				// or right rotation. If we can't do either, we need
				// to perform a node split.
				if(children[childIndex].overFlows())
					if(canRotateKeyLeft(childIndex))
						rotateKeyLeft(childIndex);
					else if(canRotateKeyRight(childIndex))
						rotateKeyRight(childIndex);
					else
						splitChild(childIndex);
			}
		}

		// The following helper method is used by add().
		// It performs binary search to find the appropriate 
		// index to add the element to.
		private int findKeyIndex(T2 element){
			int start = 0, end = numKeys - 1;
			while(start < end){ 
				int mid = start + (end - start) / 2;
				int cmp = element.compareTo(keys[mid]); 
				if(cmp == 0)
					return mid + 1; // done			
				else if(cmp < 0)
					end = mid - 1; // search left subarray
				else
					start = mid + 1; // search right subarray
			}
			if(element.compareTo(keys[start]) < 0)
				return start;
			else
				return start + 1;
		}

		private void splitChild(int childIndex){
			T2 midChildKey = children[childIndex].middleKey();
			shiftRight(childIndex, numKeys, keys);
			shiftRight(childIndex + 1, numChildren, children);
			keys[childIndex] = midChildKey;
			children[childIndex] = getSmallerHalf(children[childIndex]);
			children[childIndex + 1] = getLargerHalf(children[childIndex]);
			numKeys++;
			numChildren++;
		}

		private void shiftRight(int start, int last, Object[] array){
			for(int i = last + 1; i > start; i--)
				array[i] = array[i - 1];
			array[start] = null;
		}

		private void shiftLeft(int start, int last, Object[] array){
			for(int i = start; i < last; i++)
				array[i] = array[i + 1];
			array[last] = null;
		}

		private void shiftAllLeft(){
			shiftLeft(0, numChildren, children);
			shiftLeft(0, numKeys, keys);
		}

		private void shiftAllRight(){
			shiftRight(0, numChildren, children);
			shiftRight(0, numKeys, keys);
		}

		private boolean canRotateKeyLeft(int pivotChild){
			return (pivotChild > 0) && !children[pivotChild - 1].overFlows();
		}

		private boolean canRotateKeyRight(int pivotChild){
			return (pivotChild < numChildren) && !children[pivotChild + 1].overFlows();
		}

		private void rotateKeyRight(int sourceChild){
			// key j splits children j and j + 1
			int numChildrenInSource = children[sourceChild].numChildren;
			int numKeysInSource = children[sourceChild].numKeys;
			children[sourceChild + 1].shiftAllRight();
			children[sourceChild + 1].keys[0] = keys[sourceChild];
			keys[sourceChild] = children[sourceChild].keys[numKeysInSource - 1];
			children[sourceChild + 1].children[0] = children[sourceChild].children[numChildrenInSource - 1];
			children[sourceChild + 1].numChildren++;
			children[sourceChild + 1].numKeys++;
			children[sourceChild].keys[numKeysInSource - 1] = null;
			children[sourceChild].children[numChildrenInSource - 1] = null;
			children[sourceChild].numChildren--;
			children[sourceChild].numKeys--;			
		}

		private void rotateKeyLeft(int sourceChild){
			// key j-1 splits children j-1 and j
			int numChildrenInTarget = children[sourceChild - 1].numChildren;
			int numKeysInTarget = children[sourceChild - 1].numKeys;
			children[sourceChild - 1].keys[numKeysInTarget] = keys[sourceChild - 1]; // add one key
			keys[sourceChild - 1] = children[sourceChild].keys[0];
			children[sourceChild - 1].children[numChildrenInTarget] = children[sourceChild].children[0];
			children[sourceChild].shiftAllLeft();
			children[sourceChild].numChildren--;
			children[sourceChild].numKeys--;
			children[sourceChild - 1].numChildren++;
			children[sourceChild - 1].numKeys++;
		}

		private BTreeNode<T2> getSmallerHalf(BTreeNode<T2> source){
			BTreeNode<T2> retVal = new BTreeNode<T2>();
			int i;
			for(i = 0; i < source.keys.length / 2; i++){
				retVal.keys[i] = source.keys[i];
				retVal.children[i] = source.children[i];
			}
			retVal.children[i] = source.children[i]; // copy that extra child
			retVal.numKeys = i;
			retVal.numChildren = i + 1;
			return retVal;
		}

		private BTreeNode<T2> getLargerHalf(BTreeNode<T2> source){
			BTreeNode<T2> retVal = new BTreeNode<T2>();
			int keyIndex = 0, childIndex = 0; // Just because I'm lazy
			int i;
			for(i = source.keys.length / 2 + 1; i < source.numKeys; i++){
				retVal.keys[keyIndex++] = source.keys[i];
				retVal.children[childIndex++] = source.children[i];
			}
			retVal.children[childIndex++] = source.children[i]; // copy that extra child
			retVal.numKeys = keyIndex;
			retVal.numChildren = childIndex;
			return retVal;
		}

		T2 remove(T2 element){
			int matchIndex;
			// If the node is a leaf, perform binary search for the element.
			// At this point, you may or may not find it.
			if(isLeaf()){
				matchIndex = searchForElement(element);
				if(matchIndex == KEY_NOT_FOUND)
					return null; // Element not in tree.
				else{
					shiftLeft(matchIndex, numKeys, keys);
					numKeys--;
					return element;
				}
			}
			// If the key to be found is in the current node, we need to
			// find its inorder successor, replace the key with the inorder
			// successor, and call remove(inorder_successor) for the appropriate subtree,
			// taking care of splits and merges appropriately.
			T2 retVal, elToRemove = element;
			matchIndex = searchForElement(element); // Searches for the element in the current node.
			if(matchIndex != KEY_NOT_FOUND){
				T2 inSucc = children[matchIndex + 1].getInorderSuccessor();
				keys[matchIndex] = inSucc;
				elToRemove = inSucc;
			}
			// If, on the other hand, it is not in the current node, we simply find
			// the appropriate subtree to recurse to, and we still take care of
			// splits and merges as appropriate.
			int childIndex = findKeyIndex(element); // Finds the appropriate child index to recurse to.
			retVal = children[childIndex].remove(elToRemove); // Either the original element or the inorder successor
			if(retVal == null) // Did not find the key. This is only possible when elToRemove == element (an inorder successor is guaranteed to exist)
				return null;
			// Did our removal make some node underflow?
			if(children[childIndex].underFlows()){
				// See whether we can rotate a key from either
				// the left or the right sibling.
				if(childIndex > 0){
					if(canRotateKeyRight(childIndex - 1))
						rotateKeyRight(childIndex -1);
				}
				else if(childIndex < numChildren - 1){
					if(canRotateKeyLeft(childIndex + 1))
						rotateKeyLeft(childIndex + 1);
				} else  // Node merging required
					mergeNodes(childIndex);
			}
			return retVal;
		}

		// Search for a key in a leaf node through binary search.
		private int searchForElement(T2 element){
			int start = 0, end = numKeys;
			int matchIndex = KEY_NOT_FOUND;
			while(start <= end && matchIndex == KEY_NOT_FOUND){
				int mid = start + (end - start) / 2;
				int cmp = element.compareTo(keys[mid]); 
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
				current = current.children[0];
			return current.keys[0];			
		}

		// Merge the nodes at indices "childIndex" and "childIndex -1",
		// creating a single node which contains the keys of the node at
		// childIndex -1, followed by the key at keys[childIndex -1], and
		// then followed by the keys of the child at "childIndex".
		// The new node will have a total number of keys equal to the sum of
		// the two previous' nodes numbers of keys plus 1, and a number of children
		// equal to the sum of the two previous nodes' numbers of children. It will
		// be pointed to by the reference "children[childIndex - 1]", and the reference
		// children[childIndex] will be nullified, triggering garbage collection.
		private void mergeNodes(int childIndex){
			BTreeNode<T2> mergedNode = new BTreeNode<T2>();
			int i;
			for(i = 0; i < children[childIndex - 1].numKeys; i++){
				mergedNode.keys[i] = children[childIndex - 1].keys[i];
				mergedNode.children[i] = children[childIndex - 1].children[i];
			}
			mergedNode.keys[i] = keys[childIndex - 1];
			mergedNode.children[i] = children[childIndex - 1].children[i]; // last child from previous node
			i++;
			for(int j = 0; j < children[childIndex].numKeys; j++){
				mergedNode.keys[i] = children[childIndex].keys[j];
				mergedNode.children[i++] = children[childIndex].children[j];
			}
			mergedNode.numChildren = children[childIndex - 1].numChildren + children[childIndex].numChildren;
			mergedNode.numKeys = children[childIndex - 1].numKeys + 1 + children[childIndex].numKeys;
			children[childIndex] = null;
			children[childIndex - 1] = mergedNode;
			this.numKeys--;
			this.numChildren--;
		}

		/* Methods for retrieving minimum and maximum keys. They are pretty straightforward:
		 * To get the minimum (maximum) key, we need to traverse the BTree until the leftmost (rightmost)
		 * key. We implement both methods iteratively.
		 */
		
		T2 getMin(){
			BTreeNode<T2> current = this;
			while(!current.isLeaf())
				current = current.children[0];
			return current.keys[0]; 
		}
		
		T2 getMax(){
			BTreeNode<T2> current = this;
			while(!current.isLeaf())
				current = current.children[numChildren - 1];
			return current.keys[numKeys - 1]; 
		}
		
		/* The last methods that we need to take care of for the BTreeNode class
		 * are the ones taking care of the various tree traversals. Those are 
		 * trivial to implement. We do not implement inorder traversals,
		 * because these have been defined to be exactly the same as pre-order
		 * traversals.
		 */

		void preOrder(List<T2> list){
			int i;
			for(i = 0; i < numKeys; i++){
				list.pushBack(keys[i]);
				children[i].preOrder(list);
			}
			children[i].preOrder(list);
		}


		void postOrder(List<T2> list){
			int i;
			for(i = 0; i < numKeys; i++){
				children[i].postOrder(list);
				list.pushBack(keys[i]);
			}
			children[i].postOrder(list);
		}

		void levelOrder(List<T2> list){
			Queue<BTreeNode<T2>> queue = new LinkedQueue<BTreeNode<T2>>();
			queue.enqueue(this);
			while(!queue.isEmpty()){
				BTreeNode<T2> currNode = null;
				try {
					currNode = queue.dequeue();
				}catch(EmptyQueueException exc){
					// dummy
				}
				for(T2 key : currNode.keys)
					list.pushBack(key);
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

	/**
	 * Default constructor creates a 5-tree
	 */
	public BTree(){
		M = 5; 
		root = null;
		count = 0;
	} 

	/**
	 * Constructor which allows the user to specify the node width.
	 * @param M The node width to build the tree with.
	 */
	public BTree(int M) { 
		this.M = M;
		root = null;
		count = 0;
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
		if(root == null)
			root = new BTreeNode<T>(element);
		else{
			root.add(element);
			if(root.overFlows())
				root = splitRoot(root);
		}
		count++;
	}

	private BTreeNode<T> splitRoot(BTreeNode<T> oldRoot){
		T midRootKey = oldRoot.middleKey();
		BTreeNode<T> newRoot = new BTreeNode<T>(midRootKey);
		newRoot.children[0]= oldRoot.getSmallerHalf(oldRoot);
		newRoot.children[1] = oldRoot.getLargerHalf(oldRoot);
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
		return root.remove(element);
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
		ArrayListBasedList<T> list = new ArrayListBasedList<T>();
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
		ArrayListBasedList<T> list = new ArrayListBasedList<T>();
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
		ArrayListBasedList<T> list = new ArrayListBasedList<T>();
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
	}

}
