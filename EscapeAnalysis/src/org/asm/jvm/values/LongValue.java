package org.asm.jvm.values;

/*
 * Represents an arbitrary long value.
 */
public class LongValue extends Integral {

	private static LongValue instance; 
	
	private LongValue() {
		
	}
	
	public static LongValue getInstance() {
		
		if(instance == null) {
			instance = new LongValue();
		}
		return instance;
	}
	
	@Override
	public int getComputationalTypeCategory() {
		return 2;
	}
}
