package org.methodFinding;

import java.util.List;

import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassList;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodList;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class JarClassMethodFinder extends ClassVisitor {

	private JavaClass currentClass;
	private JavaClassList classes;
	private JavaMethodList foundMethods;
		
	public JarClassMethodFinder(JavaClass currentClass, JavaClassList classes, JavaMethodList foundMethods) {
		super(Opcodes.ASM6);
		
		this.currentClass = currentClass;
		this.classes = classes;
		this.foundMethods = foundMethods;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		JavaMethod currentMethod = this.currentClass.findMethod(name, desc, signature);
		
		if(currentMethod != null) {
			return new MethodFinder(currentMethod, this.classes, this.foundMethods);
		} else {
			System.out.println("Method not found!!");
			return null;
		}
	}
}
