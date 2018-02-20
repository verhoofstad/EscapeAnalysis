package org.classHierarchy.tree;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;

/*
 * Represents a Java class or interface.
 */
public abstract class JavaType {

	private String internalName;
	private JarFile jarFile;
	
	private JavaClassSet subClasses;
	private JavaInterfaceSet superInterfaces;
	
	private JavaTypeSet coneSet;
	
	private AccessFlags accessFlags;
	
	private JavaMethodSet declaredMethods;
	
	protected JavaType(String internalName, AccessFlags accessFlags, JavaInterfaceSet superInterfaces, JarFile jarFile) 
	{
		this.internalName = internalName;
		this.accessFlags = accessFlags;
		this.superInterfaces = superInterfaces;
		this.jarFile = jarFile;
		
		this.subClasses = new JavaClassSet();
		this.declaredMethods = new JavaMethodSet();
		this.coneSet = new JavaTypeSet(this);
	}
	
	/*
	 * Gets the fully qualified name that uniquely identifies this type.
	 */
	public String id() {
		return this.internalName;
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
	
	protected AccessFlags accessFlags() {
		return this.accessFlags;
	}
	
	/*
	 * Gets the JAR-file this type was loaded from.
	 */
	public JarFile jarFile() {
		return this.jarFile;
	}
	
	public JavaInterfaceSet superInterfaces() {
		return this.superInterfaces;
	}
	
	/*
	 * Returns the sub classes of the current type. If the current type represents an interface,
	 * it returns the classes that implement it.
	 */
	public JavaClassSet subClasses() {
		return this.subClasses;
	}
	
	/*
	 * Returns the cone set of the current type.
	 * If the current type is a class, it returns the current instance and all direct and indirect sub classes.
	 * If the current type is an interface, it returns the current instance, all direct and indirect sub interfaces
	 * and all classes implementing these interfaces.
	 */
	public JavaTypeSet coneSet() {
		return this.coneSet;
	}
	
	public JavaMethodSet declaredMethods() {
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
	
	public void addSubClass(JavaClass subClass) {
		this.subClasses.add(subClass);
		addToConeSet(subClass);
	}
	
	protected void addToConeSet(JavaType subType) {

		if(!this.coneSet.contains(subType.id())) {
			this.coneSet.add(subType);
		}
		for(JavaInterface superInterface : this.superInterfaces) {
			superInterface.addToConeSet(subType);
		}
	}
	
	public boolean hasPublicSubClass() {
		for(JavaClass subClass : this.subClasses) {
			if(subClass.isPublic() || subClass.hasPublicSubClass()) {
				return true;
			}
		}
		return false;
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
	
	public JavaMethod findMethod(String name, String desc) {
		for(JavaMethod method : this.declaredMethods) {
			if(method.signatureEquals(name, desc)) { 
				return method;
			}
		}
		return null;
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