package org.escapeAnalysis.connectionGraph;


/**
 * Represents a field. 
 */
public class NonStaticField extends FieldNode {

	public NonStaticField(String id) {
		super(id);
	}
	
	public NonStaticField(String id, EscapeState escapeState) {
		super(id, escapeState);
	}
	
	@Override
	public String toString() {
		return "Non-static field (" + this.id() + ")";
	}
}
