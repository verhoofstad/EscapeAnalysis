package org.connectionGraph.nodes;

/*
 * Represents a static or non-static field node.
 */
public abstract class FieldNode extends ReferenceNode 
{
	public FieldNode(String id) {
		super(id);
	}
}
