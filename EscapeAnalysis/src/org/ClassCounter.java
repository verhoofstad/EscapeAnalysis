package org;

import org.asm.JarClass;
import org.asm.JarVisitor;

public class ClassCounter extends JarVisitor {

	private int _publicClassCount = 0;
	private int _packagePrivateClassCount = 0;
	private int _publicEnumCount = 0;
	private int _packagePrivateEnumCount = 0;
	private int _publicInterfaceCount = 0;
	private int _packagePrivateInterfaceCount = 0;
	
	public int getPublicClassCount() {
		return _publicClassCount;
	}

	public int getPackagePrivateClassCount() {
		return _packagePrivateClassCount;
	}

	public int getPublicEnumCount() {
		return _publicEnumCount;
	}

	public int getPackagePrivateEnumCount() {
		return _packagePrivateEnumCount;
	}

	public int getPublicInterfaceCount() {
		return _publicInterfaceCount;
	}

	public int getPackagePrivateInterfaceCount() {
		return _packagePrivateInterfaceCount;
	}
	
	public void printStatistics() {
		
		System.out.format("Public class count              : %s\n", _publicClassCount);
		System.out.format("Package-private class count     : %s\n", _packagePrivateClassCount);
		System.out.format("Public enum count               : %s\n", _publicEnumCount);
		System.out.format("Package-private enum count      : %s\n", _packagePrivateEnumCount);
		System.out.format("Public interface count          : %s\n", _publicInterfaceCount);
		System.out.format("Package-private interface count : %s\n", _packagePrivateInterfaceCount);
		
	}

	@Override
	public void visitPublicClass(JarClass jarClass) {
		_publicClassCount++;

		ClassBrowser browser = new ClassBrowser();
		
		jarClass.accept(browser);
	}
	
	@Override
	public void visitPackagePrivateClass(JarClass jarClass) {
		_packagePrivateClassCount++;
	}

	@Override
	public void visitPublicEnum(JarClass jarClass) {
		_publicEnumCount++;
	}

	@Override
	public void visitPackagePrivateEnum(JarClass jarClass) {
		_packagePrivateEnumCount++;
	}

	@Override
	public void visitPublicInterface(JarClass jarClass) {
		_publicInterfaceCount++;
	}

	@Override
	public void visitPackagePrivateInterface(JarClass jarClass) {
		_packagePrivateInterfaceCount++;
	}
}
