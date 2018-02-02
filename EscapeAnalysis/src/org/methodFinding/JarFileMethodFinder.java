package org.methodFinding;

import org.asm.JarClass;
import org.asm.JarVisitor;
import org.packagePrivateClasses.JavaClassList;

public class JarFileMethodFinder extends JarVisitor
{
	JavaClassList _packagePrivateClasses;
	
	public JarFileMethodFinder(JavaClassList packagePrivateClasses) {
		
		_packagePrivateClasses = packagePrivateClasses;
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
	
	public void visitPublicInterface(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	public void visitPackagePrivateInterface(JarClass jarClass) {
		visitClass(jarClass);
	}
	
	private void visitClass(JarClass jarClass) {
		
		System.out.println(">> VISIT CLASS: " + jarClass.name());
		
		JarClassMethodFinder methodFinder = new JarClassMethodFinder(_packagePrivateClasses);
		
		jarClass.accept(methodFinder);
		
	}
}
