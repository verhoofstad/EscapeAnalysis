package org.soot;

import java.util.List;

import org.objectweb.asm.Type;

import soot.RefLikeType;
import soot.RefType;
import soot.jimple.ThisRef;

public abstract class EscapeStatementVisitor {


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
	 * Visit an assignment in the form of p = q.f
	 */
	public void visitAssignment(String localNameLeft, String localNameRight, String fieldName) {}

	/*
	 * Visit a field assignment in the form of p.f = q 
	 */
	public void visitNonStaticFieldAssignment(String localNameLeft, String fieldName, String localNameRight) {}

	/*
	 * Visit a field assignment in the form of p.f = q 
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
	 * Visit a reference return statement 
	 */
	public void visitReturn(String localName) {	}
	
	
	public void visitEnd() { }
}
