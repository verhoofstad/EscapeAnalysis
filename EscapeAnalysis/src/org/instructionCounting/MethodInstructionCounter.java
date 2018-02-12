package org.instructionCounting;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodInstructionCounter extends MethodVisitor {
	
	private Map<Integer, Integer> opcodeCount = new HashMap<Integer, Integer>();
	
	public MethodInstructionCounter(Map<Integer, Integer> opcodeCount) {
		super(Opcodes.ASM6);

		this.opcodeCount = opcodeCount;
	}
	
    /**
     *  Visits a field instruction. A field instruction is an instruction that loads or stores the value of a field of an object.
     */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		incOpcode(opcode);
	}

    /**
     *  Visits an IINC instruction.
     */
	@Override
	public void visitIincInsn(int var, int increment) {
		incOpcode(Opcodes.IINC);
	}

    /**
     *  Visits a zero operand instruction.
     */
	@Override
	public void visitInsn(int opcode) {
		incOpcode(opcode);
	}

    /**
     *  Visits an instruction with a single int operand.
     */
	@Override
	public void visitIntInsn(int opcode, int operand) {
		incOpcode(opcode);

	}

    /**
     *  Visits an invokedynamic instruction.
     */
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		incOpcode(Opcodes.INVOKEDYNAMIC);
	}

    /**
     *  Visits a jump instruction. A jump instruction is an instruction that may jump to another instruction.
     */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		incOpcode(opcode);
	}

    /**
     *  Visits a LDC instruction. Note that new constant types may be added in future versions of the Java Virtual Machine.
     *  To easily detect new constant types, implementations of this method should check for unexpected constant types, like this: 
     */
	@Override
	public void visitLdcInsn(Object cst) {
		incOpcode(Opcodes.LDC);
	}

    /**
     *  Visits a LOOKUPSWITCH instruction
     */
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		incOpcode(Opcodes.LOOKUPSWITCH);
	}


    /**
     *  Visits a method instruction. A method instruction is an instruction that invokes a method.
     */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

		incOpcode(opcode);
	}

    /**
     *  Visits a MULTIANEWARRAY instruction.
     */
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {

		incOpcode(Opcodes.MULTIANEWARRAY);
	}


    /**
     *  Visits a type instruction. A type instruction is an instruction that takes the internal name of a class as parameter.
     */
	@Override
	public void visitTypeInsn(int opcode, String type) {

		incOpcode(opcode);
	}

    /**
     *  Visits a local variable instruction. A local variable instruction is an instruction that loads or stores the value of a local variable.
     */
	@Override
	public void visitVarInsn(int opcode, int var) {
		
		incOpcode(opcode);
	}
	
	private void incOpcode(int opcode) {
		if(!this.opcodeCount.containsKey(opcode)) {
			this.opcodeCount.put(opcode, 1);
		} else {
			int currentCount = this.opcodeCount.get(opcode);
			currentCount += 1;
			this.opcodeCount.put(opcode, currentCount);
		}
	}
}
