package org.connectionGraph.edges;

import org.connectionGraph.nodes.*;

public class PointsToEdge extends Edge {

	private Node from;
	private ObjectNode to;

	public PointsToEdge(Node from, ObjectNode to) {
		this.from = from;
		this.to = to;
	}
	
	@Override
	public String toString() {
		
		return String.format("Points-to-edge: '%s' --> '%s'", this.from.getId(), this.to.getId()); 
	}
}
