package org.counting;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;

public class ClassCounter extends JarFileSetVisitor {

    private CountResults result;

    public ClassCounter() {
        this.result = new CountResults();
    }

    public CountResults countResults() {
        return this.result;
    }

    public void visitPublicClass(JarClass jarClass) {
        this.result.classCount++;
        this.result.classFileCount++;
        this.result.publicClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPackagePrivateClass(JarClass jarClass) {
        this.result.classCount++;
        this.result.classFileCount++;
        this.result.packagePrivateClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPublicEnum(JarClass jarClass) {
        this.result.classCount++;
        this.result.classFileCount++;
        this.result.publicClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPackagePrivateEnum(JarClass jarClass) {
        this.result.classCount++;
        this.result.classFileCount++;
        this.result.packagePrivateClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPublicInterface(JarClass jarClass) {
        this.result.interfaceCount++;
        this.result.classFileCount++;
        this.result.publicInterfaceCount++;

        countInterfaceMethods(jarClass);
    }

    public void visitPackagePrivateInterface(JarClass jarClass) {
        this.result.interfaceCount++;
        this.result.classFileCount++;
        this.result.packagePrivateInterfaceCount++;

        countInterfaceMethods(jarClass);
    }

    private void countClassMethods(JarClass jarClass) {

        MethodCounter methodCounter = new MethodCounter(this.result);

        jarClass.accept(methodCounter);
    }

    private void countInterfaceMethods(JarClass jarClass) {

        MethodCounter methodCounter = new MethodCounter(this.result);

        jarClass.accept(methodCounter);
    }
}
