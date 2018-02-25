package org.classHierarchy.tree;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;

public class JavaInterface extends JavaType {
	
	private JavaTypeSet subInterfaces;

	public JavaInterface(String internalName, AccessFlags accessFlags, JavaTypeSet superInterfaces, JarFile jarFile) {
		super(internalName, accessFlags, superInterfaces, jarFile);
		
		this.subInterfaces = new JavaTypeSet();
	}
	
	public JavaTypeSet subInterfaces() {
		return this.subInterfaces;
	}
	
	public void addSubInterface(JavaInterface subInterface) {
		this.subInterfaces.add(subInterface);
		
		this.addToConeSet(subInterface);
	}
}
