package datastructures.trees;

import java.util.ArrayList;
import java.util.Iterator;

import datastructures.UnsupportedOperationException;
import datastructures.queues.*;
/**
 * <p>A <tt>Trie</tt> is a {@link Tree}-based data structure which uses character strings to index into its nodes.
 * The large degree of prefix sharing exhibited in typical string-based applications (insertion, lookup) 
 * means that we save a lot of time when searching for a particular character string. The values inherited
 * by trees play the role of the value which any particular string indexes into; a null value in a node means
 * that the substring consisting of all the characters up to that node does not represent a string that was
 * inserted into a trie. </p>
 * 
 * <p> The insertion, deletion and accessing elements require a {@link CharSequence} to be provided. This is
 * because we only require the character string to expose a charAt(int index) method that will return the char at
 * position "index" of the <tt>CharSequence</tt> provided. </p>
 *  
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @param T the type for the values held by the <tt>Trie</tt>.
 * 
 * @since December 2013
 *
 */
@SuppressWarnings("unchecked")
public class Trie<T> implements Tree<T>{

	/**
	 * Node type for a Trie. Maintains an <tt>alphabetSize</tt>-large array  of references
	 * to possible children. We choose an array over a list, sacrificing memory footprint over
	 * amortized operation complexity.
	 * 
	 * @author Jason Filippou (jasonfil@cs.umd.edu)
	 */
	protected class TrieNode{
		T data;
		Object[] children;

		/**
		 * Constructor that provides the data element.
		 * @param data The data element to create the TrieNode around.
		 */
		TrieNode(T data){
			this.data = data;
			children = new Object[alphabetSize]; 
		}

		/**
		 * Default constructor does not provide a data element.
		 */
		TrieNode(){
			this(null);
		}

		/**
		 * Find the element specified somewhere in the trie and return a reference to
		 * the node that contains it.
		 * @param element The element to search for.
		 * @return A reference to the node that contains the element, or null if the element is not in the tree.
		 */
		TrieNode find(T element){
			if(data == element) // Makes sense to do a pre-order traversal in exhaustive search scenarios.
				return this;
			for(int i = 0; i < children.length; i++)
				if(children[i] != null)
					return ((TrieNode)children[i]).find(element);
			return null;
		}

		/**
		 * Find the node indexed by index and return a reference to it.
		 * @param index A {@link:CharSequence} which indexes into the node to be retrieved.
		 * @param currCharIndex An int that points to the character of <tt>index</tt> to be read next.
		 * @return A reference to the node indexed by index, or null if the index was invalid.
		 */
		TrieNode find(CharSequence index, int currCharIndex){
			if(currCharIndex == index.length())
				return this;
			char childIndex = index.charAt(currCharIndex);
			if(childIndex < 0 || childIndex >= children.length)
				throw new CharacterNotInAlphabetException("TrieNode.find(CharSequence data, int currInd): character " + childIndex + " is not contained in the alphabet.");
			if(children[childIndex] == null)
				return null; // CharSequence provided led to a null node, so there's no such element in the structure.
			else
				return ((TrieNode) children[childIndex]).find(index, currCharIndex + 1);
		}

		/**
		 * Collect all strings with the specified prefix.
		 * @param prefix The String containing the prefix sought.
		 * @param queue The queue that will collect all strings found.
		 */
		void collect(String prefix, Queue<CharSequence> queue){
			if(data != null)
				queue.enqueue(prefix); // Only add this as a valid String if the value is non-null, signifying a contained string.
			for(char c = 0; c < children.length; c++)
				if(children[c] != null)
					((TrieNode) children[c]).collect(prefix + c, queue);
		}

		/**
		 * Collect all strings that match the specified pattern.
		 * @param builtString A String that contains the elements of the match string built up to that point.
		 * @param pattern The pattern we are seeking to match. The period character ('.') is matched with any character.
		 * @param queue The queue that collects the Strings that we will match.
		 */
		void collect(String builtString, String pattern, Queue<CharSequence> queue){
			if(builtString.length() == pattern.length()){ // exhausted pattern
				if(data != null)
					queue.enqueue(builtString);
				return;
			}
			char currChar = pattern.charAt(builtString.length()); // next character of pattern
			/* If the next character of the pattern is a period, then we want
			 * to scan all possible children for valid "descendants". If not, we just want to
			 * scan one child, namely the one indicated by currChar.
			 */
			if(currChar != '.' && currChar < 0 && currChar >= children.length)
				throw new CharacterNotInAlphabetException("TrieNode.collect(String, String, Queue): character " + currChar + " is not contained in the alphabet.");
			if(currChar == '.'){
				for(char c = 0; c < children.length; c++)
					if(children[c] != null)
						((TrieNode)children[c]).collect(builtString + c, pattern, queue);
			}else
				if(children[currChar] != null)
					((TrieNode)children[currChar]).collect(builtString + currChar, pattern, queue);

		}

