package org.validation;

import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ClassValidator  extends ClassVisitor {

	private Set<String> invokedTypes;
	private Set<String> instantiatedTypes;
	
	public ClassValidator(Set<String> invokedTypes, Set<String> instantiatedTypes) {
		super(Opcodes.ASM6);
		
		this.invokedTypes = invokedTypes;
		this.instantiatedTypes = instantiatedTypes;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		return new MethodValidator(this.invokedTypes, this.instantiatedTypes);
	}
}
