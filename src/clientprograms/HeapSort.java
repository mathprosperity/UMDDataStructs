package clientprograms;

import datastructures.trees.*;

/**
 * Demonstrates an example usage of heapsort, a standard sorting
 * algorithm. Given a collection of comparable objects, heapsort inserts
 * each object into a max(min)heap and then pulls the maximum(minimum)
 * element of the heap sequentially, one-by-one, until the heap is emptied.
 * @author jason
 * @since October 2013
 */
public class HeapSort {
	
	private  static String[] sarr = {"Mitchell", "Brianna", "Courtney", "Danielle", 
			"Nicole", "Emma"};
	
	public static void main(String[] args) throws EmptyHeapException{
		MinHeap<String> h = new ArrayMinHeap<>();
		System.out.println("Unsorted elements:");
		for(String s: sarr){
			System.out.print(s + " ");
			h.add(s);
		}
		System.out.println("\nSorted elements:");
		while(!h.isEmpty())
			System.out.print(h.removeMin() + " ");
		System.out.println("\nDone!");
	}
}
