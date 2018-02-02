package org.connectionGraph.nodes;

/**
 * Represents a 
 */
public class ReferenceVariable extends ReferenceNode {

	public ReferenceVariable(String id) {
		this(id, false);
	}

	public ReferenceVariable(String id, Boolean escapeState) {
		super(id, escapeState);
	}
	
	@Override
	public String toString() {
		return "Reference variable (" + this.getId() + ")";
	}
}
