package org.asm.jvm.values;


/*
 * Represents an arbitrary double value.
 */
public class DoubleValue extends FloatingPoint {

	private static DoubleValue instance; 
	
	private DoubleValue() {
		
	}
	
	public static DoubleValue getInstance() {
		
		if(instance == null) {
			instance = new DoubleValue();
		}
		return instance;
	}
	
	@Override
	public int getComputationalTypeCategory() {
		return 2;
	}
}
