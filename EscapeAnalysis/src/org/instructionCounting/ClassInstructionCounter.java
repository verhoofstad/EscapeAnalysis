package org.instructionCounting;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassInstructionCounter extends ClassVisitor {

	private Map<Integer, Integer> opcodeCount = new HashMap<Integer, Integer>();
	
	public ClassInstructionCounter(Map<Integer, Integer> opcodeCount) {
		super(Opcodes.ASM6);
		
		this.opcodeCount = opcodeCount;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		return new MethodInstructionCounter(this.opcodeCount);
	}
}
