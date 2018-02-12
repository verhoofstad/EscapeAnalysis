package org.classHierarchy.tree;

import org.asm.JarFile;

public class JavaInterface extends JavaType {

	public JavaInterface(String internalName, int access, JavaInterfaceList superInterfaces, JarFile jarFile) {
		super(internalName, access, superInterfaces, jarFile);
	}
}
