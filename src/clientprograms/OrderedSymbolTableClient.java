package clientprograms;
import datastructures.symbol_tables.*;
import java.util.Random;
import datastructures.lists.*;
public class OrderedSymbolTableClient {
	
	private final static int NUM_ITERS = 20000000;
	
	public static void main(String[] args){
		BSTOrderedST<Integer, Integer> bstBased = new BSTOrderedST<Integer, Integer>();
		RBBSTOrderedST<Integer, Integer> rbbstBased = new RBBSTOrderedST<Integer, Integer>();
		Random generator = new Random();
		generator.setSeed(47); // To allow reproduction of results
		LinkedList<Integer> randomNums = new LinkedList<Integer>();
		for(int i = 0; i < NUM_ITERS; i++)
			randomNums.pushFront(generator.nextInt(101));
		long start = System.currentTimeMillis();
		addVals(bstBased, randomNums);
		long end =  System.currentTimeMillis();
		System.out.println("Adding " + NUM_ITERS + 
				" elements for a Binary Search Tree - based ordered symbol table took: "
					+ (end - start) / 1000 +  " seconds");
		start = System.currentTimeMillis();
		addVals(rbbstBased, randomNums);
		end =  System.currentTimeMillis();
		System.out.println("Adding " + NUM_ITERS + 
				" elements for a Red Black Binary Search Tree - based ordered symbol table took: "
					+ (end - start) / 1000 +  " seconds");
	}
	
	private static void addVals(OrderedST<Integer, Integer> st, List<Integer> nums){
		
		for(Integer i: nums){
			Integer value = st.get(i);
			if(value == null)
				st.put(i, 0);
			else
				st.put(i, value + 1);
		}
	}
}
