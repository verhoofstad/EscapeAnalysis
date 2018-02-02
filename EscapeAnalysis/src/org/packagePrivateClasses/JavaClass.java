package org.packagePrivateClasses;

import java.util.ArrayList;

/**
 * Represents a Java class.
 */
public class JavaClass {

	private String _internalName;
	private String _superName;
	
	// The methods/constructors in which an object of this class is instantiated.
	private ArrayList<JavaMethod> _instaniatedIn = new ArrayList<JavaMethod>();
	
	public JavaClass(String internalName, String superName) 
	{
		_internalName = internalName;
		_superName = superName;
	}
	
	public String name() {
		return _internalName;
	}
	
	@Override
	public String toString() {
		
		return _internalName + " extends " + _superName; 
	}
	
	public void addMethod(JavaMethod method) {
		_instaniatedIn.add(method);
	}
	
	public int methodCount() {
		return _instaniatedIn.size();
	}
}
