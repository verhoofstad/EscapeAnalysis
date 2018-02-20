package org.methodFinding;

import java.util.List;

import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassSet;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class JarClassMethodFinder extends ClassVisitor {

	private JavaClass currentClass;
	private JavaClassSet classes;
	private JavaMethodSet foundMethods;
		
	public JarClassMethodFinder(JavaClass currentClass, JavaClassSet classes, JavaMethodSet foundMethods) {
		super(Opcodes.ASM6);
		
		this.currentClass = currentClass;
		this.classes = classes;
		this.foundMethods = foundMethods;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		JavaMethod currentMethod = this.currentClass.findMethod(name, desc);
		
		if(currentMethod != null) {
			return new MethodFinder(currentMethod, this.classes, this.foundMethods);
		} else {
			System.out.println("Method not found!!");
			return null;
		}
	}
}
