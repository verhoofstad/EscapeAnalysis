package org.connectionGraph.nodes;

public class PhantomObjectNode extends Node {

	public PhantomObjectNode(String id) {
		super(id);
	}
	
	@Override
	public String toString() {
		return "Phantom object (" + this.getId() + ")";
	}
}
