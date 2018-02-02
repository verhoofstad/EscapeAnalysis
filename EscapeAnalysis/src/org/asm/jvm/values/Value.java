package org.asm.jvm.values;

/*
 * Represents a Java value (primitive or reference).
 */
public abstract class Value {

	public int getComputationalTypeCategory() {
		return 1;
	}
}
