package org.asm.jvm.values;

/*
 * Represents an arbitrary byte value.
 */
public class ByteValue extends Integral {

	private static ByteValue instance; 
	
	private ByteValue() {
		
	}
	
	public static ByteValue getInstance() {
		
		if(instance == null) {
			instance = new ByteValue();
		}
		return instance;
	}
}
