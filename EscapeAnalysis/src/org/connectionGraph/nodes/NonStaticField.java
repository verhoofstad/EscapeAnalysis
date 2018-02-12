package org.connectionGraph.nodes;


/**
 * Represents a field. 
 */
public class NonStaticField extends FieldNode {

	public NonStaticField(String id) {
		super(id);
	}
	
	public NonStaticField(String id, boolean escapeState) {
		super(id, escapeState);
	}
	
	@Override
	public String toString() {
		return "Non-static field (" + this.getId() + ")";
	}
}
