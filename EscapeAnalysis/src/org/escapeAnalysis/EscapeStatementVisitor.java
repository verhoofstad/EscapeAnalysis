package org.escapeAnalysis;

import java.util.List;

import soot.RefType;

public class EscapeStatementVisitor {

	/*
	 * Visit the assignment of 'this' to a local variable.
	 */
	public void visitThis(String localName) { }
	
	/*
	 * Visit the assignment of a reference parameter to a local variable.
	 */
	public void visitParameter(String localName) { }
	
	/*
	 * Visit the declaration of a local reference variable.
	 */
	public void visitLocalDeclaration(String localName) { }
	
	/*
	 * Visit an assignment in the form of p = new T()
	 */
	public void visitNew(RefType objectName, String localName) { }
	
	/*
	 * Visit an assignment in the form of p = q
	 */
	public void visitAssignment(String localNameLeft, String localNameRight) {}

	/*
	 * Visit an assignment in the form of p = q.f (non-static)
	 */
	public void visitAssignment(String localNameLeft, String localNameRight, String fieldName) {}

	/*
	 * Visit an assignment in the form of p = Q.f (static)
	 */
	public void visitAssignment(String localNameLeft, RefType objectType, String fieldName) {}

	/*
	 * Visit a field assignment in the form of p.f = q (non-static) 
	 */
	public void visitNonStaticFieldAssignment(String localNameLeft, String fieldName, String localNameRight) {}

	/*
	 * Visit a field assignment in the form of P.f = q (static)
	 */
	public void visitStaticFieldAssignment(RefType objectType, String fieldName, String localNameRight) {}
	
	/*
	 * Visit the invocation of a method.
	 */
	public void visitMethodInvoke(List<String> localArguments) { }
	
	/*
	 * Visit the assignment of a local variable with an trivial value.
	 * This happens when a variable is assigned with the return value of a method.
	 * 
	 */
	public void visitClearLocal(String localName) { }

	/*
	 * Visit the assignment of an instance field of a local variable with an trivial value.
	 * This happens when a variable is assigned with the return value of a method.
	 * 
	 */
	public void visitClearField(String localName, String fieldName) { }
	
	/*
	 * Visit a reference return statement 
	 */
	public void visitReturn(String localName) {	}
	
	
	public void visitEnd() { }
}
