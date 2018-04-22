package org.counting;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;
import org.dataSets.Library;
import org.dataSets.LibraryResult;

public class ClassCounter extends JarFileSetVisitor {

    private LibraryResult result;

    public ClassCounter(Library library) {
        this.result = new LibraryResult(library);
    }

    public LibraryResult libraryResult() {
        return this.result;
    }

    public void visitPublicClass(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_publicClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPackagePrivateClass(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_packageVisibleClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPublicEnum(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_publicClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPackagePrivateEnum(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_packageVisibleClassCount++;

        countClassMethods(jarClass);
    }

    public void visitPublicInterface(JarClass jarClass) {
        this.result.all_interfaceCount++;
        this.result.all_classFileCount++;
        this.result.all_publicInterfaceCount++;

        countInterfaceMethods(jarClass);
    }

    public void visitPackagePrivateInterface(JarClass jarClass) {
        this.result.all_interfaceCount++;
        this.result.all_classFileCount++;
        this.result.all_packageVisibleInterfaceCount++;

        countInterfaceMethods(jarClass);
    }

    public void printTotals() {
        System.out.format("Public class count:              %s\n", this.result.all_publicClassCount);
        System.out.format("Package-private class count:     %s\n", this.result.all_packageVisibleClassCount);
        System.out.format("Total class count:               %s\n", this.result.all_classCount);
        System.out.println();
        System.out.format("Public interface count:          %s\n", this.result.all_publicInterfaceCount);
        System.out.format("Package-private interface count: %s\n", this.result.all_packageVisibleInterfaceCount);
        System.out.format("Total interface count:           %s\n", this.result.all_interfaceCount);
        System.out.println();
        System.out.format("Public method count:             %s\n", this.result.all_publicMethods);
        System.out.format("Protected method count:          %s\n", this.result.all_protectedMethods);
        System.out.format("Package-private method count:    %s\n", this.result.all_packagePrivateMethods);
        System.out.format("Private method count:            %s\n", this.result.all_privateMethods);
        System.out.format("Total method count:              %s\n", this.result.all_methodCount);
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
