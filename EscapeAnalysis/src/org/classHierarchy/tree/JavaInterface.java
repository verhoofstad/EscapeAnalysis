package org.classHierarchy.tree;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;

public class JavaInterface extends JavaType {
	
	private JavaInterfaceSet subInterfaces;

	public JavaInterface(String internalName, AccessFlags accessFlags, JavaInterfaceSet superInterfaces, JarFile jarFile) {
		super(internalName, accessFlags, superInterfaces, jarFile);
		
		this.subInterfaces = new JavaInterfaceSet();
	}
	
	public JavaInterfaceSet subInterfaces() {
		return this.subInterfaces;
	}
	
	public void addSubInterface(JavaInterface subInterface) {
		this.subInterfaces.add(subInterface);
		
		this.addToConeSet(subInterface);
	}
	
	/*
	public JavaMethod findNonAbstractMethodUpwards(String name, String desc) {
		JavaMethod method = findMethod(name, desc);
		if(method != null && !method.isAbstract()) {
			return method;
		} else {
			for(JavaInterface superInterface : this.superInterfaces()) {
				
				method = superInterface.findNonAbstractMethodUpwards(name, desc);
				
				if(method != null) {
					return method;
				}
			}
			return null;
		}
	}*/
}
