package org.asm.jvm.values;

/*
 * Represents an arbitrary char value.
 */
public class CharValue extends Integral {

	private static CharValue instance; 
	
	private CharValue() {
		
	}
	
	public static CharValue getInstance() {
		
		if(instance == null) {
			instance = new CharValue();
		}
		return instance;
	}
}
