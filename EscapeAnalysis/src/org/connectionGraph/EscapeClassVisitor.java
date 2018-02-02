package org.connectionGraph;


import java.io.File;
import java.util.Map;

import org.asm.JarClass;
import org.asm.jvm.StatementFilter2;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EscapeClassVisitor extends ClassVisitor {
	
	private File jarFile;
	private JarClass currentClass;
	private Map<String, Integer> patternCount;
	
	public EscapeClassVisitor(File jarFile, JarClass currentClass, Map<String, Integer> patternCount) 
	{
		super(Opcodes.ASM6);
		
		this.jarFile = jarFile;
		this.currentClass = currentClass;
		this.patternCount = patternCount;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		ConnectionGraphBuilder builder = new ConnectionGraphBuilder(access, desc);
		
		return new StatementFilter2(builder, this.jarFile, this.currentClass, name, this.patternCount);
	}
}
