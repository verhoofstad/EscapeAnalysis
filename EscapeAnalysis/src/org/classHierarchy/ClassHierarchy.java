package org.classHierarchy;

import org.asm.JarFile;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;

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
            if (javaClass.isPublic() && javaClass.containedIn(jarFile)) {
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
            if (javaInterface.isPublic() && javaInterface.containedIn(jarFile)) {
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
                if (javaClass.containedIn(jarFile) && !method.isAbstract()) {
                    concreteMethods.add(method);
                }
            }
        }
        // Since Java 8, interfaces can also contain concrete methods (default methods).
        for (JavaType javaInterface : this.interfaces) {
            for (JavaMethod method : javaInterface.declaredMethods()) {
                if (javaInterface.containedIn(jarFile) && !method.isAbstract()) {
                    concreteMethods.add(method);
                }
            }
        }
        return concreteMethods;        
    }
    
    /**
     * Gets the exported methods for RTA which are contained in a given library
     * (JAR-file).
     * Abstract methods are excluded since they do not affect the call graph.
     * We also filter out synthetic methods since those 'should' not be called directly. --> We assume a Closed Package scenario.
     */
    public JavaMethodSet getExportedMethods(JarFile jarFile) {
        JavaMethodSet exportedMethods = new JavaMethodSet();
        
        for (JavaType publicClass : this.getPublicClasses(jarFile)) {

            boolean isNonFinal = !publicClass.isFinal();
            for (JavaMethod method : publicClass.declaredMethods()) {

                if(!method.isAbstract() && !method.isSynthetic()) {
                    if (method.isPublic() || (method.isProtected() && isNonFinal)) {
                        exportedMethods.add(method);
                    }
                }
            }
        }

        // Since Java 8, interfaces can also contain concrete methods (default methods).
        // These methods are public by default, so unless they are part of a package-private interface,
        for (JavaType publicInterface : this.getPublicInterfaces(jarFile)) {
            for (JavaMethod method : publicInterface.declaredMethods()) {
                if (!method.isAbstract() && !method.isSynthetic()) {
                    exportedMethods.add(method);
                }
            }
        }
        return exportedMethods;
    }
    
    public JavaMethodSet getCompilerGeneratedMethods(JarFile jarFile) {
        JavaMethodSet compilerGeneratedMethods = new JavaMethodSet();
        
        for(JavaType javaClass : this.classes) {
            if(javaClass.containedIn(jarFile)) {

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
            if(javaInterface.containedIn(jarFile)) {

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
}