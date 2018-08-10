package org.asm.validation;

import java.util.HashSet;
import java.util.Set;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;

/**
 * Validates whether a library (set of jar-files) is complete and has no missing dependencies. 
 * i.e. all classes and interfaces that are extended, implemented, instantiated, invoked, etc. are 
 * present in the library.
 */
public class LibraryValidator extends JarFileSetVisitor {

    // Classes and interfaces that are defined in the JAR-set
    Set<String> declaredTypes = new HashSet<String>();
    
    // Classes and interfaces that are referenced in the JAR-set (i.e. the dependencies)
    Set<String> extendedTypes = new HashSet<String>();
    Set<String> implementedTypes = new HashSet<String>();
    Set<String> invokedTypes = new HashSet<String>();
    Set<String> instantiatedTypes = new HashSet<String>();

    @Override
    public void visitPublicClass(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPackagePrivateClass(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPublicEnum(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPackagePrivateEnum(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPublicInterface(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPackagePrivateInterface(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitEnd() {

        this.extendedTypes.removeAll(this.declaredTypes);
        this.implementedTypes.removeAll(this.declaredTypes);
        this.invokedTypes.removeAll(this.declaredTypes);
        this.instantiatedTypes.removeAll(this.declaredTypes);

        if (!this.extendedTypes.isEmpty()) {
            System.out.println("Unfound classes: " + this.extendedTypes.size());
            printFirst(this.extendedTypes, 10);
        } else {
            System.out.println("All extended classes are present.");
        }
        if (!this.implementedTypes.isEmpty()) {
            System.out.println("Unfound interfaces: " + this.implementedTypes.size());
            printFirst(this.implementedTypes, 10);
        } else {
            System.out.println("All implemented interfaces are present.");
        }
        if (!this.invokedTypes.isEmpty()) {
            System.out.println("Unfound invoked types: " + this.invokedTypes.size());
            printFirst(this.invokedTypes, 10);
        } else {
            System.out.println("All invoked types are present.");
        }
        if (!this.instantiatedTypes.isEmpty()) {
            System.out.println("Unfound instantiated classes: " + this.instantiatedTypes.size());
            printFirst(this.instantiatedTypes, 10);
        } else {
            System.out.println("All instantiated classes are present.");
        }

        if (this.isValid()) {
            System.out.println("Library is complete!");
        }
    }

    public boolean isValid() {
        return this.extendedTypes.isEmpty() && this.implementedTypes.isEmpty() && this.invokedTypes.isEmpty()
                && this.instantiatedTypes.isEmpty();
    }

    private void printFirst(Set<String> types, int count) {

        int i = 0;
        for (String invokedType : types) {
            if (i > count)
                break;

            System.out.println("   " + invokedType);
            i++;
        }
    }

    private void processJarClass(JarClass jarClass) {

        this.declaredTypes.add(jarClass.name());
        this.extendedTypes.add(jarClass.name());
        
        for (String implementedInterface : jarClass.interfaces()) {
            this.implementedTypes.add(implementedInterface);
            
            if(implementedInterface.equals("org/junit/rules/TestRule")) {
                System.out.format("Class %s implements %s in %s.\r\n", jarClass.name(), implementedInterface, jarClass.jarFile());
            }
        }

        jarClass.accept(new ClassValidator(this.invokedTypes, this.instantiatedTypes));
    }
}
