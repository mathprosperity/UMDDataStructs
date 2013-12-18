package datastructures.lists;
import java.util.Iterator;
import java.util.ArrayList;
import datastructures.InvalidCapacityException;

/** ArrayListBasedList<T> is a LinearList<T> which is implemented with
 * an ArrayList. It doesn't make much practical sense, since we are actually
 * re-exposing a subset of the ArrayList's interface, but it is theoretically possible.  
 * @author Jason Filippou (jasonfil@cs.umd.edu)
 * @since September 2013
 * @param <T> The type that this ArrayListLinearList holds.
 */
@SuppressWarnings("unchecked")
public class ArrayListBasedList<T> implements List<T> {

	private ArrayList<T> data; 
	private final int INIT_CAPACITY = 50; 

	/**
	 * Constructor. Initializes the data structure with a default
	 * capacity of INIT_CAPACITY elements. 
	 */
	public ArrayListBasedList(){
		data = new ArrayList<T>(INIT_CAPACITY);
	}

	/**
	 * Constructor. Initializes the data structure with the provided capacity. 
	 * @param capacity The number of elements to initialize the ArrayList with.
	 * @throws InvalidCapacityException If the capacity provided is negative
	 */
	public ArrayListBasedList(int capacity) throws InvalidCapacityException{
		if(capacity < 0)
			throw new InvalidCapacityException("Invalid capacity provided!");
		data = new ArrayList<T>(capacity);
	}

	/**
	 * Copy constructor: Initializes the current object with the elements 
	 * contained within the argument.
	 * @param other the list to copy from. 
	 */
	public ArrayListBasedList(List<T> other){
		if(other == null)
			return;
		data = new ArrayList<T>(other.size());
		for(T el: other)
			pushBack(el);
	}

	/** Returns a String-like representation of the object.
	 * @return A representation of the object as a String.
	 */
	public String toString(){
		String retVal = "[";
		for(int i = 0; i < data.size(); i++){
			retVal += data.get(i);
			if(i < data.size() - 1)
				retVal += ", ";
		}
		retVal += "]";
		return retVal;
	}
	
	/**
	 * Standard equals() method. Two LinearLists are equal if they contain
	 * the exact same elements at the same positions. Note that this equals()
	 * method allows for comparing an ArrayLinearList with an ArrayListLinearList,
	 * and potentially allowing them to be equal because they maintain the aforementioned
	 * contract! We are interested in ADT comparison, not 1-1 memory allocation comparison.
	 * 
	 * @param other The Object reference to compare to
	 * @return true if the two objects are considered "equal", with equality defined above.
	 */
	@Override 
	public boolean equals(Object other){
		if(other == null)
			return false;
		if(!(other instanceof List<?>))
			return false;
		List<T> otherList= (List<T>)other;
		if(otherList.size() != size())
			return false;
		Iterator<T> ito = otherList.iterator(), itc = this.iterator();
		while(ito.hasNext())
			if(!ito.next().equals(itc.next()))
				return false;
		return true;
	}

	@Override
	public Iterator<T> iterator() {
		// In this implementation, we can simply return the
		// underlying ArrayList's iterator.
		return data.iterator();
	}

	@Override
	public void pushFront(T element) {
		data.add(0, element);
	}

	@Override
	public void pushBack(T element) {
		data.add(element);
	}

	@Override
	public T getFirst() throws EmptyListException {
		T retVal = null;
		try {
			retVal = get(0);
		} catch(IllegalListAccessException ile){//Dummy 
		}
		return retVal;
	}

	@Override
	public T getLast() throws EmptyListException {
		T retVal = null;
		try {
			retVal = get(size() - 1);
		} catch(IllegalListAccessException ile){//Dummy 
		}
		return retVal;
	}

	@Override
	public T get(int index) throws EmptyListException, IllegalListAccessException {
		if(data.isEmpty())
			throw new EmptyListException("get(" + index + "): list is empty!");
		if(index < 0 || index >= size())
			throw new IllegalListAccessException("get(): Index of " + index + " was out-of-bounds.");
		return data.get(index);
	}
	
	/* Because we are essentially implementing a subset of standard Collection methods,
	 * the following methods are almost all one-liners, because ArrayLists are also Collections.
	 */
	@Override
	public boolean contains(T element) {
		return data.contains(element);
	}

	@Override
	public boolean remove(T element) {
		return data.remove(element);
	}

	@Override
	public void remove(int index) throws IllegalListAccessException {
		if(index < 0 || index >= data.size())
			throw new IllegalListAccessException("remove(int): provided index value " + index + 
									" was out of bounds.");
		data.remove(index);
	}

	@Override
	public boolean removeAll(T element) {
		// Unfortunately, we cannot call the ArrayList's removeAll() implementation, because
		// it expects a Collection<T> as input, i.e its semantics are different from our own 
		// semantics of removeAll(), as specified in the JavaDoc.
		boolean contained = false;
		while(data.contains(element)){
			contained = true;
			data.remove(element);
		}
		return contained;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	

}
