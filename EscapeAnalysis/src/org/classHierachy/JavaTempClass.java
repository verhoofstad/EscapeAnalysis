package org.classHierachy;

/*
 * Represents a Java class.
 */
class JavaTempClass {

	private String internalName;
	private String superClass;
	private int access;
	
	public JavaTempClass(String internalName, String superClass, int access) 
	{
		this.internalName = internalName;
		this.superClass = superClass;
		this.access = access;
	}

	public String name() {
		return this.internalName;
	}
	
	public String superClass() {
		return this.superClass;
	}

	public int access() {
		return this.access;
	}
}
