package org.classHierarchy;

import org.classHierarchy.tree.JavaMethod;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodLoader extends ClassVisitor {

	private JavaTempClass currentType;
	
	MethodLoader(JavaTempClass currentType){
		super(Opcodes.ASM6);
		
		this.currentType = currentType;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		this.currentType.addMethod(new JavaTempMethod(access, name, desc, signature));
		return null;
	}
	
}
