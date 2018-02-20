package org.classHierarchy.tree;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.asm.jvm.AccessFlags;

/**
 * Represents a Java class.
 */
public final class JavaClass extends JavaType {
	
	private JavaClass superClass;
	
	public JavaClass(String internalName, AccessFlags accessFlags, JavaClass superClass, JavaInterfaceSet implementedInterfaces, JarFile jarFile) 
	{
		super(internalName, accessFlags, implementedInterfaces, jarFile);
		
		this.superClass = superClass;
	}
	
	/*
	 * Gets the fully qualified name that uniquely identifies this class.
	 */
	@Override
	public String id() {
		return this.name();
	}
	
	public JavaClass superClass() {
		return this.superClass;
	}
		
	public Boolean inheritsFrom(JavaClass javaClass) {
		return this.superClass != null && this.superClass.equals(javaClass);
	}
	
	public boolean isFinal() {
		return this.accessFlags().isFinal();
	}
	
	public boolean isFinalPackagePrivate() {
		return isPackagePrivate() && !hasPublicSubClass();
	}
	
	public boolean hasSuperClass() {
		return this.superClass != null;
	}
	
	@Override
	protected void addToConeSet(JavaType subType) {
		super.addToConeSet(subType);
		
		if(hasSuperClass()) {
			this.superClass.addToConeSet(subType);
		}
	}

	public JavaMethod getMethod(String name, String desc) {
		JavaMethod method = findMethod(name, desc);
		if(method != null) {
			return method;
		} else {
			throw new Error("Cannot find method " + name + "() in class " + this.name()
				+ " in JAR-file " + this.jarFile().getAbsolutePath());
		}
	}
	
	public JavaMethod findMethodUpwards(String name, String desc) {
		JavaMethod method = findMethod(name, desc);
		if(method != null) {
			return method;
		} else if(this.hasSuperClass()) {
			return this.superClass.findMethodUpwards(name, desc);
		} else {
			return null;
		}
	}

	public JavaMethod getMethodUpwards(String name, String desc) {
		JavaMethod method = findMethodUpwards(name, desc);
		if(method != null) {
			return method;
		} else {
			throw new Error();
		}
	}
	
	public JavaMethodSet findMethodsDownwards(String name, String desc) {
		
		JavaMethodSet methods = new JavaMethodSet();
		for(JavaClass subClass : this.subClasses()) {
			JavaMethod method = subClass.findMethod(name, desc);
			if(method != null) {
				methods.add(method);
			}
			methods.addAll(subClass.findMethodsDownwards(name, desc));
		}
		return methods;
	}

	@Override
	public String toString() {
		if(this.hasSuperClass()) {
			return this.name() + " extends " + this.superClass.name();
		} else {
			return this.name();
		}
	}
}
