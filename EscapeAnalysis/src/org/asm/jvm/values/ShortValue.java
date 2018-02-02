package org.asm.jvm.values;

/*
 * Represents an arbitrary short value.
 */
public class ShortValue extends Integral {

	private static ShortValue instance; 
	
	private ShortValue() {
		
	}
	
	public static ShortValue getInstance() {
		
		if(instance == null) {
			instance = new ShortValue();
		}
		return instance;
	}
}
