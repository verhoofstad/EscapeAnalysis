package org.asm;

import org.asm.jvm.AccessFlags;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * Represents a class, enum or interface from a JAR-file.
 */
public class JarClass {
	
	private String name;
	private String superName;
	private String[] interfaces;
	private AccessFlags accessFlags;
	private ClassReader reader;

	JarClass(String name, String superName, String[] interfaces, AccessFlags accessFlags, ClassReader reader) {
		
		this.name = name;
		this.superName = superName;
		this.interfaces = interfaces;
		this.accessFlags = accessFlags;
		this.reader = reader;
	}
		
	/**
	 * Gets the internal name of the class. 
	 */
	public String name() {
		return this.name;
	}
	
	/**
	 * Gets the internal of name of the super class. For interfaces, the super class is Object. May be null, but only for the Object class.
	 */
	public String superName() {
		return this.superName;
	}
	
	/**
	 * Gets the internal names of the class's interfaces. May be null.
	 */
	public String[] interfaces() {
		return this.interfaces;
	}

	public AccessFlags access() {
		return this.accessFlags;
	}
	
	
	public boolean isFinal() {
		return this.accessFlags.isFinal();
	}
	
	public boolean isInterface() {
		return this.accessFlags.isInterface();
	}
	
	public void accept(ClassVisitor visitor) {
		this.reader.accept(visitor, 0);
	}
}
