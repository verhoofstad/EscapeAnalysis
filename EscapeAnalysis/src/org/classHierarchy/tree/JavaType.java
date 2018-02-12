package org.classHierarchy.tree;

import java.util.ArrayList;
import java.util.List;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;

/*
 * Represents a Java class or interface.
 */
public abstract class JavaType {

	private String internalName;
	private JarFile jarFile;
	
	private JavaInterfaceList superInterfaces;
	private JavaInterfaceList subInterfaces;
	
	private AccessFlags accessFlags;
	
	private JavaMethodList declaredMethods;
	
	protected JavaType(String internalName, int access, JavaInterfaceList superInterfaces, JarFile jarFile) 
	{
		this.internalName = internalName;
		this.accessFlags = new AccessFlags(access);
		this.superInterfaces = superInterfaces;
		this.jarFile = jarFile;
		
		this.subInterfaces = new JavaInterfaceList();
		this.declaredMethods = new JavaMethodList();
	}
	
	public void addSubInterface(JavaInterface subInterface) {
		this.subInterfaces.add(subInterface);
	}

	public String name() {
		return this.internalName;
	}
	
	public boolean isPublic() {
		return this.accessFlags.isPublic();
	}
	
	public boolean isPackagePrivate() {
		return !isPublic();
	}
	
	public boolean isAbstract() {
		return this.accessFlags.isAbstract();
	}
	
	/*
	 * Gets the JAR-file this type was loaded from.
	 */
	public JarFile jarFile() {
		return this.jarFile;
	}
	
	public JavaMethodList declaredMethods() {
		return this.declaredMethods;
	}
	
	
	public String sootName() {
		return this.internalName.replace('/', '.');
	}
	
	@Override
	public int hashCode() {
		return this.internalName.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null
			&& obj instanceof JavaType
			&& this.internalName.equals(((JavaType)obj).name());
	}
	
	public void addMethod(JavaMethod method) {
		if(!containsMethod(method)) {
			this.declaredMethods.add(method);
		} else {
			System.out.println("Multiple method loading.");
		}
	}
	
	public int methodCount() {
		return this.declaredMethods.size();
	}
	
	public boolean containsMethod(JavaMethod method) {

		for(JavaMethod javaMethod : this.declaredMethods) {
			if(method.signatureEquals(javaMethod)) {
				return true;
			}
		}
		return false;
	}
}