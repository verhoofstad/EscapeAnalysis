package org.classHierarchy.factoryMethods;

import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.ClassHierarchyVisitor;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

public abstract class FactoryMethodCollector  extends ClassHierarchyVisitor{

    private JavaMethodSet factoryMethods;

    public JavaMethodSet collectFactoryMethodsFrom(ClassHierarchy classHierarchy) {
        this.factoryMethods = new JavaMethodSet();
        classHierarchy.accept(this);
        return this.factoryMethods;
    }
    
    protected abstract boolean isFactoryMethod(JavaMethod javaMethod);
    
    @Override
    public void visitPublicMethod(JavaMethod javaMethod) { 
        this.visitMethod(javaMethod);
    }

    @Override
    public void visitProtectedMethod(JavaMethod javaMethod) {
        this.visitMethod(javaMethod);
    }

    @Override
    public void visitPackagePrivateMethod(JavaMethod javaMethod) {
        this.visitMethod(javaMethod);
    }

    @Override
    public void visitPrivateMethod(JavaMethod javaMethod) { 
        this.visitMethod(javaMethod);
    }
    
    private void visitMethod(JavaMethod javaMethod) {
        if(isFactoryMethod(javaMethod)) {
            this.factoryMethods.add(javaMethod);
        }
    }
}
