package org.asm.classHierarchyBuilding;

import java.util.ArrayList;
import java.util.List;

import org.asm.jvm.InvokedMethod;
import org.asm.jvm.MethodSignature;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;

class JavaTempMethod {

    private int access;
    private MethodSignature signature;
    private List<InvokedMethod> invokedMethods;

    JavaTempMethod(int access, MethodSignature signature) {
        this.access = access;
        this.signature = signature;
        this.invokedMethods = new ArrayList<InvokedMethod>();
    }

    public void addInvokedMethod(InvokedMethod invokedMethod) {
        this.invokedMethods.add(invokedMethod);
    }
    
    public JavaMethod resolveToJavaMethod(JavaType containedIn) {
        return new JavaMethod(containedIn, this.access, this.signature, this.invokedMethods);
    }
}
