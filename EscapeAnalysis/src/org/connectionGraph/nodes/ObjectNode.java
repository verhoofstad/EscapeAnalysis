package org.connectionGraph.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

/**
 * Represents an object.
 */
public class ObjectNode extends Node {

	private Type objectType;

	// Edges to both the static and non-static field nodes;
	private Map<String, FieldNode> fieldNodes;
	
	public ObjectNode(String id, Type objectType) {
		super(id);
		
		this.fieldNodes = new HashMap<String, FieldNode>();
		this.objectType = objectType;
	}
	
	
	public Collection<FieldNode> getFieldNodes() 
	{
		return this.fieldNodes.values();
	}
	
	public Type getObjectType() {
		return this.objectType;
	}

	public void addNode(FieldNode node) 
	{
		this.fieldNodes.put(node.getId(), node);
	}
	

	public void prettyPrint() {
		System.out.println(this.toString());
		for(FieldNode fieldNode : this.fieldNodes.values()) {
			System.out.println("   " + fieldNode.toString());
			
			fieldNode.prettyPrint();
		}
	}
	
	/**
	 * Gets a the field node with a specified id -or- null if the current object does not contain a field node with the specified id.
	 * 
	 * @param id
	 * @return
	 */
	public FieldNode getFieldNode(String id) 
	{
		return this.fieldNodes.get(id);
	}

	@Override
	public void setEscape(Boolean escapeState) {
		super.setEscape(escapeState);
		
		for(FieldNode fieldNode : this.fieldNodes.values()) {
			fieldNode.setEscape(escapeState);
		}
	}

	
	@Override
	public String toString() {
		return "Object (" + this.getId() + ")";
	}
}
