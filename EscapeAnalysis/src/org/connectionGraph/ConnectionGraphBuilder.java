package org.connectionGraph;

import java.util.Stack;

import org.asm.ConvertOpcode;
import org.asm.jvm.AccessFlags;
import org.connectionGraph.nodes.*;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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
	private String methodDescriptor;
	private int access;
	private Boolean verbose = false;
	
	private static int objectIndex = 0;
	
	private ConnectionGraph connectionGraph; 
	private Type methodType;
	private AccessFlags methodFlags;
	
	public ConnectionGraphBuilder(int access, String methodDescriptor) {
		
		this.analysisType = AnalysisType.FlowSensitive;
		this.access = access;
		this.methodDescriptor = methodDescriptor;
	}
	
    /**
     *  Starts the visit of the method's code, if any (i.e. non abstract method).
     */
	@Override
	public void visitConcreteMethod() {
		
		// Initialize builder only if the method has code.
		this.connectionGraph = new ConnectionGraph();
		this.methodType = Type.getMethodType(this.methodDescriptor);
		this.methodFlags = new AccessFlags(this.access);
		
		this.connectionGraph.initializeParamaterNodes(this.methodType, this.methodFlags);
	}
	

	/*
	 * Visit an assignment in the form of p = new T()
	 */
	public void visitNew(Type objectType, int varIndex) { 
		
		objectIndex += 1;
		
		
		String objectid = objectType.getInternalName() + " [" + objectIndex + "]";
		
		// Create a new object node
		ObjectNode objectNode = new ObjectNode(objectid, objectType);
		
		connectionGraph.addObjectNode(objectNode);

		ReferenceVariable target = connectionGraph.ensureReferenceVariable(varIndex);
		
		target.addNode(objectNode);
		
		if(this.verbose) {
			System.out.println("visitNew");
		}
	}
	
	/*
	 * Visit an assignment in the form of p = q
	 */
	public void visitAssignment(int targetIndex, int sourceIndex) {
		
		ReferenceVariable target = connectionGraph.ensureReferenceVariable(targetIndex);
		ReferenceVariable source = connectionGraph.ensureReferenceVariable(sourceIndex);
		
		if(this.analysisType == AnalysisType.FlowSensitive) {
			connectionGraph.byPass(target);
		}

		target.addNode(source);
		if(this.verbose) {
			System.out.println("visitAssignment");
		}
	}

	/*
	 * Visit an assignment in the form of p = q.f (non-static)
	 */
	public void visitAssignment(int targetIndex, int sourceIndex, String fieldName) {

		ReferenceVariable target = connectionGraph.ensureReferenceVariable(targetIndex);
		ReferenceVariable source = connectionGraph.ensureReferenceVariable(sourceIndex);

		ObjectNodeCollection objectNodes = source.pointsTo();
		
		if(objectNodes.size() > 0) {
			for(ObjectNode objectNode : objectNodes) 
			{
				FieldNode fieldNode = objectNode.getFieldNode(fieldName);

				// Lazily add a field node if it doesn't exist yet.
				if(fieldNode == null) {
					
					fieldNode = new NonStaticField(fieldName);
					objectNode.addNode(fieldNode);
				}

				target.addNode(fieldNode);
			}
		} else {
			// Create object phantom node
			
		}
		if(this.verbose) {
			System.out.println("visitAssignment");
		}
	}

	
	/*
	 * Visit a field assignment in the form of p.f = q 
	 */
	public void visitNonStaticFieldAssignment(int varIndex, String fieldName, int sourceIndex) {

		ReferenceVariable target = this.connectionGraph.ensureReferenceVariable(varIndex);
		ReferenceVariable sourceNode = this.connectionGraph.ensureReferenceVariable(sourceIndex);
		
		ObjectNodeCollection objectNodes = target.pointsTo();
		
		if(objectNodes.size() > 0) {
			for(ObjectNode objectNode : objectNodes) 
			{
				FieldNode fieldNode = objectNode.getFieldNode(fieldName);

				// Lazily add a field node if it doesn't exist yet.
				if(fieldNode == null) {
					
					fieldNode = new NonStaticField(fieldName);
					objectNode.addNode(fieldNode);
				}

				fieldNode.addNode(sourceNode);
			}
		} else {
			// Create object phantom node
			
		}			
		if(this.verbose) {
			System.out.println("visitNonStaticFieldAssignment");
		}
	}
	
	
	/*
	 * Visit a static field assignment in the form of p.f = q
	 */
	@Override
	public void visitStaticFieldAssignment(Type objectType, String fieldName, int sourceIndex) {

		ReferenceVariable sourceNode = this.connectionGraph.ensureReferenceVariable(sourceIndex);
		
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
		if(this.verbose) {
			System.out.println("visitStaticFieldAssignment");
		}
	}
	
	@Override
	public void visitReturn(int varIndex) {

		ReferenceVariable node = this.connectionGraph.ensureReferenceVariable(varIndex);
		
		node.setEscape(true);
		
		if(this.verbose) {
			System.out.println("visitReturn");
		}
	}
	
	@Override
	public void visitEnd() {
		
		this.connectionGraph.pushEscapeState();
		
		/*
		System.out.println("Escape state");
		System.out.println("============");
		
		for(ObjectNode objectNode : this.connectionGraph.getObjects()) {

			System.out.format("%s              %s\n", objectNode.getId(), objectNode.getEscapeState() ? "ESCAPE" : "NO ESCAPE");
		}
		
		System.out.println("------------");
		*/
	}
}
