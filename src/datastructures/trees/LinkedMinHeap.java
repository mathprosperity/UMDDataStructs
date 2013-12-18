package datastructures.trees;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;
/**
 * A MinHeap is a tree (specifically, a complete binary tree) where every node is
 * smaller than or equal to its descendants (as defined by the compareTo()
 * overridings of the type T). 
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * @param T the type of Comparable object held by the Heap.
 */
public class LinkedMinHeap<T extends Comparable<T>> implements MinHeap<T> {

	private MinHeapNode<T> root, last;
	private int count;
	protected boolean modificationFlag;
	
	/**
	 *  Default constructor sets pointers to null and count to 0.
	 */
	public LinkedMinHeap(){
		root = last = null;
		count = 0;
		modificationFlag = false;
	}

	/**
	 *  Second constructor creates a root node with the element provided 
	 *  as the content.
	 *  @param rootElement the element to create teh root with.
	 */
	public LinkedMinHeap(T rootElement){
		root = last = new MinHeapNode<T>(rootElement);
		count++;
		modificationFlag = false;
	}
	
	/** 
	 * Copy constructor initializes the current MinHeap as a carbon
	 * copy of the parameter.
	 * 
	 * @param other The MinHeap to copy the elements from.
	 */
	public LinkedMinHeap(MinHeap<T> other){
		if(other == null)
			return;
		for(T el: other) // MaxHeaps have been made Iterable.
			add(el);
		modificationFlag = false;
	}
	
