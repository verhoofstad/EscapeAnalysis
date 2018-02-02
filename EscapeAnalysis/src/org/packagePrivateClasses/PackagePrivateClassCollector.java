package org.packagePrivateClasses;

import org.asm.JarClass;
import org.asm.JarVisitor;

/*
 * Finds all package-private classes.
 */
public class PackagePrivateClassCollector extends JarVisitor {

	private JavaClassList _packagePrivateClasses = new JavaClassList();
	
	public JavaClassList getClassList() {
		return _packagePrivateClasses;
	}
	
	@Override
	public void visitPackagePrivateClass(JarClass jarClass) {
		_packagePrivateClasses.add(new JavaClass(jarClass.name(), jarClass.superName()));
	}

	@Override
	public void visitPackagePrivateEnum(JarClass jarClass) {
		_packagePrivateClasses.add(new JavaClass(jarClass.name(), jarClass.superName()));
	}
}
