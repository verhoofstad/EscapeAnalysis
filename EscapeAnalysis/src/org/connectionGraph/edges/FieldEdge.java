package org.connectionGraph.edges;

import org.connectionGraph.nodes.Node;
import org.connectionGraph.nodes.ObjectNode;

public class FieldEdge extends Edge {
	private ObjectNode from;
	private Node to;

	public FieldEdge(ObjectNode from, Node to) {
		
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		
		return String.format("Field-edge: '%s' --> '%s'", this.from.getId(), this.to.getId()); 
	}
}
