package org.classHierarchy.methodFinding;

import org.asm.jvm.InvokedMethod;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.ConcreteMethodVisitor;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;

public class PackagePrivateClassMethodCollector extends ConcreteMethodVisitor {
    
    private JavaTypeSet classes;
    private JavaMethodSet foundMethods = new JavaMethodSet();

    public PackagePrivateClassMethodCollector(JavaTypeSet classes) {
        this.classes = classes;
    }

    public JavaMethodSet findMethodsIn(ClassHierarchy classHierarchy) {
        this.foundMethods = new JavaMethodSet();
        classHierarchy.accept(this);
        return this.foundMethods;
    }
    
    @Override
    public void visitConcreteMethod(JavaMethod javaMethod) { 
        if(this.invokesTargetClass(javaMethod)) {
            this.foundMethods.add(javaMethod);
        }        
    }
    
    private boolean invokesTargetClass(JavaMethod javaMethod) {
        
        JavaType declType = javaMethod.containedIn();
        
        if(declType.isInterface()) {
            return false;
        }
        
        for(InvokedMethod invocation : javaMethod.invokedMethods()) {
            
            if(invocation.isConstructorCall() && invocation.withNew()) {
                if(this.classes.contains(invocation.declaredType())) {
                    return true;
                }
            }
        }
        return false;
    }
}
