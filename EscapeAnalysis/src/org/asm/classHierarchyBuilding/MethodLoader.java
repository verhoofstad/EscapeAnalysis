package org.asm.classHierarchyBuilding;

import org.asm.jvm.MethodSignature;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodLoader extends ClassVisitor {

    private JavaTempType currentType;

    MethodLoader(JavaTempType currentType) {
        super(Opcodes.ASM6);

        this.currentType = currentType;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        JavaTempMethod currentMethod = new JavaTempMethod(access, new MethodSignature(name, desc));
        this.currentType.addMethod(currentMethod);
        
        return new MethodInvocationLoader(currentMethod);
    }
}