		/**
		 * Inserts <tt>element</tt> into the node indexed by <tt>index</tt>
		 * @param index A {@link: CharSequence} that indexes into the trie.
		 * @param element The element to insert in the trie.
		 * @return A reference to the data element inside the node before insertion or null
		 * if (a) The node did not pre-exist or (b) The node existed, but was not a "terminal"
		 * node representing a stored string in the trie.
		 */
		T insert(CharSequence index, T element, int currCharIndex){
			/* This method will exhaustively search all the characters in "index". In the process,
			 * it might end up allocating new TrieNodes. When we reach the end of the index, either
			 * we find ourselves in a TrieNode which we freshly allocated (therefore it's data reference
			 * is null) or we find ourselves in a TrieNode that pre-existed. In both cases, we 
			 * make the data reference point to element (that is, in the latter case we over-write
			 * the data element).
			 */
			if(currCharIndex == index.length()){
				T retVal = data;
				data = element;
				return retVal;
			}
			else{
				int nextChar = index.charAt(currCharIndex);
				if(nextChar < 0 || nextChar >= children.length)
					throw new CharacterNotInAlphabetException("TrieNode.insert(CharSequence, T, int): character " + nextChar + " is not contained in the alphabet.");
				if(children[nextChar] == null) // If the node to recurse to is null, allocate it.
					children[nextChar] = new TrieNode();
				return ((TrieNode) children[nextChar]).insert(index, element, currCharIndex + 1);
			}
		}

		/**
		 * Finds the longestPrefixOf
		 * @param inputString
		 * @param acc
		 * @param currentLongest
		 * @param counter
		 * @return
		 */
		String longestPrefixOf(CharSequence inputString, String acc, String currentLongest, int counter){
			String newAcc = acc;
			if(data != null){ // Update the current longest string.
				currentLongest += acc;
				newAcc =""; // newAcc needs to be nullified, because it accumulates the substrings between different prefixes.
			}
			if(counter == inputString.length()) // Exhausted the index string, must return what we got.
				return currentLongest;
			char nextChar = inputString.charAt(counter);
			if(nextChar < 0 || nextChar >= children.length)
				throw new CharacterNotInAlphabetException("TrieNode.longestPrefixOf(CharSequence, String, String, int): Character "
						+ nextChar + " is not contained in the specified alphabet.");
			if(children[nextChar] != null)
				return ((TrieNode)children[nextChar]).longestPrefixOf(inputString, newAcc + nextChar, currentLongest, counter + 1);
			else
				return currentLongest; // Whatever we got needs to be returned.
		}

		/* Traversal methods... */

		/**
		 * Preorder traversal has its normal binary tree semantics.
		 * @param accumulator An ArrayList that gathers all the data scanned by the traversal.
		 */
		void preOrder(ArrayList<T> accumulator){
			if(data != null)
				accumulator.add(data);
			for(int i = 0; i < children.length; i++)
				if(children[i] != null)
					((TrieNode)children[i]).preOrder(accumulator);
		}

		/**
		 * An in-order traversal is defined as traversing the first half of the children
		 * first, then the parent node, then the second half of the children.
		 * @param accumulator An ArrayList that gathers all the data scanned by the traversal.
		 */
		void inOrder(ArrayList<T> accumulator){
			for(int i = 0; i < children.length / 2; i++)
				if(children[i] != null)
					((TrieNode)children[i]).inOrder(accumulator);
			if(data != null)
				accumulator.add(data);
			for(int i = children.length / 2; i < children.length; i++)
				((TrieNode)children[i]).inOrder(accumulator);
		}

		/**
		 * Post-order traversals have the normal binary tree semantics.
		 * @param accumulator An ArrayList that gathers all the data scanned by the traversal.
		 */
		void postOrder(ArrayList<T> accumulator){
			for(int i = 0; i < children.length; i++)
				if(children[i] != null)
					((TrieNode)children[i]).postOrder(accumulator);
			if(data != null)
				accumulator.add(data);
		}

		/**
		 * Level-order traversals operate as in binary trees, but only add the element
		 * to the queue if it is non-null.
		 * @param accumulator An ArrayList that gathers all the data scanned by the traversal.
		 */
		void levelOrder(ArrayList<T> accumulator){
			Queue<TrieNode> queue = new LinkedQueue<TrieNode>();
			queue.enqueue(this);
			while(!queue.isEmpty()){
				TrieNode top = null;
				try {
					top = queue.dequeue();
				} catch (EmptyQueueException e) {
					// dummy
				}
				if(top.data != null)
					accumulator.add(top.data);
				for(Object child : top.children)
					if(child != null)
						queue.enqueue((TrieNode)child);
			}
		}

	}

