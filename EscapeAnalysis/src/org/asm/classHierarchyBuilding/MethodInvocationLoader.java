package org.asm.classHierarchyBuilding;

import org.asm.jvm.InvocationType;
import org.asm.jvm.InvokedMethod;
import org.asm.jvm.MethodSignature;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodInvocationLoader extends MethodVisitor {
    
    private JavaTempMethod currentMethod;
    private int instaniatingObject = 0;
    
    public MethodInvocationLoader(JavaTempMethod currentMethod) {
        super(Opcodes.ASM6);

        this.currentMethod = currentMethod;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {

        switch (opcode) {
        case Opcodes.NEW:
            this.instaniatingObject += 1;
            break;
        case Opcodes.ANEWARRAY:
            // Arrays are ignored for now.
            break;
        }

        super.visitTypeInsn(opcode, type);
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

        // Ignore array types.
        if (owner.startsWith("[")) {
            return;
        }

        MethodSignature signature = new MethodSignature(name, desc);
        InvocationType invocationType;
        boolean withNew = false;
        
        if(opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
            invocationType = InvocationType.CONSTRUCTOR;
            if(this.instaniatingObject > 0) {
                withNew = true;
                this.instaniatingObject -= 1;
            } 
        }
        else if(opcode == Opcodes.INVOKESTATIC) {
            invocationType = InvocationType.STATIC;
        }
        else {
            invocationType = InvocationType.VIRTUAL;
        }
        
        this.currentMethod.addInvokedMethod(new InvokedMethod(invocationType, owner, signature, withNew));
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
