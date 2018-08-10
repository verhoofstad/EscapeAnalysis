package org.asm.factoryMethods;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaType;

/**
 * Finds the types which have a static factory method.
 */
public class JarFileSetFactoryMethods extends JarFileSetVisitor {

    private ClassHierarchy classHierarchy;

    public JarFileSetFactoryMethods(ClassHierarchy classHierarchy) {
        if(classHierarchy == null) { throw new IllegalArgumentException("Parameter 'classHierarchy' should not be null."); }
        
        this.classHierarchy = classHierarchy;
    }
    
    public void visitPublicClass(JarClass jarClass) {
        visitClass(jarClass);
    }

    public void visitPackagePrivateClass(JarClass jarClass) {
        visitClass(jarClass);
    }

    public void visitPublicEnum(JarClass jarClass) {
        visitClass(jarClass);
    }

    public void visitPackagePrivateEnum(JarClass jarClass) {
        visitClass(jarClass);
    }

    private void visitClass(JarClass jarClass) {

        JavaType currentClass = this.classHierarchy.getClass(jarClass.name());

        JarClassFactoryMethods methodFinder = new JarClassFactoryMethods(currentClass, this.classHierarchy);

        jarClass.accept(methodFinder);
    }
}