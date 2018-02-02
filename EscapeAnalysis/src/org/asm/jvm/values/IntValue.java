package org.asm.jvm.values;

/*
 * Represents an arbitrary integer value.
 */
public class IntValue extends Integral {

	private static IntValue instance; 
	
	private IntValue() {
		
	}
	
	public static IntValue getInstance() {
		
		if(instance == null) {
			instance = new IntValue();
		}
		return instance;
	}
}
