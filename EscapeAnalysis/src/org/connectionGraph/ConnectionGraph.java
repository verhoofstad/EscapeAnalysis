package org.connectionGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.connectionGraph.nodes.*;
import org.objectweb.asm.Type;
import org.asm.jvm.AccessFlags;
import org.connectionGraph.edges.*;

/**
 * Represents a connection graph for a single method. 
 */
public class ConnectionGraph {

	private ObjectNodeCollection objectNodes;
	private Map<Integer, ReferenceVariable> referenceVariables;
	
	public ConnectionGraph() {
		this.objectNodes = new ObjectNodeCollection();
		this.referenceVariables = new HashMap<Integer, ReferenceVariable>();
	}
	
	
	public ObjectNodeCollection getObjects() {
		
		return this.objectNodes;
	}
	
	public void initializeParamaterNodes(Type methodType, AccessFlags methodFlags) {
		
		int localVariableIndex = 0;
		
		if(!methodFlags.isStatic()) {
			this.referenceVariables.put(localVariableIndex, new ReferenceVariable("0", true));
			localVariableIndex++;
		}
		
		for(Type paramType : methodType.getArgumentTypes()) 
		{
			if(paramType.getSort() == Type.OBJECT) {
				this.referenceVariables.put(localVariableIndex, new ReferenceVariable("" + localVariableIndex, true));
			} else {
				//this.referenceVariables.put(localVariableIndex, ValueNode.getInstance());
			}
			localVariableIndex++;
		}
	}
	
	
	public void addObjectNode(ObjectNode node) {
		this.objectNodes.add(node);
	}
	
	
	public ObjectNodeCollection getObjectsOfType(Type objectType) {

		ObjectNodeCollection objectNodes = new ObjectNodeCollection();
		
		for(ObjectNode node : this.objectNodes) 
		{
			if(node.getObjectType().getInternalName().equals(objectType.getInternalName())) {
				objectNodes.add(node);
			}
		}
		return objectNodes;
	}

	public ReferenceVariable ensureReferenceVariable(int localVariableIndex) 
	{
		String localVariableRef = "" + localVariableIndex;
		
		if(!this.referenceVariables.containsKey(localVariableIndex)) {
			this.referenceVariables.put(localVariableIndex, new ReferenceVariable(localVariableRef));
		}
		return (ReferenceVariable) this.referenceVariables.get(localVariableIndex);
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

			objectNode.prettyPrint();
		}
		for(ReferenceNode nonObjectNode : this.referenceVariables.values()) {

			nonObjectNode.prettyPrint();
		}
	}
}
