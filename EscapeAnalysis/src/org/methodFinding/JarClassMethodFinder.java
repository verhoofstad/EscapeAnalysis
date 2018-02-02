package org.methodFinding;

import java.util.ArrayList;

import org.asm.TemplateMethodVisitor;
import org.asm.jvm.AccessFlags;
import org.asm.jvm.StatementFilter2;
import org.connectionGraph.ConnectionGraphBuilder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.packagePrivateClasses.JavaClass;
import org.packagePrivateClasses.JavaClassList;
import org.packagePrivateClasses.JavaMethod;

public class JarClassMethodFinder extends ClassVisitor {

	JavaClassList _packagePrivateClasses;
	ArrayList<MethodFinder> _methodFinders = new ArrayList<MethodFinder>();
	
	public JarClassMethodFinder(JavaClassList packagePrivateClasses) {
		super(Opcodes.ASM6);
		
		_packagePrivateClasses = packagePrivateClasses;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		
		
		System.out.format(">> VISIT METHOD: %s, desc: %s\n", name, desc);

	
		//return new StatementFilter2(builder);
		return new TemplateMethodVisitor(Opcodes.ASM6);
	}
}
