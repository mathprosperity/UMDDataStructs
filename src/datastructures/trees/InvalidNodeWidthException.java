package datastructures.trees;

/**
 * An <tt>InvalidNodeWidthException</tt> is used by {@link BTree}
 * whenever the user supplies a node width less than 3.
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since December 2013
 *
 */
public class InvalidNodeWidthException extends Exception {
	public InvalidNodeWidthException(String msg){
		super(msg);
	}

}
