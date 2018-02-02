package org.connectionGraph;

import org.asm.jvm.ObjectInstance;
import org.objectweb.asm.Type;

public abstract class EscapeStatementVisitor {

	
	
	public void visitConcreteMethod() { }
	
	/*
	 * Visit an assignment in the form of p = new T()
	 */
	public void visitNew(Type objectName, int varIndex) { }
	
	/*
	 * Visit an assignment in the form of p = q
	 */
	public void visitAssignment(int targetIndex, int sourceIndex) {}

	/*
	 * Visit an assignment in the form of p = q.f
	 */
	public void visitAssignment(int targetIndex, int sourceIndex, String fieldName) {}

	/*
	 * Visit a field assignment in the form of p.f = q 
	 */
	public void visitNonStaticFieldAssignment(int varIndex, String fieldName, int sourceIndex) {}

	/*
	 * Visit a field assignment in the form of p.f = q 
	 */
	public void visitStaticFieldAssignment(Type objectType, String fieldName, int sourceIndex) {}
	

	/*
	 * Visit a reference return statement 
	 */
	public void visitReturn(int varIndex) {	}
	
	
	public void visitEnd() { }
}
