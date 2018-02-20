package org.classHierarchy;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaClassSet;
import org.classHierarchy.tree.JavaInterface;
import org.classHierarchy.tree.JavaInterfaceSet;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

public class ClassHierarchy {

	private JavaClass rootNode;
	private JavaInterfaceSet interfaces;
	
	private JavaClassSet classes = new JavaClassSet();
	
	public ClassHierarchy(JavaClass rootNode, JavaInterfaceSet interfaces) {
		
		this.rootNode = rootNode;
		this.interfaces = interfaces;
		
		addClassesToMap(rootNode);
	}
	
	private void addClassesToMap(JavaClass javaClass) {
		
		this.classes.add(javaClass);
		
		for(JavaClass subClass : javaClass.subClasses()) {
			addClassesToMap(subClass);
		}
	}
	
	public JavaClass findClass(String internalName) {
		return this.classes.find(internalName);
	}

	public JavaClass getClass(String internalName) {
		return this.classes.get(internalName);
	}
	
	public JavaInterface getInterface(String internalName) {
		return this.interfaces.get(internalName);
	}
	
	public JavaClassSet getClasses() {
		return this.classes;
	}
	
	/*
	 * Gets the public classes (exported classes for RTA).
	 */
	public JavaClassSet getPublicClasses() {
		JavaClassSet publicClasses = new JavaClassSet();
		for(JavaClass javaClass : this.classes) {
			if(javaClass.isPublic()) {
				publicClasses.add(javaClass);
			}
		}
		return publicClasses;
	}
	
	public JavaMethodSet getExportedMethods() {
		JavaMethodSet exportedMethods = new JavaMethodSet();
		for(JavaClass publicClass : this.getPublicClasses()) {

			boolean isFinal = publicClass.isFinal();
			for(JavaMethod method : publicClass.declaredMethods()) {
				
				if(method.isPublic() || (method.isProtected() && isFinal)) {
					exportedMethods.add(method);
				}
			}
		}
		return exportedMethods;
	}
	
	private JavaClassSet getDerivableClasses() {
		JavaClassSet derivableClasses = new JavaClassSet();
		for(JavaClass javaClass : this.classes) {
			if(javaClass.isPublic() && !javaClass.isFinal()) {
				derivableClasses.add(javaClass);
			}
		}
		return derivableClasses;
	}

	public JavaClassSet getFinalPackagePrivateClasses() {
		JavaClassSet finalPackagePrivateClasses = new JavaClassSet();
		for(JavaClass javaClass : this.classes) {
			if(javaClass.isFinalPackagePrivate()) {
				finalPackagePrivateClasses.add(javaClass);
			}
		}
		return finalPackagePrivateClasses;
	}
	
	public int classCount() {
		return this.classes.size();
	}
	
	public JarFileSet jarFiles() {
		Set<JarFile> jarFiles = new HashSet<JarFile>();
		getJarFiles(this.rootNode, jarFiles);
		return new JarFileSet(jarFiles);
	}
	
	private void getJarFiles(JavaClass javaClass, Set<JarFile> jarFiles) {
		
		jarFiles.add(javaClass.jarFile());
		for(JavaClass subClass : javaClass.subClasses()) {
			getJarFiles(subClass, jarFiles);
		}
	}
}