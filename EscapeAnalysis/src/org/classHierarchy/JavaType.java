package org.classHierarchy;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.asm.jvm.MethodSignature;

/**
 * Represents a Java class or interface.
 */
public abstract class JavaType {

    private String internalName;
    private String packagePath;
    private JarFile jarFile;

    private JavaTypeSet subClasses;
    private JavaTypeSet superInterfaces;

    private JavaTypeSet coneSet;

    private AccessFlags accessFlags;

    private JavaMethodSet declaredMethods;

    protected JavaType(String internalName, AccessFlags accessFlags, JavaTypeSet superInterfaces, JarFile jarFile) {
        this.internalName = internalName;
        this.accessFlags = accessFlags;
        this.superInterfaces = superInterfaces;
        this.jarFile = jarFile;

        this.subClasses = new JavaTypeSet();
        this.declaredMethods = new JavaMethodSet();
        this.coneSet = new JavaTypeSet(this);

        this.packagePath = internalName.substring(0, internalName.lastIndexOf("/"));
    }
    
    public abstract boolean isClass();
    
    public boolean isInterface() {
        return !this.isClass();
    }
    
    public abstract void accept(ClassHierarchyVisitor visitor);
    
    public abstract void accept(ConcreteMethodVisitor visitor);

    /**
     * Gets the fully qualified name that uniquely identifies this type.
     */
    public String id() {
        return this.internalName;
    }

    /**
     * Gets the path of the package this type is contained in. 
     */
    public String packagePath() {
        return this.packagePath;
    }

    public boolean isPublic() {
        return this.accessFlags.isPublic();
    }

    public boolean isPackagePrivate() {
        return !isPublic();
    }

    public boolean isFinalPackagePrivate() {
        return isPackagePrivate() && !hasPublicSubClass();
    }

    public boolean isFinal() {
        return this.accessFlags.isFinal();
    }

    public boolean isAbstract() {
        return this.accessFlags.isAbstract();
    }
    
    /**
     * Gets a value indicating whether this type is accessible.
     * This is the case if the type is public or package-private with a public sub type.
     */
    public boolean isAccessible() {
        return this.isPublic() || hasPublicSubClass();
    }

    /**
     * Gets the JAR-file this type was loaded from.
     */
    public JarFile jarFile() {
        return this.jarFile;
    }
    
    /**
     * Determines whether this type was loaded from a specified JAR-file.
     */
    public boolean isLoadedFrom(JarFile jarFile) {
        return this.jarFile.equals(jarFile);
    }
    
    /**
     * Gets a value indicating whether this type is a sub type (transitive) of a given type.
     */
    public boolean isSubTypeOf(JavaType superType) {
        if(superType == null) { throw new IllegalArgumentException("Parameter 'superType' should not be null."); }
        
        return !this.equals(superType) && superType.coneSet.contains(this);
    }
    
    public boolean isSubTypeOf(String internalName) {
        
        for(JavaType superInterface : this.superInterfaces) {
            if(superInterface.id().equals(internalName)) {
                return true;
            }
            if(superInterface.isSubTypeOf(internalName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the sub classes of the current type. If the current type represents
     * an interface, it returns the classes that implement it.
     */
    public JavaTypeSet subClasses() {
        return this.subClasses;
    }

    /**
     * Returns the cone set of the current type. If the current type is a class, it
     * returns the current instance and all direct and indirect sub classes. If the
     * current type is an interface, it returns the current instance, all direct and
     * indirect sub interfaces and all classes implementing those interfaces.
     */
    public JavaTypeSet coneSet() {
        return this.coneSet;
    }

    /**
     * Returns the set of methods which are declared (concrete or abstract) in this type.
     */
    public JavaMethodSet declaredMethods() {
        return this.declaredMethods;
    }

    @Override
    public int hashCode() {
        return this.internalName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof JavaType && this.id().equals(((JavaType) obj).id());
    }
    
    public void addSubClass(JavaClass subClass) {
        this.subClasses.add(subClass);
        addToConeSet(subClass);
    }

    protected void addToConeSet(JavaType subType) {

        if (!this.coneSet.contains(subType)) {
            this.coneSet.add(subType);
        }
        for (JavaType superInterface : this.superInterfaces) {
            superInterface.addToConeSet(subType);
        }
    }

    public void addMethod(JavaMethod method) {
        if (!containsMethod(method)) {
            this.declaredMethods.add(method);
            this.setOverride(method);
        } else {
            throw new Error("Multiple method loading.");
        }
    }

    protected void setOverride(JavaMethod overridingMethod) {

        for (JavaType superInterface : this.superInterfaces) {

            JavaMethod baseMethod = superInterface.findMethod(overridingMethod.signature());

            if (baseMethod != null) {
                if (!baseMethod.overridenBy().contains(overridingMethod.id())) {
                    baseMethod.overridenBy(overridingMethod);
                }
            } else {
                superInterface.setOverride(overridingMethod);
            }
        }
    }

    /**
     * Resolve all the applies-to sets of the declared methods for CHA.
     */
    public void resolveAppliesToSets() {
        for (JavaMethod declaredMethod : this.declaredMethods) {
            declaredMethod.resolveAppliestoSet();
        }
    }

    /**
     * Returns a value indicating whether this type has at least one public sub class (transitive).
     */
    private boolean hasPublicSubClass() {
        for (JavaType subClass : this.subClasses) {
            if (subClass.isPublic() || subClass.hasPublicSubClass()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a method that matches the given signature.
     */
    public JavaMethod findMethod(MethodSignature signature) {
        for (JavaMethod method : this.declaredMethods) {
            if (method.signature().equals(signature)) {
                return method;
            }
        }
        return null;
    }

    public JavaMethod getMethod(MethodSignature signature) {
        JavaMethod method = findMethod(signature);
        if (method != null) {
            return method;
        } else {
            throw new Error("Cannot find method " + signature.toString() + " in type " + this.id() + " in JAR-file " + this.jarFile());
        }
    }

    /**
     * Finds a static method that matches the given signature. If it cannot be found
     * in the current type, the super interfaces of this type are searched.
     */
    public JavaMethod findStaticMethod(MethodSignature signature) {

        for (JavaMethod declaredMethod : this.declaredMethods()) {

            if (declaredMethod.signature().equals(signature) && declaredMethod.isStatic()) {
                return declaredMethod;
            }
        }

        for (JavaType superInterface : this.superInterfaces) {

            JavaMethod staticMethod = superInterface.findStaticMethod(signature);
            if (staticMethod != null) {
                return staticMethod;
            }
        }
        return null;
    }

    public boolean containsMethod(JavaMethod method) {

        for (JavaMethod javaMethod : this.declaredMethods) {
            if (method.signature().equals(javaMethod.signature())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.id();
    }
}