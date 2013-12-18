package clientprograms;
import java.util.Scanner;

import datastructures.stacks.EmptyStackException;

/* A client main method for class MyPostfixEvaluator. The only assumption made is that 
 * the operands and operators are delimited by spaces, so that the String tokenization works
 * correctly.
 * 
 * TODO: (1) Make a GUI interface
 * 		(2) Build your own stack.
 */

public class MyPostfixEvaluatorClient {
	public static void main(String[] args) throws EmptyStackException{
		String answer;
		Scanner cmdIn = new Scanner(System.in);
		do{
			System.out.print("Please provide a postfix expression to evaluate:");
			String expression = cmdIn.nextLine();
			// I don't really see the point for a non-static method, so I'll
			// make it static.
			int value;
			try {
				value = MyPostfixEvaluator.evaluate(expression);
				System.out.println("The value for the expression you specified is: " + value + ".");
			}catch(InvalidPostfixExpressionException exc){
				System.out.println(exc.getMessage());
			} 
			System.out.println("Another expression? Y/N");
			answer = cmdIn.next();
			while(!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")){
				System.out.println("Please give \"Y\" or \"N\".");
				answer = cmdIn.next();
			}
		}while(answer.equalsIgnoreCase("Y"));
		System.out.println("Exiting...");
		cmdIn.close();
	}
}
