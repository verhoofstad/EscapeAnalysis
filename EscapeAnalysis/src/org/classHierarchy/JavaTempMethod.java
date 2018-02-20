package org.classHierarchy;

import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaType;

class JavaTempMethod {

	private int access;
	private String name;
	private String desc;
	private String signature;
	
	JavaTempMethod(int access, String name, String desc, String signature) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
	}
	
	public JavaMethod resolveToJavaMethod(JavaType containedIn) {
		return new JavaMethod(containedIn, this.access, this.name, this.desc, this.signature);
	}
}
