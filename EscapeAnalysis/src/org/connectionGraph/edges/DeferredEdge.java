package org.connectionGraph.edges;

import org.connectionGraph.nodes.Node;

public class DeferredEdge extends Edge {

	private Node from;
	private Node to;

	public DeferredEdge(Node from, Node to) {
		
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String toString() {
		
		return String.format("Deferred-edge: '%s' --> '%s'", this.from.getId(), this.to.getId()); 
	}

}
