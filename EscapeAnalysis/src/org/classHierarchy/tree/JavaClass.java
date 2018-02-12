package org.classHierarchy.tree;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;

/**
 * Represents a Java class.
 */
public class JavaClass extends JavaType {
	
	private JavaClass superClass;
	private JavaClassList subClasses;
	
	public JavaClass(String internalName, int access, JavaClass superClass, JavaInterfaceList implementedInterfaces, JarFile jarFile) 
	{
		super(internalName, access, implementedInterfaces, jarFile);
		
		this.superClass = superClass;
		this.subClasses = new JavaClassList();
	}
	
	public JavaClass superClass() {
		return this.superClass;
	}
	
	public JavaClassList subClasses() {
		return this.subClasses;
	}
	
	public void addSubClass(JavaClass subClass) {
		this.subClasses.add(subClass);
	}
		
	public int classCount() {
		
		int size = 1;
		
		for(JavaClass subClass : this.subClasses) {
			size += subClass.classCount();
		}
		return size;
	}
	
	public int abstractMethodCount(boolean includingSubClasses) {
		int abstractMethodCount = 0;
		for(JavaMethod method : this.declaredMethods()) {
			if(method.isAbstract()) {
				abstractMethodCount++;
			}
		}
		if(includingSubClasses) {
			for(JavaClass subClass : this.subClasses) {
				abstractMethodCount += subClass.abstractMethodCount(includingSubClasses);
			}
		}
		return abstractMethodCount;
	}
		
	public Boolean inheritsFrom(JavaClass javaClass) {
		return this.superClass != null && this.superClass.equals(javaClass);
	}
	
	public boolean hasPublicSubClass() {
		for(JavaClass subClass : this.subClasses) {
			if(subClass.isPublic() || subClass.hasPublicSubClass()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isFinalPackagePrivate() {
		return isPackagePrivate() && !hasPublicSubClass();
	}
	
	public boolean hasSuperClass() {
		return this.superClass != null;
	}
	

	
	@Override
	public String toString() {
		if(this.superClass != null) {
			return this.name() + " extends " + this.superClass.name();
		} else {
			return this.name();
		}
	}
		
	public JavaClass findClass(String name) {
		if(this.name().equals(name)) {
			return this;
		} else {
			for(JavaClass subClass : this.subClasses) {
				JavaClass javaClass = subClass.findClass(name);
				if(javaClass != null) {
					return javaClass;
				}
			}
			return null;
		}
	}
	
	public JavaMethod findMethod(String name, String desc, String signature) {
		for(JavaMethod method : this.declaredMethods()) {
			if(method.signatureEquals(name, desc, signature)) { 
				return method;
			}
		}
		return null;
	}

	public JavaClassList getClasses() {
		
		JavaClassList classes = new JavaClassList();

		classes.add(this);

		for(JavaClass subClass : this.subClasses) {
			classes.addAll(subClass.getClasses());
		}
		return classes;
	}
	
	public JavaClassList getFinalPackagePrivateClasses() {
		
		JavaClassList finalPackagePrivateClasses = new JavaClassList();

		if(isFinalPackagePrivate()) {
			finalPackagePrivateClasses.add(this);
		}
		for(JavaClass subClass : this.subClasses) {
			finalPackagePrivateClasses.addAll(subClass.getFinalPackagePrivateClasses());
		}
		return finalPackagePrivateClasses;
	}
	
	public JarFileSet jarFiles() {
		
		Set<JarFile> jarFiles = new HashSet<JarFile>();
		
		jarFiles.add(this.jarFile());
	
		for(JavaClass subClass : this.subClasses) {
			for(JarFile jarFile : subClass.jarFiles()) {
				jarFiles.add(jarFile);
			}
		}
		
		return new JarFileSet(jarFiles);
	}
}
