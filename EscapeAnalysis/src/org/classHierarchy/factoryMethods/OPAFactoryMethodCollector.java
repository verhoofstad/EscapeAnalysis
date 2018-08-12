package org.classHierarchy.factoryMethods;

import org.asm.jvm.InvokedMethod;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;

public class OPAFactoryMethodCollector extends FactoryMethodCollector {

    @Override
    protected boolean isFactoryMethod(JavaMethod javaMethod) {
        return javaMethod.containedIn().isClass() && javaMethod.isStatic() && javaMethod.hasReferenceReturnType() && invokesPrivateConstructor(javaMethod)
                && javaMethod.containedIn().coneSet().contains(javaMethod.referenceReturnType());
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
