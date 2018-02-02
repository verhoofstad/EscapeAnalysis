package org.methodFinding;

import org.asm.JarClass;
import org.asm.JarFileListVisitor;
import org.tree.JavaClass;
import org.tree.JavaClassList;

public class JarFileMethodFinder extends JarFileListVisitor
{
	JavaClass classHierarchy;
	JavaClassList packagePrivateClasses;
	
	public JarFileMethodFinder(JavaClass classHierarchy, JavaClassList packagePrivateClasses) {
		
		this.classHierarchy = classHierarchy;
		this.packagePrivateClasses = packagePrivateClasses;
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
		
		JavaClass javaClass = this.classHierarchy.find(jarClass.name());
		
		if(javaClass == null) {
			System.out.format("Class %s was not found.\n", jarClass.name());
		}
		
		JarClassMethodFinder methodFinder = new JarClassMethodFinder(javaClass, this.packagePrivateClasses);
		
		jarClass.accept(methodFinder);
	}
}
