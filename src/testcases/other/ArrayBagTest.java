package testcases.other;

import static org.junit.Assert.*;
import java.util.*; // For testing iterator().

import org.junit.Test;

import datastructures.other.*;
public class ArrayBagTest {

	private Bag<Double> doubleBag = new ArrayBag<Double>();

	@Test
	public void testSimpleConstructorAndSize(){
		assertTrue(doubleBag.isEmpty());
		assertTrue(doubleBag.size() == 0);
		doubleBag.add(2.8);
		assertFalse(doubleBag.isEmpty());
		assertTrue(doubleBag.size() == 1);
		doubleBag.clear();
		assertTrue(doubleBag.isEmpty());
	}

	@Test
	public void testAddAndSize(){
		doubleBag.add(10.5);
		doubleBag.add(-10.5);
		doubleBag.add(0.00002);
		doubleBag.add(-1.0);
		assertEquals(doubleBag.size(), 4);
		doubleBag.clear();
	}

	@Test
	public void testContains(){
		doubleBag.add(-1.0);
		doubleBag.add(10.0);
		assertTrue(doubleBag.contains(-1.0));
		assertFalse(doubleBag.contains(null));
	}

	/* While it is true that the order in which our overloading of the
	 * iterator() method returns the elements is not important, it is
	 * still important to test it to see that (a) It's runtime error - free
	 * and (b) It satisfies the constraints required of a Bag element accessing.
	 * We will also check to see whether we indeed get the elements in the order
	 * that we required them to come out in, but this is only required for our own
	 * personal use; the interface does not care.
	 */
	@Test
	public void testIterator(){
		TreeSet<Double> duplicateChecker = new TreeSet<Double>();	
		Double[] darr = {2.23, 1.3, 9.1, 2.3, 4.1, 9.0, 6.7, 10.1, -1.23, 0.01, -2.3};
		ArrayList<Double> arrld = new ArrayList<Double>();
		for(Double d: darr){
			arrld.add(d);
			doubleBag.add(d);
		}
		// (1)-(a) Let's check whether the iterator works correctly first.
		// We will then check whether the for-each loop works correctly
		// as well, which it should.
		Iterator<Double> itd = doubleBag.iterator();
		ArrayList<Double> temp = new ArrayList<Double>();
		while(itd.hasNext()){
			Double val = itd.next();
			assertTrue(arrld.contains(val));
			temp.add(val);
			assertTrue(duplicateChecker.add(val));
		}
		itd = doubleBag.iterator(); //re-initialize iterator
		for(Double t: arrld)
			assertTrue(temp.contains(t)); // ALL elements in the ArrayList have to have been returned. 
		// (1)-(b): Make the same checks as above, only with a for-each loop.
		temp = new ArrayList<Double>(); // gc will collect the old ArrayList and Set.
		duplicateChecker = new TreeSet<Double>();
		System.gc();
		for(Double val: doubleBag){
			assertTrue(arrld.contains(val));
			temp.add(val);
			assertTrue(duplicateChecker.add(val));
		}
		for(Double t: arrld)
			assertTrue(temp.contains(t)); // ALL elements in the ArrayList have to have been returned.

		// (2): Check whether our expected order is taking place.
		int index = 0;
		for(Double d: doubleBag){
			if(index >= arrld.size())
				index = 1;
			assertEquals(d, arrld.get(index));
			index += 2;
		}
		doubleBag.clear();
	}
}	
