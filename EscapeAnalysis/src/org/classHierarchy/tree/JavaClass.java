package org.classHierarchy.tree;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;

/**
 * Represents a Java class.
 */
public final class JavaClass extends JavaType {
	
	private JavaClass superClass;
	
	public JavaClass(String internalName, AccessFlags accessFlags, JavaClass superClass, JavaTypeSet implementedInterfaces, JarFile jarFile) 
	{
		super(internalName, accessFlags, implementedInterfaces, jarFile);
		
		this.superClass = superClass;
	}
	
	public JavaClass superClass() {
		return this.superClass;
	}
		
	public Boolean inheritsFrom(JavaClass javaClass) {
		return this.superClass != null && this.superClass.equals(javaClass);
	}
	

	public boolean hasSuperClass() {
		return this.superClass != null;
	}
	
	@Override
	protected void addToConeSet(JavaType subType) {
		super.addToConeSet(subType);
		
		if(hasSuperClass()) {
			this.superClass.addToConeSet(subType);
		}
	}

	@Override
	protected void setOverride(JavaMethod overridingMethod) {
		super.setOverride(overridingMethod);
		
		if(this.hasSuperClass()) {

			JavaMethod baseMethod = this.superClass.findMethod(overridingMethod.name(), overridingMethod.desc());
			
			if(baseMethod != null) {
				baseMethod.overridenBy(overridingMethod);
			} else {
				this.superClass.setOverride(overridingMethod);
			}
		}
	}

	
	public JavaMethod findMethodUpwards(String name, String desc) {
		JavaMethod method = findMethod(name, desc);
		if(method != null) {
			return method;
		} else if(this.hasSuperClass()) {
			return this.superClass.findMethodUpwards(name, desc);
		} else {
			return null;
		}
	}

	public JavaMethod getMethodUpwards(String name, String desc) {
		JavaMethod method = findMethodUpwards(name, desc);
		if(method != null) {
			return method;
		} else {
			throw new Error();
		}
	}

	@Override
	public JavaMethod findStaticMethod(String name, String desc) {
		
		if(this.hasSuperClass()) {
			JavaMethod staticMethod = this.superClass.findStaticMethod(name, desc);
			if(staticMethod != null) {
				return staticMethod;
			}
		}
		
		return super.findStaticMethod(name, desc);
	}

	@Override
	public String toString() {
		if(this.hasSuperClass()) {
			return this.name() + " extends " + this.superClass.name();
		} else {
			return this.name();
		}
	}
}
