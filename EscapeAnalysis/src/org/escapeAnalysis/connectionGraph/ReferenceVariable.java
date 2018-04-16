package org.escapeAnalysis.connectionGraph;

/**
 * Represents a 
 */
public class ReferenceVariable extends ReferenceNode {

	public ReferenceVariable(String id) {
		this(id, EscapeState.UNRESOLVED);
	}

	public ReferenceVariable(String id, EscapeState escapeState) {
		super(id, escapeState);
	}
	
	@Override
	public String toString() {
		return "Reference variable (" + this.id() + ")";
	}
}
