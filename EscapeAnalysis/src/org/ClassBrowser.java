package org;

import org.connectionGraph.ConnectionGraphBuilder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassBrowser extends ClassVisitor {

	public static int totalClasses = 0;
	
	public ClassBrowser() {
		super(Opcodes.ASM6);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

		System.out.println(name);
		totalClasses += 1;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		System.out.println(signature);
		
		//MethodVisitor  mv = super.visitMethod(access, name, desc, signature, exceptions);

		//ConnectionGraphBuilder builder = new ConnectionGraphBuilder(Opcodes.ASM6, mv);
		
		//return builder;
		return null;
	}

}