	/**
	 * Standard equals() method.
	 * 
	 * @return true If the parameter Object and the current MinHeap
	 * are identical Objects.
	 */
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof MinHeap<?>))
			return false;
		MinHeap<?> oheap = null;
		try {
			oheap = (MinHeap<?>)other;
		} catch(ClassCastException cce){
			return false;
		}
		Iterator<?> itthis = iterator(), ito = oheap.iterator();
		while(itthis.hasNext())
			if(!itthis.next().equals(ito.next()))
				return false;
		return true;
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
	public void clear() {
		root = last = null;
		count = 0;
		System.gc();
		modificationFlag = false;
	}


	/* To add an element in the heap, we add it as the last leaf, and then we move the element upward until
	 * the heap identity is maintained.  
	 */
	@Override
	public void add(T element) {
		MinHeapNode<T> newNode = new MinHeapNode<T>(element); 
		MinHeapNode<T> newNodeParent = null;
		if(root == null)
			root = newNode;
		else{
			newNodeParent = root.getParentForAdd(last);
			if(newNodeParent.left == null)
				newNodeParent.left = newNode;
			else
				newNodeParent.right = newNode;
		}
		newNode.setParent(newNodeParent);
		last = newNode; 
		root.moveUpNode(last);
		modificationFlag = true;
		count++;
	} 

	@Override
	public T getMin() throws EmptyHeapException {
		if(root == null)
			throw new EmptyHeapException("getMin(): tree is empty.");
		return root.getElement();
	}

	@Override
	public T removeMin() throws EmptyHeapException {
		if(root == null)
			throw new EmptyHeapException("removeMax(): Tree is empty.");
		T maxElement = root.getElement();

		// In a maxheap, the root contains the maximum element.To remove it, 
		// we make the last leaf node the new root, and then "push" this 
		// node downward until the maxheap identity is maintained;

		if(root.left == null) // single element in the heap, the root element
			root = last = null; // just remove it, and we're done.
		else{ // at least one more element in the heap.
			MinHeapNode<T> newLast = root.getNewLast(last);
			if(last.parent.left == last)// Was the last node a left child?
				last.parent.left = null;
			else // Was the last node a right child?
				last.parent.right = null;
			root.setElement(last.getElement()); // Exchange elements between root and last nodes.
			last = newLast;
			root.reorderHeap(root); // re-order the heap by "pushing down" (element exchange) the new root as appropriate.
		}
		count--;
		modificationFlag = true;
		return maxElement;
	}	
	
	/**
	 * MinHeapNode is a class representing a minheap's node. It contains recursive methods
	 * typical of heap operations.
	 * 
	 * @author Jason Filippou (jasonfil@cs.umd.edu)
	 * 
	 * @param <T2> The type of Comparable element that will be contained in the node. Using T2 instead
	 * of T aids with unwanted compile-time warnings (type shadowing).
	 */
	private class MinHeapNode<T2 extends Comparable<T2>>{

		private T2 data;
		// HeapNodes contain references to ancestral and "offspring" nodes.
		private MinHeapNode<T2> parent, left, right;

		public MinHeapNode(T2 element){
			parent = left = right = null;
			data = element;
		}

		public MinHeapNode(T2 element, MinHeapNode<T2> parent){
			data = element;
			this.parent = parent;
			left = right = null;
		}

		public T2 getElement(){
			return data;
		}

		public void setElement(T2 element){
			data = element;
		}

		public void setParent(MinHeapNode<T2> parent){
			this.parent = parent;
		}

		private MinHeapNode<T2> findSmallerChild(){
			MinHeapNode<T2> retVal;
			if(left == null) // Because the heap is a complete tree, this subsumes right == null.
				retVal = null;
			else if(right == null) // Only right is null
				retVal = left;
			else { // Need to check both children
				if(left.getElement().compareTo(right.getElement()) < 0)
					retVal = left;
				else
					retVal = right;
			}
			return retVal;
		}

		public MinHeapNode<T2> getParentForAdd(MinHeapNode<T2> lastNode){
			MinHeapNode<T2> result = lastNode;
			// We need to move on a left-to-right direction until we hit the root
			// or a parent of which we are the left child!
			while(result.parent != null && result.parent.left != result) 
				result = result.parent;
			if(result.parent != null) // We did not encounter the root node 
				if(result.parent.right == null) // New node is going to be a right leaf of its parent
					result = result.parent;
				else { // We need to go up, then down and right, and continue scanning all the way down to the left.
					result = result.parent.right;
					while(result.left != null)
						result = result.left;
				}
			else // Encountered the root, which means that the next leaf will begin a new level. Equivalently, the parent will be
				// the left-most current leaf of the tree.
				while(result.left != null)
					result = result.left;
			return result;
		}

		public void moveUpNode(MinHeapNode<T2> last){
			MinHeapNode<T2> current = last;
			// Move up as long as you can, swapping elements in the process.
			while(current.parent != null && current.data.compareTo(current.parent.data) < 0){
				//swap elements
				T2 temp = current.data;
				current.data = current.parent.data;
				current.parent.data = temp;
				current = current.parent; // move up in the heap
			}
		}

		public MinHeapNode<T2> getNewLast(MinHeapNode<T2> last){
			MinHeapNode<T2> result = last;
			// Move upwards in the heap from a left-to-right direction until
			// you either encounter the root (in which case the new last node will be the
			// right-most current leaf in the tree) or you encounter a parent who had you as the
			// right child, which then means that the new last node will be found after taking a step
			// upwards and left, and then going right as far as possible!
			while(result.parent != null && result.parent.left == result)
				result = result.parent;
			if(result.parent != null)// Did not encounter the root, so we need to take that extra step!
				result = result.parent.left;
			while(result.right != null)
				result = result.right;
			return result;
		}

		// The following method is called typically after root removal.
		// To re-order the heap, we need to keep pushing the new root down 
		// in the heap until the heap identity is maintained.
		public void reorderHeap(MinHeapNode<T2> root){
			MinHeapNode<T2> currNode = root, largerChild = currNode.findSmallerChild();
			while(largerChild != null && largerChild.data.compareTo(currNode.data) < 0) {
				// swap elements
				T2 temp = currNode.data;
				currNode.data = largerChild.data;
				largerChild.data = temp;

				// Update references
				currNode = largerChild;
				largerChild = currNode.findSmallerChild();
			}
		}
		
		public void gatherAllElements(MinHeap<T2> oheap){
			oheap.add(data); // pre-order traversal
			if(left != null)
				left.gatherAllElements(oheap);
			if(right!= null)
				right.gatherAllElements(oheap);
		}


	}// inner class HeapNode

	@Override
	public Iterator<T> iterator() {
		return new LinkedMinHeapIterator<T>();
	}
	
	class LinkedMinHeapIterator<T2 extends Comparable<T2>> implements Iterator<T2>{
	
		private MinHeap<T2> tempHeap;
		
		@SuppressWarnings("unchecked")
		
		public LinkedMinHeapIterator(){ // The meat
			tempHeap = new LinkedMinHeap<T2>();
			if(root != null)
				root.gatherAllElements((MinHeap<T>) tempHeap);
			modificationFlag = false;
		}
		
		@Override
		public boolean hasNext() {
			return !tempHeap.isEmpty(); // also covers the case of the root being null.
		}

		@Override
		public T2 next() throws ConcurrentModificationException, NoSuchElementException {
			if(modificationFlag)
				throw new ConcurrentModificationException("next():"
						+ " cannot traverse the LinkedMaxHeap through an iterator after externally modifying it.");
			T2 retVal = null;
			try {
				retVal = tempHeap.removeMin();
			} catch (EmptyHeapException e) {
				throw new NoSuchElementException("Heap is empty!");
			}
			return retVal;
		}

		/**
		 * Removing an arbitrary element from the maxheap is an unsupported
		 * operation. We only allow the user to explicitly call removeMax()
		 * in order to remove the maximum element (the root).
		 * @throws UnsupportedOperationException always
		 */
		@Override
		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Arbitrary element removal is not allowed for MaxHeaps.");
		}		
	}


} // outer class LinkedMaxHeap



