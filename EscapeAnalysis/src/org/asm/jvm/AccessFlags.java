package org.asm.jvm;

import org.objectweb.asm.Opcodes;

public class AccessFlags {
	
	private int access;
	
	public AccessFlags(int access) {
		this.access = access;
	}

	public Boolean isAbstract() {
		return isAbstract(this.access);
	}

	public Boolean isAnnotation() {
		return isAnnotation(this.access);
	}
	
	public Boolean isBridge() {
		return isBridge(this.access);
	}
	
	public Boolean isDeprecated() {
		return isDeprecated(this.access);
	}
	
	public Boolean isEnum() {
		return isEnum(this.access);
	}
	
	public Boolean isFinal() {
		return isFinal(this.access);
	}
	
	public Boolean isInterface() {
		return isInterface(this.access);
	}

	public Boolean isNative() {
		return isNative(this.access);
	}

	public Boolean isPrivate() {
		return isPrivate(this.access);
	}

	public Boolean isProtected() {
		return isProtected(this.access);
	}

	public Boolean isPublic() {
		return isPublic(this.access);
	}

	public Boolean isStatic() {
		return isStatic(this.access);
	}

	public Boolean isStrict() {
		return isStrict(this.access);
	}

	public Boolean isSuper() {
		return isSuper(this.access);
	}

	public Boolean isSynchronized() {
		return isSynchronized(this.access);
	}

	public Boolean isSynthetic() {
		return isSynthetic(this.access);
	}

	public Boolean isTransient() {
		return isTransient(this.access);
	}

	public Boolean isVarArgs() {
		return isVarArgs(this.access);
	}

	public Boolean isVolatile() {
		return isVolatile(this.access);
	}

	public Boolean isPackagePrivate() {
		return isPackagePrivate(this.access);
	}

	
	
	
	public static Boolean isAbstract(int access) {
		return (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT;
	}

	public static Boolean isAnnotation(int access) {
		return (access & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION;
	}
	
	public static Boolean isBridge(int access) {
		return (access & Opcodes.ACC_BRIDGE) == Opcodes.ACC_BRIDGE;
	}
	
	public static Boolean isDeprecated(int access) {
		return (access & Opcodes.ACC_DEPRECATED) == Opcodes.ACC_DEPRECATED;
	}
	
	public static Boolean isEnum(int access) {
		return (access & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM;
	}
	
	public static Boolean isFinal(int access) {
		return (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
	}
	
	public static Boolean isInterface(int access) {
		return (access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;
	}

	public static Boolean isNative(int access) {
		return (access & Opcodes.ACC_NATIVE) == Opcodes.ACC_NATIVE;
	}

	public static Boolean isPrivate(int access) {
		return (access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
	}

	public static Boolean isProtected(int access) {
		return (access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED;
	}

	public static Boolean isPublic(int access) {
		return (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
	}

	public static Boolean isStatic(int access) {
		return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
	}

	public static Boolean isStrict(int access) {
		return (access & Opcodes.ACC_STRICT) == Opcodes.ACC_STRICT;
	}

	public static Boolean isSuper(int access) {
		return (access & Opcodes.ACC_SUPER) == Opcodes.ACC_SUPER;
	}

	public static Boolean isSynchronized(int access) {
		return (access & Opcodes.ACC_SYNCHRONIZED) == Opcodes.ACC_SYNCHRONIZED;
	}

	public static Boolean isSynthetic(int access) {
		return (access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC;
	}

	public static Boolean isTransient(int access) {
		return (access & Opcodes.ACC_TRANSIENT) == Opcodes.ACC_TRANSIENT;
	}

	public static Boolean isVarArgs(int access) {
		return (access & Opcodes.ACC_VARARGS) == Opcodes.ACC_VARARGS;
	}

	public static Boolean isVolatile(int access) {
		return (access & Opcodes.ACC_VOLATILE) == Opcodes.ACC_VOLATILE;
	}

	public static Boolean isPackagePrivate(int access) {
		return !isPublic(access) && !isProtected(access) && !isPrivate(access);
	}
}
