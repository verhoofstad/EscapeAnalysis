package org.classHierarchy;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.asm.jvm.MethodSignature;

/**
 * Represents a Java class.
 */
public final class JavaClass extends JavaType {

    private JavaClass superClass;

    public JavaClass(String internalName, AccessFlags accessFlags, JavaClass superClass,
            JavaTypeSet implementedInterfaces, JarFile jarFile) {
        super(internalName, accessFlags, implementedInterfaces, jarFile);

        this.superClass = superClass;
    }
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public void accept(ClassHierarchyVisitor visitor) {
        if(this.isPublic()) {
            visitor.visitPublicClass(this);
        }
        else {
            visitor.visitPackagePrivateClass(this);
        }
        for(JavaMethod declaredMethod : this.declaredMethods()) {
            declaredMethod.accept(visitor);
        }
    }
    
    @Override
    public void accept(ConcreteMethodVisitor visitor) {
        for(JavaMethod declaredMethod : this.declaredMethods()) {
            declaredMethod.accept(visitor);
        }
    }

    public JavaClass superClass() {
        return this.superClass;
    }

    public boolean hasSuperClass() {
        return this.superClass != null;
    }
    
    @Override
    public boolean isSubTypeOf(String internalName) {
        
        if(super.isSubTypeOf(internalName)) {
            return true;
        }
        if(this.hasSuperClass()) {
            if(this.superClass.id().equals(internalName)) {
                return true;
            } else {
                return superClass.isSubTypeOf(internalName);
            }
        }
        return false;
    }
    
    /**
     * Gets the set of constructors this class contains.
     */
    public JavaMethodSet constructors() {
        JavaMethodSet constructors = new JavaMethodSet();
        
        for(JavaMethod declaredMethod : this.declaredMethods()) {
            if(declaredMethod.isConstructor()) {
                constructors.add(declaredMethod);
            }
        }
        return constructors;
    }
    
    public boolean hasNonPrivateConstructor() {
        for(JavaMethod constructor : this.constructors()) {
            if(!constructor.isPrivate()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addToConeSet(JavaType subType) {
        super.addToConeSet(subType);

        if (hasSuperClass()) {
            this.superClass.addToConeSet(subType);
        }
    }

    @Override
    protected void setOverride(JavaMethod overridingMethod) {
        super.setOverride(overridingMethod);

        if (this.hasSuperClass()) {

            JavaMethod baseMethod = this.superClass.findMethod(overridingMethod.signature());

            if (baseMethod != null) {
                baseMethod.overridenBy(overridingMethod);
            } else {
                this.superClass.setOverride(overridingMethod);
            }
        }
    }

    /**
     * Finds the static method that matches the given signature.
     */
    @Override
    public JavaMethod findStaticMethod(MethodSignature signature) {

        if (this.hasSuperClass()) {
            JavaMethod staticMethod = this.superClass.findStaticMethod(signature);
            if (staticMethod != null) {
                return staticMethod;
            }
        }
        return super.findStaticMethod(signature);
    }
}
