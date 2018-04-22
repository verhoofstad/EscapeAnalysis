package org.classHierarchy;

import org.asm.JarFile;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;

public class ClassHierarchy {

	private JavaType rootNode;
	private JavaTypeSet interfaces;
	private JavaTypeSet classes;
	
	public ClassHierarchy(JavaType rootNode, JavaTypeSet classes, JavaTypeSet interfaces) {
		
		this.rootNode = rootNode;
		this.classes = classes;
		this.interfaces = interfaces;
	}
	
	public JavaType rootNode() {
		return this.rootNode;
	}
	
	public JavaType findType(String internalName) 
	{
		if(this.classes.contains(internalName)) {
			return this.classes.get(internalName);
		} else if(this.interfaces.contains(internalName)) {
			return this.interfaces.get(internalName);
		} else {
			return null;
		}
	}
	
	public JavaType findClass(String internalName) {
		return this.classes.find(internalName);
	}
	
	public JavaType getClass(String internalName) {
		return this.classes.get(internalName);
	}
	
	public JavaType getType(String internalName) {
		if(this.containsType(internalName)) {
			return this.findType(internalName);
		} else {
			throw new Error("Could not find type " + internalName);
		}
	}
	
	public JavaTypeSet getClasses() {
		return this.classes;
	}
	
	public JavaTypeSet getInterfaces() {
		return this.interfaces;
	}
	
	public boolean containsType(String internalName) {
		return this.classes.contains(internalName) || this.interfaces.contains(internalName);
	}
	
	/**
	 * Gets the public classes (exported classes for RTA).
	 */
	public JavaTypeSet getPublicClasses() {
		JavaTypeSet publicClasses = new JavaTypeSet();
		for(JavaType javaClass : this.classes) {
			if(javaClass.isPublic()) {
				publicClasses.add(javaClass);
			}
		}
		return publicClasses;
	}
	
	/**
	 * Gets the exported methods for RTA.
	 * @see 
	 */
	public JavaMethodSet getExportedMethods() {
		JavaMethodSet exportedMethods = new JavaMethodSet();
		for(JavaType publicClass : this.getPublicClasses()) {

			boolean isNonFinal = !publicClass.isFinal();
			for(JavaMethod method : publicClass.declaredMethods()) {
				
				if(method.isPublic() || (method.isProtected() && isNonFinal)) {
					exportedMethods.add(method);
				}
			}
		}
		return exportedMethods;
	}
	
	
	/**
	 * Gets the exported methods for RTA which are contained in a given library (JAR-file).
	 */
	public JavaMethodSet getExportedMethods(JarFile jarFile) {
		JavaMethodSet exportedMethods = new JavaMethodSet();
		for(JavaType publicClass : this.getPublicClasses()) {

			if(publicClass.jarFile().getAbsolutePath().equals(jarFile.getAbsolutePath())) {
			
				boolean isNonFinal = !publicClass.isFinal();
				for(JavaMethod method : publicClass.declaredMethods()) {
					
					if(method.isPublic() || (method.isProtected() && isNonFinal)) {
						exportedMethods.add(method);
					}
				}
			}
		}
		return exportedMethods;
	}
	
	public JavaTypeSet getFinalPackagePrivateClasses() {
		JavaTypeSet finalPackagePrivateClasses = new JavaTypeSet();
		for(JavaType javaClass : this.classes) {
			if(javaClass.isFinalPackagePrivate()) {
				finalPackagePrivateClasses.add(javaClass);
			}
		}
		return finalPackagePrivateClasses;
	}
	
	public int classCount() {
		return this.classes.size();
	}
}