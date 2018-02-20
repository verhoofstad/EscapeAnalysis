package org.methodFinding;

import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassSet;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodFinder extends MethodVisitor {

	private JavaMethod currentMethod;
	private JavaClassSet classes;
	private JavaMethodSet foundMethods;
		
	public MethodFinder(JavaMethod currentMethod, JavaClassSet classes, JavaMethodSet foundMethods) {
		super(Opcodes.ASM6);
		
		this.currentMethod = currentMethod;
		this.classes = classes;
		this.foundMethods = foundMethods;
	}
		
    /**
     *  Visits a type instruction. A type instruction is an instruction that takes the internal name of a class as parameter.
     */
	@Override
	public void visitTypeInsn(int opcode, String type) {

		switch(opcode) {
			case Opcodes.NEW:
		
				JavaClass javaClass = classes.find(type);
				
				if(javaClass != null) {
					this.foundMethods.add(this.currentMethod);
				}
				
				break;
			case Opcodes.ANEWARRAY:
				//System.out.format("new %s[]\n", type);
				break;
		}
		
		super.visitTypeInsn(opcode, type);
	}
}
