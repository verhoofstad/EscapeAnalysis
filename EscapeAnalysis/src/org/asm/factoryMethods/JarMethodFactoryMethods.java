package org.asm.factoryMethods;

import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethod;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class JarMethodFactoryMethods extends MethodVisitor {
    
    private JavaMethod currentStaticMethod;
    private ClassHierarchy classHierarchy;

    public JarMethodFactoryMethods(JavaMethod currentStaticMethod, ClassHierarchy classHierarchy) {
        super(Opcodes.ASM6);

        this.currentStaticMethod = currentStaticMethod;
        this.classHierarchy = classHierarchy;
    }
    
     @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

        if(opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
             
            // Find the constructor in the class hierarchy.
            JavaMethod constructor = classHierarchy.getClass(owner).getMethod(name, desc);
             
            if(constructor.isPrivate()) {
                this.currentStaticMethod.isFactoryMethod(true);
            }
        }
    }
}
