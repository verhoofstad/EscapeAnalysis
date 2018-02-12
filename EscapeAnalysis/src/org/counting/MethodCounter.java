package org.counting;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCounter extends ClassVisitor {
	
	private int concreteMethodCount = 0;
	private int abstractMethodCount = 0;

	public MethodCounter() {
		super(Opcodes.ASM6);
	}
	
	public int concreteMethodCount() {
		return this.concreteMethodCount;
	}
	
	public int abstractMethodCount() {
		return this.abstractMethodCount;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		if((access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
			this.abstractMethodCount++;
		} else {
			this.concreteMethodCount++;
		}
		return null;
	}
}
