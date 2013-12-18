package clientprograms;
import datastructures.queues.*;

import java.util.Scanner;

/** Codes.java is an example client that uses a queue to store a "repeated key",
 * which is an improvement over the classic Ceasar Cipher. According to this key,
 * every element in the original message is shifted by the amount specified in the front
 * element of the queue, which is then dequeued and re-enqueued. 
 * 
 * See chapter 15.2 of the book "Java Foundations, 2nd edition, Addison/Wensley" for more
 * in-depth information.
 * 
 * @author Jason Filippou
 * 
 * @since September 2013
 *
 */

public class Codes {
	
	// Let's encode a static key to make things easy.
	private static int[] key = {-2, 3, 0, -1, 10};
	
	public static void main(String[] args) throws EmptyQueueException{
		Scanner sc = new Scanner(System.in);
		System.out.print("Please provide a message to encode.");
		String message = sc.nextLine();
		Queue<Integer> encodingKey = new LinkedQueue<Integer>(), // Any queue should do
						decodingKey = new LinkedQueue<Integer>();
		Encoder e = new Encoder(key);
		String cipherText = e.encode(message), clearText = e.decode(cipherText);
		System.out.println("The encoded message is: " + cipherText);
		System.out.println("The decoded message is: " + clearText);
	}
}
