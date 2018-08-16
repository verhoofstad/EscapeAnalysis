package org.classHierarchy.factoryMethods;

import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.ConcreteMethodVisitor;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

public abstract class FactoryMethodCollector extends ConcreteMethodVisitor {

    private JavaMethodSet factoryMethods;

    public JavaMethodSet collectFactoryMethodsFrom(ClassHierarchy classHierarchy) {
        this.factoryMethods = new JavaMethodSet();
        classHierarchy.accept(this);
        return this.factoryMethods;
    }
    
    protected abstract boolean isFactoryMethod(JavaMethod javaMethod);
    
    @Override
    public void visitConcreteMethod(JavaMethod javaMethod) { 
        if(isFactoryMethod(javaMethod)) {
            this.factoryMethods.add(javaMethod);
        }        
    }
}
