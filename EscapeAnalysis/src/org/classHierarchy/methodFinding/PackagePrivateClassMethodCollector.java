package org.classHierarchy.methodFinding;

import org.asm.jvm.InvokedMethod;
import org.classHierarchy.ClassHierarchyVisitor;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;

public class PackagePrivateClassMethodCollector extends ClassHierarchyVisitor {
    
    private JavaTypeSet classes;
    private JavaMethodSet foundMethods = new JavaMethodSet();

    public PackagePrivateClassMethodCollector(JavaTypeSet classes) {
        this.classes = classes;
    }

    public JavaMethodSet foundMethods() {
        return this.foundMethods;
    }
    
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
