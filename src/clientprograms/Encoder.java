package clientprograms;
import datastructures.queues.*;

/**
 * The Encoder class is provided a repeating key which it then stores in
 * two separate queues for encoding and decoding. Arbitrary length Unicode
 * messages can be then passed to the Encoder for encoding and decoding. 
 * 
 * @author Jason Filippou
 * 
 * @since September 2013
 */
public class Encoder {
	
	private Queue<Integer> encodingKey, decodingKey;
	private static int[] DEFAULT_KEY = {5, 1, -3}; 
	
	
	/**
	 * Default constructor. Uses a default key for coding and encoding.
	 */
	public Encoder(){
		this(DEFAULT_KEY);
	}
	
	/**
	 * Constructor that provides the key.
	 * @param key The key to be used, in the form of an int array.
	 */
	public Encoder(int[] key){
		if(key == null)
			key = DEFAULT_KEY;
		// We can use pretty much any queue we like:
		encodingKey = new LinkedQueue<Integer>();
		decodingKey = new LinkedQueue<Integer>();
		for(Integer i : key){
			encodingKey.enqueue(i);
			decodingKey.enqueue(i);
		}
	}
	
	/**
	 * Encodes and returns the message passed according to the stored key.
	 * @param msg The message to encode (cleartext).
	 * @return The encrypted message (ciphertext).
	 * @throws EmptyQueueException never really
	 */
	public String encode(String msg) throws EmptyQueueException{
		String encodedMsg = "";
		for(int i = 0; i < msg.length(); i++){
			int offset = encodingKey.dequeue();
			char c = (char)((int)msg.charAt(i) + offset); // We allow any Unicode character as the "encrypted" character.
			encodedMsg += c;
			encodingKey.enqueue(offset);
		}
		encodingKey = new LinkedQueue<Integer>(encodingKey); // will reset the encodingKey for future use.
		return encodedMsg;
	}
	
	/**
	 * 
	 * @param msg The ciphertext to be decoded.
	 * @return The original cleartext.
	 * @throws EmptyQueueException never really
	 */
	public String decode(String msg) throws EmptyQueueException{
		String decodedMsg = "";
		for(int i = 0; i < msg.length(); i++){
			int offset = decodingKey.dequeue();
			char c = (char)((int)msg.charAt(i) - offset); // We allow any Unicode character as the "encrypted" character.
			decodedMsg += c;
			decodingKey.enqueue(offset);
		}
		decodingKey = new LinkedQueue<Integer>(decodingKey); // will reset the decodingKey for future use.
		return decodedMsg;
	}
}
