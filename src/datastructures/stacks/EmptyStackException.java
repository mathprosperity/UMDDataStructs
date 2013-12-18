package datastructures.stacks;

@SuppressWarnings("serial")
public class EmptyStackException extends Exception{
	public EmptyStackException(String msg){
		super(msg); // Another way to ensure that the message is saved. Pretty straightforward.
	}
}
