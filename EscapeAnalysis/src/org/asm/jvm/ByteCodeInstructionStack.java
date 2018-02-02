package org.asm.jvm;

import java.util.Stack;

import org.asm.ConvertOpcode;

public class ByteCodeInstructionStack {

	private Stack<ByteCodeInstruction> instructions;
	
	private String instructionTrail;
	
	public ByteCodeInstructionStack() {
		this.instructions = new Stack<ByteCodeInstruction>();
		this.instructionTrail = "";
	}
	
	public String getInstructionTrail() {
		return this.instructionTrail;
	}
	
	public void clear() {
		this.instructions.clear();
		this.instructionTrail = "";
	}
	
	public void push(int opcode) {
		this.push(new ByteCodeInstruction(opcode));
	}
	
	public void push(ByteCodeInstruction instruction) {
		this.instructions.push(instruction);
		
		this.instructionTrail += ";" + instruction.getOpcode();
	}
	
	public ByteCodeInstruction pop() {
		return this.instructions.pop();
	}
	
	public Boolean endsWith(String pattern) {
		return this.instructionTrail.endsWith(pattern);
	}
	
	public Boolean matches(String pattern) 
	{
		return this.instructionTrail.equals(";" + pattern);
	}
}
