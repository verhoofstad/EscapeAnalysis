package org.soot;

import java.util.List;

import org.connectionGraph.nodes.*;

import soot.RefLikeType;
import soot.RefType;
import soot.jimple.ThisRef;

/**
 *	Performs an intra procedural analysis on a single method to create its connection graph. 
 */
public class ConnectionGraphBuilder extends EscapeStatementVisitor {

	public enum AnalysisType 
	{
		FlowSensitive,
		FlowInsensitive;
	}
	
	private AnalysisType analysisType;
	private Boolean verbose = true;
	
	private ConnectionGraph connectionGraph; 
	
	public ConnectionGraphBuilder() {
		
		this.analysisType = AnalysisType.FlowSensitive;
		this.connectionGraph = new ConnectionGraph();
	}
	
	
	/*
	 * Visit the assignment of 'this' to a local variable.
	 */
	@Override
	public void visitThis(String localName) {
		printDebug("visitThis %s", localName);
		this.connectionGraph.addThisVariable(localName);
	}
	
	/*
	 * Visit the assignment of a reference parameter to a local variable.
	 */
	@Override
	public void visitParameter(String localName) { 
		printDebug("visitParameter %s", localName);
		this.connectionGraph.addParameterVariable(localName);
	}
	
	/*
	 * (1) Visit an assignment in the form of p = new T()
	 */
	@Override
	public void visitNew(RefType objectType, String localName) { 
		printDebug("visitNew %s = new %s()", localName, objectType.getClassName());
		
		String objectid = objectType.getClassName() + " [" + localName + "]";
		
		// Create a new object node
		ObjectNode objectNode = new ObjectNode(objectid, objectType);
		
		connectionGraph.addObjectNode(objectNode);

		ReferenceVariable target = connectionGraph.ensureLocal(localName);
		
		target.addNode(objectNode);
	}
	
	/*
	 * (2) Visit an assignment in the form of p = q
	 */
	@Override
	public void visitAssignment(String localNameLeft, String localNameRight) {
		printDebug("visitAssignment %s = %s", localNameLeft, localNameRight);
		
		ReferenceVariable target = connectionGraph.ensureLocal(localNameLeft);
		ReferenceVariable source = connectionGraph.ensureLocal(localNameRight);
		
		if(this.analysisType == AnalysisType.FlowSensitive) {
			connectionGraph.byPass(target);
		}

		target.addNode(source);
	}

	
	/*
	 * (3) Visit a field assignment in the form of p.f = q  (non-static)
	 */
	@Override
	public void visitNonStaticFieldAssignment(String localNameLeft, String fieldName, String localNameRight) {

		printDebug("visitNonStaticFieldAssignment %s.%s = %s", localNameLeft, fieldName, localNameRight);

		ReferenceVariable target = this.connectionGraph.ensureLocal(localNameLeft);
		ReferenceVariable sourceNode = this.connectionGraph.ensureLocal(localNameRight);
		
		if(target.pointsToNothing()) {
			// Create object phantom node.
			PhantomObjectNode phantomObject = new PhantomObjectNode();
			
			target.addNode(phantomObject);
			this.connectionGraph.addPhantomObjectNode(phantomObject);
			printDebug("   created phantom node for %s.", localNameLeft);
		}

		ObjectNodeCollection objectNodes = target.pointsTo();

		for(ObjectNode objectNode : objectNodes) 
		{
			FieldNode fieldNode = objectNode.getFieldNode(fieldName);

			// Lazily add a field node if it doesn't exist yet.
			if(fieldNode == null) {
				
				fieldNode = new NonStaticField(fieldName);
				objectNode.addNode(fieldNode);
				printDebug("   created field node '%s' for %s.", fieldName, objectNode.getId());
			}

			fieldNode.addNode(sourceNode);
		}
	}
	
	
	/*
	 * (4) Visit an assignment in the form of p = q.f (non-static)
	 */
	@Override
	public void visitAssignment(String localNameLeft, String localNameRight, String fieldName) {

		printDebug("visitAssignment %s = %s.%s", localNameLeft, localNameRight, fieldName);

		ReferenceVariable target = connectionGraph.ensureLocal(localNameLeft);
		ReferenceVariable source = connectionGraph.ensureLocal(localNameRight);

		if(source.pointsToNothing()) {
			// Create object phantom node.
			PhantomObjectNode phantomObject = new PhantomObjectNode();
			phantomObject.setEscape(source.getEscapeState());
			source.pointsTo(phantomObject);
			this.connectionGraph.addPhantomObjectNode(phantomObject);
			printDebug("   created phantom node for %s.", localNameRight);
		}

		ObjectNodeCollection objectNodes = source.pointsTo();

		for(ObjectNode objectNode : objectNodes) 
		{
			FieldNode fieldNode = objectNode.getFieldNode(fieldName);

			// Lazily add a field node if it doesn't exist yet.
			if(fieldNode == null) {
				
				fieldNode = new NonStaticField(fieldName, objectNode.getEscapeState());
				objectNode.addNode(fieldNode);
				printDebug("   created field node '%s' for %s.", fieldName, objectNode.getId());
			}

			target.addNode(fieldNode);
			if(!target.getEscapeState()) {
				target.setEscape(fieldNode.getEscapeState());
			}
		}
	}

	/*
	 * Visit a static field assignment in the form of p.f = q
	 */
	@Override
	public void visitStaticFieldAssignment(RefType objectType, String fieldName, String localNameRight) {
		
		printDebug("visitStaticFieldAssignment %s.%s = %s", objectType.getClassName(), fieldName, localNameRight);

		ReferenceVariable sourceNode = this.connectionGraph.ensureLocal(localNameRight);
		
		ObjectNodeCollection objectNodes = this.connectionGraph.getObjectsOfType(objectType);
		
		if(objectNodes.size() > 0) {
			for(ObjectNode objectNode : objectNodes) 
			{
				FieldNode fieldNode = objectNode.getFieldNode(fieldName);

				// Lazily add a field node if it doesn't exist yet.
				if(fieldNode == null) {
					
					fieldNode = new StaticField(fieldName);
					objectNode.addNode(fieldNode);
				}

				fieldNode.addNode(sourceNode);
			}
		} else {
			// Create object phantom node
		}
	}
	
	@Override
	public void visitReturn(String localName) {

		printDebug("visitReturn %s", localName);
		
		ReferenceVariable node = this.connectionGraph.ensureLocal(localName);
		
		node.setEscape(true);
	}
	
	/*
	 * Visit the invocation of a method.
	 */
	public void visitMethodInvoke(List<String> localArguments) 
	{
		printDebug("visitMethodInvoke");
		for(String localName : localArguments) {
			ReferenceVariable node = this.connectionGraph.ensureLocal(localName);
			
			node.setEscape(true);
		}
	}

	
	@Override
	public void visitEnd() {
		
		this.connectionGraph.pushEscapeState();
		
		System.out.println("Escape state");
		System.out.println("------------");
		
		for(ObjectNode objectNode : this.connectionGraph.getObjects()) {

			System.out.format("%s              %s\n", objectNode.getId(), objectNode.getEscapeState() ? "ESCAPE" : "NO ESCAPE");
		}
		
		System.out.println("Edges");
		System.out.println("-----");
		this.connectionGraph.printEdges();
		
		System.out.println("========================================================");
	}
	
	private void printDebug(String format, Object... args) {
		if(this.verbose) {
			System.out.format("   " + format, args);
			System.out.println();
		}
	}
}
