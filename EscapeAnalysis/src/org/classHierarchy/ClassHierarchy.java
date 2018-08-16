package org.classHierarchy;

import org.asm.JarFile;

public class ClassHierarchy {

    private JavaType rootNode;
    private JavaTypeSet interfaces;
    private JavaTypeSet classes;

    public ClassHierarchy(JavaType rootNode, JavaTypeSet classes, JavaTypeSet interfaces) {
        if(rootNode == null) { throw new IllegalArgumentException("Parameter 'rootNode' should not be null."); }
        if(classes == null) { throw new IllegalArgumentException("Parameter 'classes' should not be null."); }
        if(interfaces == null) { throw new IllegalArgumentException("Parameter 'interfaces' should not be null."); }

        this.rootNode = rootNode;
        this.classes = classes;
        this.interfaces = interfaces;
    }

    public JavaType rootNode() {
        return this.rootNode;
    }

    public JavaType findType(String internalName) {
        if (this.classes.contains(internalName)) {
            return this.classes.get(internalName);
        } else if (this.interfaces.contains(internalName)) {
            return this.interfaces.get(internalName);
        } else {
            return null;
        }
    }

    public JavaType findClass(String internalName) {
        return this.classes.find(internalName);
    }

    public JavaType getClass(String internalName) {
        return this.classes.get(internalName);
    }

    public JavaType getType(String internalName) {
        if (this.containsType(internalName)) {
            return this.findType(internalName);
        } else {
            throw new Error("Could not find type " + internalName);
        }
    }

    public boolean containsType(String internalName) {
        return this.classes.contains(internalName) || this.interfaces.contains(internalName);
    }

    public JavaTypeSet getClasses() {
        return this.classes;
    }

    /**
     * Returns the set of public classes.
     */
    public JavaTypeSet getPublicClasses() {
        JavaTypeSet publicClasses = new JavaTypeSet();
        for (JavaType javaClass : this.classes) {
            if (javaClass.isPublic()) {
                publicClasses.add(javaClass);
            }
        }
        return publicClasses;
    }
    
    /**
     * Returns the set of public classes in a given JAR-file.
     */
    public JavaTypeSet getPublicClasses(JarFile jarFile) {
        JavaTypeSet publicClasses = new JavaTypeSet();
        
        for (JavaType javaClass : this.classes) {
            if (javaClass.isPublic() && javaClass.isLoadedFrom(jarFile)) {
                publicClasses.add(javaClass);
            }
        }
        return publicClasses;
    }
    
    public JavaTypeSet getInterfaces() {
        return this.interfaces;
    }

    /**
     * Returns the set of public interfaces.
     */
    public JavaTypeSet getPublicInterfaces() {
        JavaTypeSet publicInterfaces = new JavaTypeSet();
        for (JavaType javaInterface : this.interfaces) {
            if (javaInterface.isPublic()) {
                publicInterfaces.add(javaInterface);
            }
        }
        return publicInterfaces;
    }
    
    /**
     * Returns the set of public interfaces in a given JAR-file.
     */
    public JavaTypeSet getPublicInterfaces(JarFile jarFile) {
        JavaTypeSet publicInterfaces = new JavaTypeSet();
        
        for (JavaType javaInterface : this.interfaces) {
            if (javaInterface.isPublic() && javaInterface.isLoadedFrom(jarFile)) {
                publicInterfaces.add(javaInterface);
            }
        }
        return publicInterfaces;
    }
    
    /**
     * Returns the set of final package-private classes.
     */
    public JavaTypeSet getFinalPackagePrivateClasses() {
        JavaTypeSet finalPackagePrivateClasses = new JavaTypeSet();
        
        for (JavaType javaClass : this.classes) {
            if (javaClass.isFinalPackagePrivate()) {
                finalPackagePrivateClasses.add(javaClass);
            }
        }
        return finalPackagePrivateClasses;
    }

    /**
     * Returns the set of concrete methods (methods with an implementation) in a given JAR-file.
     */
    public JavaMethodSet getConcreteMethods(JarFile jarFile) {
        JavaMethodSet concreteMethods = new JavaMethodSet();
        
        for (JavaType javaClass : this.classes) {
            for (JavaMethod method : javaClass.declaredMethods()) {
                if (javaClass.isLoadedFrom(jarFile) && !method.isAbstract()) {
                    concreteMethods.add(method);
                }
            }
        }
        // Since Java 8, interfaces can also contain concrete methods (default methods).
        for (JavaType javaInterface : this.interfaces) {
            for (JavaMethod method : javaInterface.declaredMethods()) {
                if (javaInterface.isLoadedFrom(jarFile) && !method.isAbstract()) {
                    concreteMethods.add(method);
                }
            }
        }
        return concreteMethods;        
    }
    
    public void accept(ClassHierarchyVisitor visitor) {
        for(JavaType javaClass : this.classes) {
            javaClass.accept(visitor);
        }
        for(JavaType javaInterface : this.interfaces) {
            javaInterface.accept(visitor);
        }
    }
    
    public void accept(ConcreteMethodVisitor visitor) {
        for(JavaType javaClass : this.classes) {
            javaClass.accept(visitor);
        }
        for(JavaType javaInterface : this.interfaces) {
            javaInterface.accept(visitor);
        }
    }
    
    public JavaMethodSet getCompilerGeneratedMethods(JarFile jarFile) {
        JavaMethodSet compilerGeneratedMethods = new JavaMethodSet();
        
        for(JavaType javaClass : this.classes) {
            if(javaClass.isLoadedFrom(jarFile)) {

                for(JavaMethod javaMethod : javaClass.declaredMethods()) {
                    if(!javaMethod.isAbstract()) {
                        
                        // The name <clinit> is supplied by a compiler. Because the name <clinit> is not a valid identifier, it cannot be used directly in a program written in the Java programming language. Class and interface initialization methods are invoked implicitly by the Java Virtual Machine; they are never invoked directly from any Java Virtual Machine instruction, but are invoked only indirectly as part of the class initialization process. 
                        if(javaMethod.isStaticInitializer() || javaMethod.isSynthetic()) {
                            compilerGeneratedMethods.add(javaMethod);
                        }
                    }
                }
            }
        }
        for(JavaType javaInterface : this.interfaces) {
            if(javaInterface.isLoadedFrom(jarFile)) {

                for(JavaMethod javaMethod : javaInterface.declaredMethods()) {
                    if(!javaMethod.isAbstract()) {
                        
                        // The name <clinit> is supplied by a compiler. Because the name <clinit> is not a valid identifier, it cannot be used directly in a program written in the Java programming language. Class and interface initialization methods are invoked implicitly by the Java Virtual Machine; they are never invoked directly from any Java Virtual Machine instruction, but are invoked only indirectly as part of the class initialization process. 
                        if(javaMethod.isStaticInitializer() || javaMethod.isSynthetic()) {
                            compilerGeneratedMethods.add(javaMethod);
                        }
                    }
                }
            }
        }
        return compilerGeneratedMethods;
    }
    
    public void resolveAppliesToSets() {

        for (JavaType javaClass : this.classes) {
            javaClass.resolveAppliesToSets();
        }

        for (JavaType javaInterface : this.interfaces) {
            javaInterface.resolveAppliesToSets();
        }
    }
}