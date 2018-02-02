package org.asm.jvm;

import java.util.ArrayList;
import java.util.Stack;

import org.asm.jvm.values.*;
import org.objectweb.asm.Type;

/*
 * Represents a frame used to store data and partial results, as well as to perform dynamic linking, return values for methods, and dispatch exceptions. 
 * 
 * Each frame has its own array of local variables (§2.6.1), its own operand stack (§2.6.2), and a reference to the run-time constant pool (§2.5.5) of the class of the current method. 
 * 
 * See also: https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.6
 * 
 * Note: This is a simplification 
 * Since we are only tracking the lifetime of objects we are only interested in assignments of object references 
 * 
 */
public class Frame {

	private ArrayList<Value> localVariables;
	
	private Stack<Value> operandStack;
	
	
	public Frame() {
		this.localVariables = new ArrayList<Value>();
		this.operandStack = new Stack<Value>();
	}
	
	
	public void initializeLocalVariables(ArrayList<Value> parameters) 
	{
		for(Value value : parameters) {
			this.localVariables.add(value);
		}
	}
	
	
	public void pushValueOntoOperandStack(Value value) {
		this.operandStack.push(value);
	}
	
	
	public void pushLocalVariable(int index) {
		this.operandStack.push(this.localVariables.get(index));
	}
	
	
	public Value popFromStack() 
	{
		return this.operandStack.pop();
	}
}
