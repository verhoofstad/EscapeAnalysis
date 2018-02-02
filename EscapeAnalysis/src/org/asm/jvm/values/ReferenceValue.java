package org.asm.jvm.values;

import org.asm.jvm.ObjectInstance;

public class ReferenceValue extends Value {

	private static ReferenceValue nullReference;
	private ObjectInstance obj;
	
	private ReferenceValue() {
		
	}
	
	public ReferenceValue(ObjectInstance obj) {
		this.obj = obj;
	}
	
	public static ReferenceValue getNullReference() {
		if(nullReference == null) {
			nullReference = new ReferenceValue();
		}
		return nullReference;
	}
	
	public Boolean isNull() {
		return this.obj == null;
	}
}
