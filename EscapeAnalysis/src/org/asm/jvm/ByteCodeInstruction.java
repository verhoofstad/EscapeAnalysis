package org.asm.jvm;

import org.asm.ConvertOpcode;
import org.objectweb.asm.Type;

public class ByteCodeInstruction {

	private int opcode;

	// The name of a field
	private String name;
	
	// The index of a local variable if this instruction is a local variabel instruction
	private int varIndex;
	
	private Type type;
	
	public ByteCodeInstruction(int opcode) {
		this.opcode = opcode;
	}

	public ByteCodeInstruction(int opcode, String name) {
		this.opcode = opcode;
		this.name = name;
	}
	
	public ByteCodeInstruction(int opcode, int varIndex) {
		this.opcode = opcode;
		this.varIndex = varIndex;
	}

	public ByteCodeInstruction(int opcode, Type type) {
		this.opcode = opcode;
		this.type = type;
	}

	public String getOpcode() {
		return ConvertOpcode.toString(this.opcode);
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getVarIndex() {
		return this.varIndex;
	}
	
	public Type getType() {
		return this.type;
	}
}
