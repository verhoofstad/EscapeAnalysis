package org.tree;

import java.util.ArrayList;
import org.objectweb.asm.Opcodes;

/**
 * Represents a Java class.
 */
public class JavaClass {

	private String internalName;
	
	private JavaClass superClass;
	private JavaClassList subClasses;
	
	private int access;
	
	private ArrayList<JavaMethod> methods = new ArrayList<JavaMethod>();
	
	public JavaClass(String internalName, int access, JavaClass superClass) 
	{
		this.internalName = internalName;
		this.access = access;
		this.superClass = superClass;
		this.subClasses = new JavaClassList();
		this.methods = new ArrayList<JavaMethod>();
	}
	
	public void addSubClass(JavaClass subClass) {
		this.subClasses.add(subClass);
	}

	public String name() {
		return this.internalName;
	}
	
	public String sootName() {
		return this.internalName.replace('/', '.');
	}
	
	public int size() {
		
		int size = 1;
		
		for(JavaClass subClass : this.subClasses) {
			size += subClass.size();
		}
		return size;
	}
	
	public Boolean isPublic() {
		return (this.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
	}
	
	public Boolean isPackagePrivate() {
		return !isPublic();
	}
	
	public Boolean inheritsFrom(JavaClass javaClass) {
		return this.superClass != null && this.superClass.name().equals(javaClass.name());
	}
	
	public Boolean hasPublicSubClass(Boolean transitive) {
		return this.subClasses.containsPublicClass(transitive);
	}
	
	public Boolean isFinalPackagePrivate() {
		return isPackagePrivate() && !hasPublicSubClass(true);
	}
	
	public Boolean hasSuperClass() {
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
	
	public void addMethod(JavaMethod method) {
		if(!containsMethod(method)) {
			this.methods.add(method);
		}
	}
	
	public int methodCount() {
		return this.methods.size();
	}
	
	public int allMethodCount() {
		int total = methodCount();
		
		for(JavaClass subClass : this.subClasses) {
			total += subClass.allMethodCount();
		}
		return total;
	}
	
	public Boolean containsMethod(JavaMethod method) {

		for(JavaMethod javaMethod : this.methods) {
			if(method.desc().equals(javaMethod.desc())) {
				return true;
			}
		}
		return false;
	}
	
	public JavaClass find(String name) {
		if(this.internalName.equals(name)) {
			return this;
		} else {
			return this.subClasses.find(name);
		}
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
}
