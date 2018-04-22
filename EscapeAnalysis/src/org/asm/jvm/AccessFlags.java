package org.asm.jvm;

import org.objectweb.asm.Opcodes;

public class AccessFlags {

    private int access;

    public AccessFlags(int access) {
        this.access = access;
    }

    public boolean isAbstract() {
        return (this.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT;
    }

    public boolean isAnnotation() {
        return (this.access & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION;
    }

    public boolean isBridge() {
        return (this.access & Opcodes.ACC_BRIDGE) == Opcodes.ACC_BRIDGE;
    }

    public boolean isDeprecated() {
        return (this.access & Opcodes.ACC_DEPRECATED) == Opcodes.ACC_DEPRECATED;
    }

    public boolean isEnum() {
        return (this.access & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM;
    }

    public boolean isFinal() {
        return (this.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
    }

    public boolean isInterface() {
        return (this.access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;
    }

    public boolean isNative() {
        return (this.access & Opcodes.ACC_NATIVE) == Opcodes.ACC_NATIVE;
    }

    public boolean isPrivate() {
        return (this.access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
    }

    public boolean isProtected() {
        return (this.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED;
    }

    public boolean isPublic() {
        return (this.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
    }

    public boolean isStatic() {
        return (this.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    }

    public boolean isStrict() {
        return (this.access & Opcodes.ACC_STRICT) == Opcodes.ACC_STRICT;
    }

    public boolean isSuper() {
        return (this.access & Opcodes.ACC_SUPER) == Opcodes.ACC_SUPER;
    }

    public boolean isSynchronized() {
        return (this.access & Opcodes.ACC_SYNCHRONIZED) == Opcodes.ACC_SYNCHRONIZED;
    }

    public boolean isSynthetic() {
        return (this.access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC;
    }

    public boolean isTransient() {
        return (this.access & Opcodes.ACC_TRANSIENT) == Opcodes.ACC_TRANSIENT;
    }

    public boolean isVarArgs() {
        return (this.access & Opcodes.ACC_VARARGS) == Opcodes.ACC_VARARGS;
    }

    public boolean isVolatile() {
        return (this.access & Opcodes.ACC_VOLATILE) == Opcodes.ACC_VOLATILE;
    }

    public boolean isPackagePrivate() {
        return !isPublic() && !isProtected() && !isPrivate();
    }
}
