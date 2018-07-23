package org.asm;

import org.asm.jvm.AccessFlags;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

class JarClassVisitor extends ClassVisitor {
    private JarFileVisitor visitor;
    private ClassReader reader;
    private JarFile jarfile;

    public JarClassVisitor(JarFileVisitor visitor, ClassReader reader, JarFile jarFile) {
        super(Opcodes.ASM6);

        this.visitor = visitor;
        this.reader = reader;
        this.jarfile = jarFile;
    }

    /*
     * Visits the header of the class.
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        AccessFlags accessFlags = new AccessFlags(access);

        if (accessFlags.isEnum()) {

            if (accessFlags.isPublic()) {
                this.visitor.visitPublicEnum(new JarClass(name, superName, interfaces, accessFlags, this.reader, this.jarfile));
            } else {
                this.visitor.visitPackagePrivateEnum(new JarClass(name, superName, interfaces, accessFlags, this.reader, this.jarfile));
            }
        }

        if (accessFlags.isInterface()) {

            if (accessFlags.isPublic()) {
                this.visitor.visitPublicInterface(new JarClass(name, superName, interfaces, accessFlags, this.reader, this.jarfile));
            } else {
                this.visitor.visitPackagePrivateInterface(new JarClass(name, superName, interfaces, accessFlags, this.reader, this.jarfile));
            }
        }

        if (!accessFlags.isEnum() && !accessFlags.isInterface()) {

            if (accessFlags.isPublic()) {
                this.visitor.visitPublicClass(new JarClass(name, superName, interfaces, accessFlags, this.reader, this.jarfile));
            } else {
                this.visitor.visitPackagePrivateClass(new JarClass(name, superName, interfaces, accessFlags, this.reader, this.jarfile));
            }
        }
    }
}