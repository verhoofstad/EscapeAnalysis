package org.methodFinding;

import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class MethodFinder extends MethodVisitor {

    private JavaMethod currentMethod;
    private JavaTypeSet classes;
    private JavaMethodSet foundMethods;
    private boolean methodAdded = false;

    public MethodFinder(JavaMethod currentMethod, JavaTypeSet classes, JavaMethodSet foundMethods) {
        super(Opcodes.ASM6);

        this.currentMethod = currentMethod;
        this.classes = classes;
        this.foundMethods = foundMethods;
    }

    /**
     * Visits a type instruction. A type instruction is an instruction that takes
     * the internal name of a class as parameter.
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {

        switch (opcode) {
        case Opcodes.NEW:

            JavaType javaClass = classes.find(type);

            if (javaClass != null && !this.methodAdded) {
                this.foundMethods.add(this.currentMethod);
                this.methodAdded = true;
            }

            break;
        case Opcodes.ANEWARRAY:
            // System.out.format("new %s[]\n", type);
            break;
        }

        super.visitTypeInsn(opcode, type);
    }
}
