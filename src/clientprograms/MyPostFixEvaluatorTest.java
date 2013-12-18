package clientprograms;
import static org.junit.Assert.*;

import org.junit.Test;

import datastructures.stacks.EmptyStackException;


public class MyPostFixEvaluatorTest {

	@Test
	public void basicTest1() throws InvalidPostfixExpressionException, EmptyStackException{
		assertEquals(3, MyPostfixEvaluator.evaluate("1 2 +"));
	}
	
	@Test
	public void basicTest2() throws InvalidPostfixExpressionException, EmptyStackException {
		assertEquals(0, MyPostfixEvaluator.evaluate("0 0 -"));
	}
	
	@Test
	public void basicTest3() throws EmptyStackException{
		try {
			MyPostfixEvaluator.evaluate("2 A *"); // Should throw an InvalidPostfixExpressionException object.
		} catch(InvalidPostfixExpressionException exc){
			assertTrue(true);
		} 
	}
	
	@Test
	public void intermediateTest1() throws InvalidPostfixExpressionException, EmptyStackException{
		assertEquals(9, MyPostfixEvaluator.evaluate("1 2 4 * +"));
	}
	
	@Test
	public void intermediateTest2() throws EmptyStackException{
		try {
			MyPostfixEvaluator.evaluate("12 4 * +"); // Malformed expression
		} catch(InvalidPostfixExpressionException exc){
			assertTrue(true);
		}
	}
	
	@Test
	public void advancedTest1() throws InvalidPostfixExpressionException, EmptyStackException{
		assertEquals(-14, MyPostfixEvaluator.evaluate("7 4 -3 * 1 5 + / *")); // -3 will be handled by Integer.parseInt()
	}
	
	@Test
	public void advancedTest2() throws InvalidPostfixExpressionException, EmptyStackException{
		assertEquals(4, MyPostfixEvaluator.evaluate("12 4 - 3 7 * 19 - /"));
	}

}
