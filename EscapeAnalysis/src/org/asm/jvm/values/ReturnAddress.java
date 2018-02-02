package org.asm.jvm.values;

/*
 * Represents a return address.
 */
public class ReturnAddress extends PrimitiveValue {

	private static ReturnAddress instance; 
	
	private ReturnAddress() {
		
	}
	
	public static ReturnAddress getInstance() {
		
		if(instance == null) {
			instance = new ReturnAddress();
		}
		return instance;
	}
}
