package org.classHierarchy.factoryMethods;

import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class JarClassFactoryMethods extends ClassVisitor {

    private JavaType currentClass;
    private ClassHierarchy classHierarchy;

    public JarClassFactoryMethods(JavaType currentClass, ClassHierarchy classHierarchy) {
        super(Opcodes.ASM6);

        this.currentClass = currentClass;
        this.classHierarchy = classHierarchy;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        JavaMethod currentMethod = this.currentClass.getMethod(name, desc);

        if(currentMethod.isStatic() && currentMethod.hasReferenceReturnType()) {
            return new JarMethodFactoryMethods(currentMethod, this.classHierarchy);
        }
        return null;
    }
}