package org.methodFinding;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassList;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodList;

/*
 * Finds the methods in which one of the provided classes is instantiated.
 */
public class JarFileSetMethodFinder extends JarFileSetVisitor
{
	private JavaClass classHierarchy;
	private JavaClassList classes;
	private JavaMethodList foundMethods = new JavaMethodList();
	
	public JarFileSetMethodFinder(JavaClass classHierarchy, JavaClassList classes) {
		
		this.classHierarchy = classHierarchy;
		this.classes = classes;
	}
	
	public JavaMethodList foundMethods() {
		return this.foundMethods;
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
		
		JavaClass javaClass = this.classHierarchy.findClass(jarClass.name());
		
		if(javaClass != null) {
			JarClassMethodFinder methodFinder = new JarClassMethodFinder(javaClass, this.classes, this.foundMethods);
			
			jarClass.accept(methodFinder);
		} else {			
			System.out.format("Class %s was not found.\n", jarClass.name());
		}
	}
}
