package org.connectionGraph.nodes;

import java.util.HashMap;
import java.util.Map;

/*
 * Represents a non-object; i.e. a reference variable or a static or non-static field.
 */
public abstract class ReferenceNode extends Node {
	
	protected Map<String, ObjectNode> pointsToNodes;
	protected Map<String, ReferenceNode> deferredNodes;
	
	public ReferenceNode(String id) {
		this(id, false);
	}

	public ReferenceNode(String id, Boolean escapeState) {
		super(id, escapeState);
		
		pointsToNodes = new HashMap<String, ObjectNode>();
		deferredNodes = new HashMap<String, ReferenceNode>();
	}
	
	public ObjectNodeCollection pointsTo() {
		
		ObjectNodeCollection objectNodes = new ObjectNodeCollection();
		
		objectNodes.addAll(pointsToNodes.values());
		
		for(ReferenceNode node : this.deferredNodes.values()) 
		{
			objectNodes.addAll(node.pointsTo());
		}
		return objectNodes;
	}
	

	public void pointsTo(ObjectNode objectNode) {
		this.pointsToNodes.put(objectNode.getId(), objectNode);
	}
	
	public boolean pointsToNothing() {
		return this.pointsTo().size() == 0;
	}
	
	
	public void addNode(Node node) {
		
		// Forget double dispatch for now...
		
		if(node instanceof ObjectNode) {
			this.pointsToNodes.put(node.getId(), (ObjectNode)node);
		} else {
			
			if(node.getId().equals(this.getId())) {
				System.out.println("WARNING: Node " + this.getId() + " gets reference to self!");
			}
			else {
			
				this.deferredNodes.put(node.getId(), (ReferenceNode)node); 
			}
		}
	}
	
	
	@Override
	public void setEscape(boolean escapeState) {
		super.setEscape(escapeState);
		
		for(ReferenceNode referenceNode : this.deferredNodes.values()) {
			referenceNode.setEscape(escapeState);
		}
		for(ObjectNode objectNode : this.pointsToNodes.values()) {
			objectNode.setEscape(escapeState);
		}
	}
	
	public void byPass(ReferenceNode nodeToByPass) 
	{
		if(this.getId().equals(nodeToByPass.getId())) {
			// A node cannot bypass its self.
			return;
		}
		
		if(this.deferredNodes.containsKey(nodeToByPass.getId())) 
		{
			this.deferredNodes.remove(nodeToByPass.getId());

			for(ObjectNode objectNode : nodeToByPass.pointsToNodes.values()) 
			{
				addNode(objectNode);
			}
			for(ReferenceNode nonObjectNode : nodeToByPass.deferredNodes.values()) 
			{
				addNode(nonObjectNode);
			}
		}
	}
	
	public void prettyPrint(int indent) {
		
		String indentStr = new String(new char[indent]).replace("\0", "   ");

		System.out.println(indentStr + this.toString() + (this.getEscapeState() ? "[escape]" : "[noEscape]"));
		for(ObjectNode pointsTo : pointsToNodes.values()) {
			System.out.println(indentStr + "   " + pointsTo.toString() + (this.getEscapeState() ? "[escape]" : "[noEscape]"));
		}
		for(ReferenceNode deferred : deferredNodes.values()) {
			System.out.println(indentStr + "   " + deferred.toString() + (this.getEscapeState() ? "[escape]" : "[noEscape]"));
		}
	}
}