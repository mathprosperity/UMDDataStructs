package datastructures.lists;

@SuppressWarnings("serial")
public class EmptyListException extends Exception{
	public EmptyListException(String msg){
		super(msg); // Another way to ensure that the message is saved. Pretty straightforward.
	}
}
