package clientprograms;
public class InvalidPostfixExpressionException extends Exception{
	
	private String message;
	
	public InvalidPostfixExpressionException(String msg){
		message = msg;
	}
	
	@Override
	public String getMessage(){
		return message;
	}
}
