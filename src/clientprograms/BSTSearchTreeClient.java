package clientprograms;
import datastructures.trees.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
/** <p>A client that compare our Binary Search Tree and Red-Black Binary
 * Search Tree implementations. The goal is to make it clear that RB-BSTs
 * are much more efficient for searching, since they retain balance.</p>
 * 
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * 
 * @since December 2013
 */

public class BSTSearchTreeClient {
	
	private final static int HOW_MANY = 10000;

	public static void main(String[] args) {
		BinarySearchTree<Integer> bst, rbbst;
		bst = new LinkedBinarySearchTree<Integer>();
		rbbst = new RedBlackBinarySearchTree<Integer>();
		Random r = new Random();
		r.setSeed(47);// To allow reproduction of results
		ArrayList<Integer> arrL = new ArrayList<Integer>();
		for(int i = 0; i < HOW_MANY; i++)
			arrL.add(r.nextInt(101));
		
		Object[] nums = arrL.toArray();
		Arrays.sort(nums);
		
		// Fill up both trees
		for(Object i: nums){
			bst.add((Integer) i);
			rbbst.add((Integer)i);
		}
		System.out.println("Added " + HOW_MANY + " random integers in [0, 100] to both trees.");
		
		// Compare times for searching minimum and maximum
		long before = System.currentTimeMillis();
		try {
			bst.getMax();
		} catch (EmptyTreeException e) {
			// Dummy
			e.printStackTrace();
		}
		long after = System.currentTimeMillis();
		long timeInSecs = (after - before);
		System.out.println("Took " + timeInSecs + " seconds to find the maximum in a classic binary search tree.");
		before = System.currentTimeMillis();
		try {
			bst.getMin();
		} catch (EmptyTreeException e) {
			// Dummy
			e.printStackTrace();
		}
		after = System.currentTimeMillis();
		timeInSecs = (after - before);
		System.out.println("Took " + timeInSecs + " seconds to find the minimum in a classic binary search tree.");
		
		try {
			bst.getMax();
		} catch (EmptyTreeException e) {
			// Dummy
			e.printStackTrace();
		}
		after = System.currentTimeMillis();
		timeInSecs = (after - before);
		System.out.println("Took " + timeInSecs + " seconds to find the maximum in a red-black binary search tree.");
		before = System.currentTimeMillis();
		try {
			bst.getMin();
		} catch (EmptyTreeException e) {
			// Dummy
			e.printStackTrace();
		}
		after = System.currentTimeMillis();
		timeInSecs = (after - before);
		System.out.println("Took " + timeInSecs + " seconds to find the minimum in a red-black binary search tree.");
	}

}
