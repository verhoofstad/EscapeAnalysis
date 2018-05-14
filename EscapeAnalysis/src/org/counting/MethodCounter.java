package org.counting;

import org.asm.jvm.AccessFlags;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodCounter extends ClassVisitor {

    private CountResults result;

    public MethodCounter(CountResults result) {
        super(Opcodes.ASM6);

        this.result = result;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        AccessFlags accessFlags = new AccessFlags(access);

        this.result.methodCount++;

        if (accessFlags.isPublic()) {
            this.result.publicMethods++;
        } else if (accessFlags.isProtected()) {
            this.result.protectedMethods++;
        } else if (accessFlags.isPackagePrivate()) {
            this.result.packagePrivateMethods++;
        } else if (accessFlags.isPrivate()) {
            this.result.privateMethods++;
        } else {
            throw new Error();
        }
        return null;
    }
}
