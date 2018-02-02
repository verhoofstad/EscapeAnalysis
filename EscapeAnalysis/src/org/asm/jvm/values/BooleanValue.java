package org.asm.jvm.values;

/*
 * Represents a boolean value.
 */
public class BooleanValue extends PrimitiveValue {

	private static BooleanValue instance; 
	
	private BooleanValue() {
		
	}
	
	public static BooleanValue getInstance() {
		
		if(instance == null) {
			instance = new BooleanValue();
		}
		return instance;
	}
}
