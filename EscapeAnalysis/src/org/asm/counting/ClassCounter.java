package org.asm.counting;

import org.asm.JarClass;
import org.asm.JarFile;
import org.asm.JarFileSetVisitor;

public class ClassCounter extends JarFileSetVisitor {

    private CountResults result;
    private JarFile libraryFile;

    public ClassCounter(JarFile libraryFile) {
        this.result = new CountResults();
        this.libraryFile = libraryFile;
    }

    public CountResults countResults() {
        return this.result;
    }

    public void visitPublicClass(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_publicClassCount++;
        
        if(jarClass.jarFile().equals(this.libraryFile)) {
            this.result.project_classCount++;
            this.result.project_classFileCount++;
            this.result.project_publicClassCount++;
        } else {
            this.result.libraries_classCount++;
            this.result.libraries_classFileCount++;
            this.result.libraries_publicClassCount++;
        }
        countClassMethods(jarClass);
    }

    public void visitPackagePrivateClass(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_packageVisibleClassCount++;

        if(jarClass.jarFile().equals(this.libraryFile)) {
            this.result.project_classCount++;
            this.result.project_classFileCount++;
            this.result.project_publicClassCount++;
        } else {
            this.result.libraries_classCount++;
            this.result.libraries_classFileCount++;
            this.result.libraries_publicClassCount++;
        }
        countClassMethods(jarClass);
    }

    public void visitPublicEnum(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_publicClassCount++;

        if(jarClass.jarFile().equals(this.libraryFile)) {
            this.result.project_classCount++;
            this.result.project_classFileCount++;
            this.result.project_publicClassCount++;
        } else {
            this.result.libraries_classCount++;
            this.result.libraries_classFileCount++;
            this.result.libraries_publicClassCount++;
        }
        countClassMethods(jarClass);
    }

    public void visitPackagePrivateEnum(JarClass jarClass) {
        this.result.all_classCount++;
        this.result.all_classFileCount++;
        this.result.all_packageVisibleClassCount++;

        if(jarClass.jarFile().equals(this.libraryFile)) {
            this.result.project_classCount++;
            this.result.project_classFileCount++;
            this.result.project_publicClassCount++;
        } else {
            this.result.libraries_classCount++;
            this.result.libraries_classFileCount++;
            this.result.libraries_publicClassCount++;
        }
        countClassMethods(jarClass);
    }

    public void visitPublicInterface(JarClass jarClass) {
        this.result.all_interfaceCount++;
        this.result.all_classFileCount++;
        this.result.all_publicInterfaceCount++;

        if(jarClass.jarFile().equals(this.libraryFile)) {
            this.result.project_interfaceCount++;
            this.result.project_classFileCount++;
            this.result.project_publicInterfaceCount++;
        } else {
            this.result.libraries_interfaceCount++;
            this.result.libraries_classFileCount++;
            this.result.libraries_publicInterfaceCount++;
        }
        countInterfaceMethods(jarClass);
    }

    public void visitPackagePrivateInterface(JarClass jarClass) {
        this.result.all_interfaceCount++;
        this.result.all_classFileCount++;
        this.result.all_packageVisibleInterfaceCount++;

        if(jarClass.jarFile().equals(this.libraryFile)) {
            this.result.project_interfaceCount++;
            this.result.project_classFileCount++;
            this.result.project_packageVisibleInterfaceCount++;
        } else {
            this.result.libraries_interfaceCount++;
            this.result.libraries_classFileCount++;
            this.result.libraries_packageVisibleInterfaceCount++;
        }
        countInterfaceMethods(jarClass);
    }

    private void countClassMethods(JarClass jarClass) {

        MethodCounter methodCounter = new MethodCounter(this.result, jarClass.jarFile().equals(this.libraryFile), false);

        jarClass.accept(methodCounter);
    }

    private void countInterfaceMethods(JarClass jarClass) {

        MethodCounter methodCounter = new MethodCounter(this.result, jarClass.jarFile().equals(this.libraryFile), true);

        jarClass.accept(methodCounter);
    }
}
