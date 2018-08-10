package org.classHierarchy;

public abstract class ClassHierarchyVisitor {

    public void visitPublicClass(JavaType javaClass) { }

    public void visitPackagePrivateClass(JavaType javaClass) { }

    public void visitPublicInterface(JavaType javaInterface) { }
    
    public void visitPackagePrivateInterface(JavaType javaInterface) { }
    
    public void visitPublicMethod(JavaMethod javaMethod) { }

    public void visitProtectedMethod(JavaMethod javaMethod) { }

    public void visitPackagePrivateMethod(JavaMethod javaMethod) { }

    public void visitPrivateMethod(JavaMethod javaMethod) { }
}
