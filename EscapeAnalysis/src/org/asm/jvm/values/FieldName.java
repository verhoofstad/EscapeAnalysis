package org.asm.jvm.values;

/*
 * A special value that represents a field name of a class.
 */
public class FieldName extends Value {

	private String fieldName;
	
	public FieldName(String fieldName ) {
		this.fieldName = fieldName;
	}
	
}
