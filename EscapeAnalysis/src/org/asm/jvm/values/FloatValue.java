package org.asm.jvm.values;

/*
 * Represents an arbitrary float value.
 */
public class FloatValue extends FloatingPoint {

	private static FloatValue instance; 
	
	private FloatValue() {
		
	}
	
	public static FloatValue getInstance() {
		
		if(instance == null) {
			instance = new FloatValue();
		}
		return instance;
	}
}
