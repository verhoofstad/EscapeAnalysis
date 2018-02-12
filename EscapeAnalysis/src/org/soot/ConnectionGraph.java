package org.soot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.connectionGraph.nodes.*;

import soot.RefType;

/**
 * Represents a connection graph for a single method. 
 */
public class ConnectionGraph {

	private ObjectNodeCollection objectNodes;
	private Map<String, ReferenceVariable> referenceVariables;
	private List<PhantomObjectNode> phantomObjectNodes;
	
	public ConnectionGraph() {
		this.objectNodes = new ObjectNodeCollection();
		this.referenceVariables = new HashMap<String, ReferenceVariable>();
		this.phantomObjectNodes = new ArrayList<PhantomObjectNode>();
	}
	
	public ObjectNodeCollection getObjects() {
		
		return this.objectNodes;
	}

	/*
	 * Adds a local variable for 'this' with its escape state set to ESCAPE.
	 */
	public void addThisVariable(String name) {
		this.referenceVariables.put(name, new ReferenceVariable(name, true));
	}
	
	/*
	 * Adds a local variable for a parameter with its escape state set to ESCAPE.
	 */
	public void addParameterVariable(String name) {
		this.referenceVariables.put(name, new ReferenceVariable(name, true));
	}
	
	public void addObjectNode(ObjectNode node) {
		this.objectNodes.add(node);
	}
	
	public void addPhantomObjectNode(PhantomObjectNode node) {
		this.phantomObjectNodes.add(node);
	}
	
	
	public ObjectNodeCollection getObjectsOfType(RefType objectType) {

		ObjectNodeCollection objectNodes = new ObjectNodeCollection();
		
		for(ObjectNode node : this.objectNodes) 
		{
			if(node.getObjectType().getClassName().equals(objectType.getClassName())) {
				objectNodes.add(node);
			}
		}
		return objectNodes;
	}

	public ReferenceVariable getLocal(String localName) 
	{
		return this.referenceVariables.get(localName);
	}
	
	public ReferenceVariable ensureLocal(String localName) 
	{
		if(!this.referenceVariables.containsKey(localName)) {
			this.referenceVariables.put(localName, new ReferenceVariable(localName));
		}
		return this.referenceVariables.get(localName);
	}
	
	
	
	public void byPass(ReferenceVariable nodeToByPass) 
	{
		byPassNonObjectNode(nodeToByPass);
	}

	public void byPass(NonStaticField nodeToByPass) 
	{
		byPassNonObjectNode(nodeToByPass);
	}
	
	private void byPassNonObjectNode(ReferenceNode nodeToByPass) 
	{
		for(ReferenceNode predecessorNode : this.referenceVariables.values()) 
		{
			predecessorNode.byPass(nodeToByPass);
		}
		for(ObjectNode objectNode : this.objectNodes) 
		{
			for(FieldNode predecessorNode : objectNode.getFieldNodes()) {
				predecessorNode.byPass(nodeToByPass);
			}
		}
	}
	
	
	public void pushEscapeState() {
		for(ReferenceNode referenceNode : this.referenceVariables.values()) 
		{
			referenceNode.pushEscapeState();
		}
		for(ObjectNode objectNode : this.objectNodes) 
		{
			objectNode.pushEscapeState();
		}
	}

	
	public void printEdges() {
		
		for(ObjectNode objectNode : this.objectNodes) {

			objectNode.prettyPrint(0);
		}
		for(ReferenceNode nonObjectNode : this.referenceVariables.values()) {

			nonObjectNode.prettyPrint(0);
		}
		for(PhantomObjectNode phantomObjectNode : this.phantomObjectNodes) {
			phantomObjectNode.prettyPrint(0);
		}
	}
}