	protected static final int DEFAULT_ALPHABET_SIZE = 256;
	protected int alphabetSize, size;
	protected TrieNode root;

	public Trie(){
		this(DEFAULT_ALPHABET_SIZE);
	}

	public Trie(int alphabetSize){
		this.alphabetSize =alphabetSize;
		size = 0;
		root = null;
	}

	@Override
	public Iterator<T> iterator() {
		try {
			return preorder();
		} catch (EmptyTreeException e) {
			return new ArrayList<T>().iterator(); // empty 
		}
	}

	@Override
	public T getRoot() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("getRoot(): trie is empty!");
		return root.data;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public T find(T element) throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("find(T element): trie is empty!");
		TrieNode node = root.find(element);
		return (node == null) ? null : node.data;
	}

	/**
	 * Use the provided string of characters to find an element and return it.
	 * @param index A {@link CharSequence} that helps us index into the trie.
	 * @return THe element removed, or null if the index is invalid.
	 * @throws EmptyTreeException
	 */
	public T find(CharSequence index) throws EmptyTreeException{
		if(isEmpty())
			throw new EmptyTreeException("find(CharSequence index): trie is empty!");
		TrieNode found = root.find(index, 0);
		return (found == null) ? null : found.data;
	}

	/**
	 * Delete the element pointed to by the parameter. In tries, the only kind of
	 * deletion that makes sense is "soft deletion", i.e the node pointed to by
	 * the provided index is nullified, but no actual nodes are removed; that would
	 * cause issues with any subsequent strings which have <tt>index</tt> as a prefix.
	 * @param index a {@link CharSequence} indexing the element to be removed.
	 */
	public void delete(CharSequence index){
		if(!isEmpty()){
			TrieNode node = root.find(index, 0);
			if(node != null){ // Valid index
				// If the node contained something, decrease the size of the trie.
				if(node.data != null && size > 0) {
					size--;
					node.data = null;
				}
			}
		}
	}

	public void insert(CharSequence index, T value){
		if(isEmpty())
			root = new TrieNode();
		T valBefore = root.insert(index, value, 0); // In the worst case, allocates index.length() - many nodes.		
		if(valBefore == null && value != null)
			size++;
	}

	public Iterable<CharSequence> keysWithPrefix(CharSequence prefix){
		Queue<CharSequence> queue = new LinkedQueue<CharSequence>();
		if(!isEmpty() && prefix != null){
			TrieNode nodeWithPrefix = root.find(prefix, 0);
			if(nodeWithPrefix == null)
				return queue;
			nodeWithPrefix.collect(prefix.toString(), queue); // collects all the underlying strings and puts them in queue
		}
		return queue;
	}

	public String longestPrefixOf(CharSequence string){
		if(isEmpty())
			return "";
		return root.longestPrefixOf(string, "", "", 0);
	}

	/**
	 * Return an Iterable over Strings that match the pattern. The special character '.' is supposed
	 * to match any possible character, such that the input String "mil." would return an Iterable
	 * containing both "mill" and "milk".
	 * 
	 * @param pattern The CharSequence to search the trie for matche of.
	 * 
	 * @return An Iterable<String> containing all matches of "pattern" that we found.
	 */
	public Iterable<CharSequence> keysThatMatch(CharSequence pattern){
		/* Because the period is interpreted in the aforementioned special manner,
		 * we cannot use TrieNode.find to find the node which matches "string" and work
		 * recursively from there. Instead, we will need to scan the pattern String
		 * character - by - character again. This is what the 3-argument version of
		 * TrieNode.collect() does.
		 */
		Queue<CharSequence> queue = new LinkedQueue<CharSequence>();
		if(!isEmpty())
			root.collect("", pattern.toString(), queue);
		return queue;
	}

	@Override
	public Iterator<T> preorder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("preOrder(): trie is empty!");
		ArrayList<T> accumulator = new ArrayList<T>();
		root.preOrder(accumulator);
		return accumulator.iterator();
	}

	@Override
	public Iterator<T> inOrder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("inOrder(): trie is empty!");
		ArrayList<T> accumulator = new ArrayList<T>();
		root.inOrder(accumulator);
		return accumulator.iterator();
	}

	@Override
	public Iterator<T> postOrder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("postOrder(): trie is empty!");
		ArrayList<T> accumulator = new ArrayList<T>();
		root.postOrder(accumulator);
		return accumulator.iterator();
	}

	@Override
	public Iterator<T> levelOrder() throws EmptyTreeException {
		if(isEmpty())
			throw new EmptyTreeException("levelOrder(): trie is empty!");
		ArrayList<T> accumulator = new ArrayList<T>();
		root.preOrder(accumulator);
		return accumulator.iterator();
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}
}
