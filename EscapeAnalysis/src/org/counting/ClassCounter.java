package org.counting;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;

public class ClassCounter extends JarFileSetVisitor {
	
	private int publicClassCount = 0;
	private int privatePackageClassCount = 0;
	
	private int publicInterfaceCount = 0;
	private int packagePrivateInterfaceCount = 0;
	
	private int concreteMethodCount = 0;
	private int abstractMethodCount = 0;
	private int interfaceMethodCount = 0;
	
	public void visitPublicClass(JarClass jarClass) {
		this.publicClassCount++;
		
		countClassMethods(jarClass);
	}
	
	public void visitPackagePrivateClass(JarClass jarClass) {
		this.privatePackageClassCount++;
		countClassMethods(jarClass);
	}
	
	public void visitPublicEnum(JarClass jarClass) {
		this.publicClassCount++;
		countClassMethods(jarClass);
	}
	
	public void visitPackagePrivateEnum(JarClass jarClass) {
		this.privatePackageClassCount++;
		countClassMethods(jarClass);
	}
	
	public void visitPublicInterface(JarClass jarClass) {
		this.publicInterfaceCount++;
		countInterfaceMethods(jarClass);
	}
	
	public void visitPackagePrivateInterface(JarClass jarClass) {
		this.packagePrivateInterfaceCount++;
		countInterfaceMethods(jarClass);
	}
	
	public void printTotals() {
		System.out.format("Public class count:              %s\n", this.publicClassCount);
		System.out.format("Package-private class count:     %s\n", this.privatePackageClassCount);
		System.out.format("Total class count:               %s\n", this.publicClassCount + this.privatePackageClassCount);
		System.out.println();
		System.out.format("Public interface count:          %s\n", this.publicInterfaceCount);
		System.out.format("Package-private interface count: %s\n", this.packagePrivateInterfaceCount);
		System.out.format("Total interface count:           %s\n", this.publicInterfaceCount + this.packagePrivateInterfaceCount);
		System.out.println();
		System.out.format("Concrete method count:           %s\n", this.concreteMethodCount);
		System.out.format("Abstract method count:           %s\n", this.abstractMethodCount);
		System.out.format("Interface method count:          %s\n", this.interfaceMethodCount);
		System.out.format("Total method count:              %s\n", this.concreteMethodCount + this.abstractMethodCount + this.interfaceMethodCount);
	}
	
	private void countClassMethods(JarClass jarClass) {

		MethodCounter methodCounter = new MethodCounter();
		
		jarClass.accept(methodCounter);
		
		this.concreteMethodCount += methodCounter.concreteMethodCount();
		this.abstractMethodCount += methodCounter.abstractMethodCount();
	}

	private void countInterfaceMethods(JarClass jarClass) {

		MethodCounter methodCounter = new MethodCounter();
		
		jarClass.accept(methodCounter);
		
		this.interfaceMethodCount += methodCounter.concreteMethodCount();
		this.interfaceMethodCount += methodCounter.abstractMethodCount();
	}
}
