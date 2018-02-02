package org.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Represents a class, enum or interface from a JAR-file.
 */
public class JarClass {
	
	private ClassReader _reader;
	private String _name;
	private String _superName;
	private String[] _interfaces;
	private int _access;

	JarClass(String name, String superName, String[] interfaces, int access, ClassReader reader) {
		
		_name = name;
		_superName = superName;
		_interfaces = interfaces;
		_access = access;
		_reader = reader;
	}
	
	/**
	 * Gets the internal name of the class. 
	 */
	public String name() {
		return _name;
	}
	

	/**
	 * Gets the internal of name of the super class. For interfaces, the super class is Object. May be null, but only for the Object class.
	 */
	public String superName() {
		return _superName;
	}
	
	/**
	 * Gets the internal names of the class's interfaces. May be null.
	 */
	public String[] interfaces() {
		return _interfaces;
	}

	public int access() {
		return _access;
	}
	
	
	public Boolean isFinal() {
		return (_access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
	}
	
	
	public void accept(ClassVisitor visitor) {
		
		_reader.accept(visitor, 0);
	}
}
