package org.classHierarchy.factoryMethods;

import org.asm.jvm.InvokedMethod;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.ClassHierarchyVisitor;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;

public class OldFactoryMethodCollector extends ClassHierarchyVisitor {

    private JavaMethodSet factoryMethods;
    
    public JavaMethodSet collectFactoryMethodsFrom(ClassHierarchy classHierarchy) {
        this.factoryMethods = new JavaMethodSet();
        classHierarchy.accept(this);
        return this.factoryMethods;
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
        if(isFactoryMethod(javaMethod)) {
            this.factoryMethods.add(javaMethod);
        }
    }
    
    private boolean isFactoryMethod(JavaMethod javaMethod) {
        return javaMethod.containedIn().isClass() && javaMethod.isStatic() && javaMethod.hasReferenceReturnType() && invokesPrivateConstructor(javaMethod);
    }
    
    private boolean invokesPrivateConstructor(JavaMethod javaMethod) {
        
        JavaType declType = javaMethod.containedIn();
        
        for(InvokedMethod invocation : javaMethod.invokedMethods()) {
            
            if(invocation.isConstructorCall()) {
                
                JavaMethod constructor = declType.findMethod(invocation.signature());
                
                if(constructor != null && constructor.isPrivate() && constructor.containedIn().id().equals(invocation.declaredType())) {
                    return true;
                }
            }
        }
        return false;
    }
}
