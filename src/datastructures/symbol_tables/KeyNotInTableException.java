package datastructures.symbol_tables;

public class KeyNotInTableException extends RuntimeException {
	public KeyNotInTableException(String msg){
		super(msg);
	}
}
