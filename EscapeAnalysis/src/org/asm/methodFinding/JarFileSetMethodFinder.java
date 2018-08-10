package org.asm.methodFinding;

import org.asm.JarClass;
import org.asm.JarFileSetVisitor;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;

/**
 * Finds the methods in which one of the provided classes is instantiated.
 */
public class JarFileSetMethodFinder extends JarFileSetVisitor {
    private ClassHierarchy classHierarchy;
    private JavaTypeSet classes;
    private JavaMethodSet foundMethods = new JavaMethodSet();

    public JarFileSetMethodFinder(ClassHierarchy classHierarchy, JavaTypeSet classes) {

        this.classHierarchy = classHierarchy;
        this.classes = classes;
    }

    public JavaMethodSet foundMethods() {
        return this.foundMethods;
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

        JavaType javaClass = this.classHierarchy.findClass(jarClass.name());

        if (javaClass != null) {
            JarClassMethodFinder methodFinder = new JarClassMethodFinder(javaClass, this.classes, this.foundMethods);

            jarClass.accept(methodFinder);
        } else {
            System.out.format("Error: Class %s was not found.\n", jarClass.name());
        }
    }
}
