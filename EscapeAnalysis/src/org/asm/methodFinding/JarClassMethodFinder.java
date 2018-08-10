package org.asm.methodFinding;

import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class JarClassMethodFinder extends ClassVisitor {

    private JavaType currentClass;
    private JavaTypeSet classes;
    private JavaMethodSet foundMethods;

    public JarClassMethodFinder(JavaType currentClass, JavaTypeSet classes, JavaMethodSet foundMethods) {
        super(Opcodes.ASM6);

        this.currentClass = currentClass;
        this.classes = classes;
        this.foundMethods = foundMethods;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        JavaMethod currentMethod = this.currentClass.findMethod(name, desc);

        if (currentMethod != null) {
            return new MethodFinder(currentMethod, this.classes, this.foundMethods);
        } else {
            System.out.println("Error: Method not found!!");
            return null;
        }
    }
}
