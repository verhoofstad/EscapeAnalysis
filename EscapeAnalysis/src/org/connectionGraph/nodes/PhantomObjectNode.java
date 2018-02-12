package org.connectionGraph.nodes;

import java.util.UUID;

public class PhantomObjectNode extends ObjectNode {

	public PhantomObjectNode() {
		super(UUID.randomUUID().toString(), null);
	}
	
	@Override
	public String toString() {
		return "Phantom object (" + this.getId() + ")";
	}
}
