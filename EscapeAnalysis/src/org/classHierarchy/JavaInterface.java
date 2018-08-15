package org.classHierarchy;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;

/**
 * Represents a Java interface.
 */
public final class JavaInterface extends JavaType {

    private JavaTypeSet subInterfaces;

    public JavaInterface(String internalName, AccessFlags accessFlags, JavaTypeSet superInterfaces, JarFile jarFile) {
        super(internalName, accessFlags, superInterfaces, jarFile);

        this.subInterfaces = new JavaTypeSet();
    }

    @Override
    public boolean isClass() {
        return false;
    }
    
    @Override
    public void accept(ClassHierarchyVisitor visitor) {
        if(this.isPublic()) {
            visitor.visitPublicInterface(this);
        }
        else {
            visitor.visitPackagePrivateInterface(this);
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

    public void addSubInterface(JavaInterface subInterface) {
        this.subInterfaces.add(subInterface);

        this.addToConeSet(subInterface);
    }
}