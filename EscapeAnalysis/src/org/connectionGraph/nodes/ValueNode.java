package org.connectionGraph.nodes;

public class ValueNode extends Node {

	private static ValueNode instance;
	
	private ValueNode() {
		super("ValueNode");
		
	}

	public static ValueNode getInstance() {
		
		if(instance == null) {
			instance = new ValueNode();
		}
		return instance;
	}
	
}
