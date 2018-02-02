package org.methodFinding;

import java.util.ArrayList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.tree.JavaClass;
import org.tree.JavaClassList;
import org.tree.JavaMethod;

class JarClassMethodFinder extends ClassVisitor {

	JavaClass currentClass;
	JavaClassList packagePrivateClasses;
	ArrayList<MethodFinder> _methodFinders = new ArrayList<MethodFinder>();
	
	public JarClassMethodFinder(JavaClass currentClass, JavaClassList packagePrivateClasses) {
		super(Opcodes.ASM6);
		
		this.currentClass = currentClass;
		this.packagePrivateClasses = packagePrivateClasses;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		JavaMethod currentMethod = new JavaMethod(access, name, desc, signature, exceptions);
	
		return new MethodFinder(this.currentClass, currentMethod, this.packagePrivateClasses);
	}
}
