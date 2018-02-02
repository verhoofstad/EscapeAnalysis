package org.connectionGraph.nodes;

/**
 * Represents a connection graph node.
 */
public abstract class Node {

	private final String id;
	
	private Boolean escapeState;
	
	public Node(String id) {
		this.id = id;
		this.escapeState = false;
	}
	
	public Node(String id, Boolean escapeState) {
		this.id = id;
		this.escapeState = escapeState;
	}
	
	public String getId() {
		return this.id;
	}

	
	public Boolean getEscapeState() {
		return this.escapeState;
	}
	
	public void setEscape(Boolean escapeState) {
		this.escapeState = escapeState;
	}
	
	public void pushEscapeState() {
		if(this.getEscapeState()) {
			this.setEscape(true);
		}
	}
}