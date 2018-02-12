package org.classHierarchy;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarClass;
import org.asm.JarFile;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaInterface;
import org.classHierarchy.tree.JavaInterfaceList;

/*
 * Represents a Java class.
 */
class JavaTempClass {

	private JarClass jarClass;
	private JarFile jarFile;
	private List<JavaTempMethod> methods;
	
	public JavaTempClass(JarClass jarClass, JarFile jarFile) 
	{
		this.jarClass = jarClass;
		this.jarFile = jarFile;
		this.methods = new ArrayList<JavaTempMethod>();
	}

	public String name() {
		return this.jarClass.name();
	}
	
	public String superClass() {
		return this.jarClass.superName();
	}
		
	public String[] superInterfaces() {
		return this.jarClass.interfaces();
	}
	
	public JarFile jarFile() {
		return this.jarFile;
	}

	public boolean isSubClassOf(JavaTempClass tempClass) {
		return tempClass.name().equals(this.superClass());
	}
	
	public boolean isSubInterfaceOf(JavaTempClass tempInterface) {
		for(String superInterface : this.jarClass.interfaces()) {
			if(tempInterface.name().equals(superInterface)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSuperInterfaces() {
		return this.jarClass.interfaces().length > 0;
	}
	
	public boolean hasSuperInterface(String name) {
		for(String superInterface : this.jarClass.interfaces()) {
			if(superInterface.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void addMethod(JavaTempMethod method) {
		this.methods.add(method);
	}
	
	public JavaClass resolveToJavaClass(JavaClass superClass, JavaInterfaceList superInterfaces) {
		JavaClass javaClass =new JavaClass(this.jarClass.name(), this.jarClass.access(), superClass, superInterfaces, this.jarFile);
		for(JavaTempMethod tempMethod : this.methods) {
			javaClass.addMethod(tempMethod.resolveToJavaMethod(javaClass));
		}
		return javaClass; 
	}
	
	public JavaInterface resolveToJavaInterface(JavaInterfaceList superInterfaces) {
		JavaInterface javaInterface = new JavaInterface(this.jarClass.name(), this.jarClass.access(), superInterfaces, this.jarFile);
		for(JavaTempMethod tempMethod : this.methods) {
			javaInterface.addMethod(tempMethod.resolveToJavaMethod(javaInterface));
		}
		return javaInterface;
	}
	
	@Override
	public String toString() {
		return this.jarClass.name();
	}
}
