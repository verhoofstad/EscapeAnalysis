package org.connectionGraph.nodes;

/**
 * Represents a connection graph node.
 */
public abstract class Node {

	private final String id;
	
	private boolean escapeState;
	
	public Node(String id) {
		this.id = id;
		this.escapeState = false;
	}
	
	public Node(String id, boolean escapeState) {
		this.id = id;
		this.escapeState = escapeState;
	}
	
	public String getId() {
		return this.id;
	}
	
	public boolean getEscapeState() {
		return this.escapeState;
	}
	
	public void setEscape(boolean escapeState) {
		this.escapeState = escapeState;
	}
	
	public final void pushEscapeState() {
		if(this.getEscapeState()) {
			this.setEscape(true);
		}
	}
}