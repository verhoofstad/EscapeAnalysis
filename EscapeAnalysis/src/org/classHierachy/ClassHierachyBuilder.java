package org.classHierachy;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarClass;
import org.asm.JarFileListVisitor;
import org.tree.JavaClass;
import org.tree.JavaClassList;

/*
 * Builds a class hierarchy (classes and enums only).
 */
public class ClassHierachyBuilder extends JarFileListVisitor  {
	
	private String javaObjectName = "java/lang/Object";
	
	private List<JavaTempClass> tempClasses = new ArrayList<JavaTempClass>();
	
	private JavaClassList classList = new JavaClassList();
	
	public JavaClassList getClassList() {
		return this.classList;
	}
	
	@Override 
	public void visitPublicClass(JarClass jarClass) {
		processJarClass(jarClass);
	}
	
	@Override 
	public void visitPublicEnum(JarClass jarClass) {
		processJarClass(jarClass);
	}
	
	@Override
	public void visitPackagePrivateClass(JarClass jarClass) {
		processJarClass(jarClass);
	}

	@Override
	public void visitPackagePrivateEnum(JarClass jarClass) {
		processJarClass(jarClass);
	}
	
	@Override
	public void visitEnd() {
		buildClassHierarchy();
		this.tempClasses = null;
	}
	
	
	private void processJarClass(JarClass jarClass) {
		
		this.tempClasses.add(new JavaTempClass(jarClass.name(), jarClass.superName(), jarClass.access()));
	}
	
	private void buildClassHierarchy() {
		
		System.out.format("%s temp classes found.\n", this.tempClasses.size());
		
		for(JavaTempClass tempClass : this.tempClasses) {
		
			JavaClass javaClass = resolveClassInheritence(tempClass);
			
			this.classList.add(javaClass);
		}
	}

	private JavaClass resolveClassInheritence(JavaTempClass tempClass) {
		
		JavaClass javaClass = this.classList.find(tempClass.name());
		
		if(javaClass == null) {
			
			if(tempClass.name().equals(javaObjectName)) {
				return new JavaClass(tempClass.name(),tempClass.access(), null);
			} else {
				
				JavaTempClass superTempClass = findTempClass(tempClass.superClass());
				
				if(superTempClass == null) {
					System.out.format("Cant find class %s\n", tempClass.superClass());
				}
				
				return new JavaClass(tempClass.name(),tempClass.access(), resolveClassInheritence(superTempClass));
			}
		} else {
			return javaClass;
		}
	}
	
	private JavaTempClass findTempClass(String name) {
		
		for(JavaTempClass tempClass : this.tempClasses) {
			if(tempClass.name().equals(name)) {
				return tempClass;
			}
		}
		return null;
	}
}
