package org.connectionGraph;

import java.io.File;
import java.util.Map;

import org.asm.JarClass;
import org.asm.JarFileVisitor;

public class EscapeJarVisitor extends JarFileVisitor 
{
	private File jarFile;
	private Map<String, Integer> patternCount;
	
	public EscapeJarVisitor(File jarFile, Map<String, Integer> patternCount) {
		this.jarFile = jarFile;
		this.patternCount = patternCount;
	}
	
	public void visitPublicClass(JarClass jarClass)	{
		visitClass(jarClass);
	}
	
	public void visitPackagePrivateClass(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPublicEnum(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPackagePrivateEnum(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	private void visitClass(JarClass jarClass) {
		
		EscapeClassVisitor classVisitor = new EscapeClassVisitor(this.jarFile, jarClass, this.patternCount);
		
		jarClass.accept(classVisitor);
	}
}
