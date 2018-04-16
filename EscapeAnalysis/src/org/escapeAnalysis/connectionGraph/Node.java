package org.escapeAnalysis.connectionGraph;

/**
 * Represents a connection graph node.
 */
public abstract class Node {

	private final String id;
	private EscapeState escapeState;
	
	public Node(String id) {
		this.id = id;
		this.escapeState = EscapeState.UNRESOLVED;
	}
	
	public Node(String id, EscapeState escapeState) {
		this.id = id;
		this.escapeState = escapeState;
	}
	
	public String id() {
		return this.id;
	}
	
	public EscapeState getEscapeState() {
		return this.escapeState;
	}
	
	public void setEscape(EscapeState escapeState) {
		this.escapeState = escapeState;
	}
}