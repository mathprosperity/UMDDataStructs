package clientprograms;
/*** MyPostfixEvaluator.java:An implementation of postfix evaluation through a 
 * Java stack generic ADT. This class is an enhancement of the book's version in that it also 
 * checks for bad input.
 */

//import java.util.EmptyStackException;
//import java.util.Stack;// Eventual goal is to replace this import with my own stack
import datastructures.*;
import datastructures.stacks.EmptyStackException;
import datastructures.stacks.LinkedStack;
import datastructures.stacks.Stack;

import java.util.Scanner;

public class MyPostfixEvaluator {
	
	
	/* Some constants to help with readability. */
	private static final char ADD = '+', SUBTRACT = '-', MULTIPLY = '*',  DIVIDE = '/';
	
	/* A private method which determines the operator read and then applies the implied
	 * operation to the top two elements of the stack.
	 */
	private static int performOperation(char operator, int op1, int op2){
		int retVal = 0;
		switch(operator){
		case ADD: 
			retVal = op1+op2;
			break;
		case SUBTRACT:
			retVal = op1-op2;
			break;
		case MULTIPLY:
			retVal = op1*op2;
			break;
		case DIVIDE:
			retVal = op1/op2;
			break;
		default: // This will not really be needed because the caller will've filtered out other possible values.
			break;
		}
		return retVal;
	}
	
	/* This is the only public method of this class, and does all the hard work.
	 * 
	 */
	
	public static int evaluate(String expression) throws InvalidPostfixExpressionException, EmptyStackException{
		Scanner tokenizer = new Scanner(expression); // Default token delimiter is whitespace.
		Stack<Integer> values = new LinkedStack<Integer>(); // Any one of our Stacks will do the trick :) 
		while(tokenizer.hasNext()){
			String token = tokenizer.next();
			if(token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")){
				int op1, op2;
				try {
					op2 = values.pop();
					op1 = values.pop();
				} catch(EmptyStackException e){ // One possible error case when evaluating postfix expressions
					throw new InvalidPostfixExpressionException("Invalid postfix expression format: Operator " + 
											token + " was applied to less than 2 operands.");
				}
				values.push(new Integer(performOperation(token.charAt(0), op1, op2)));
			} else{ // Possibly numerical token, but also possibly a token with invalid characters!
				int num;
				try {
					num = Integer.parseInt(token);
				} catch(NumberFormatException e){ 
					throw new InvalidPostfixExpressionException("Invalid postfix expression format: Non-numerical operand "
							+ token + " found.");
				} 
				values.push(num);
			}	
		}
		// After we've scanned the whole String, the stack needs to contain exactly one element.
		// Otherwise, the expression is not well defined.
		if(values.size() != 1)
			throw new InvalidPostfixExpressionException("Invalid postfix expression format: After scanning input, stack size was: " +
														values.size() + ".");
		return values.peek();
	}
}
