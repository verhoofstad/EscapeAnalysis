package org.classHierarchy.tree;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.objectweb.asm.Opcodes;

public class TestData {

    /**
     * Returns an instance of JavaClass that represents the java.lang.Object type.
     */
    public static JavaClass javaObject() {

        int access = Opcodes.ACC_PUBLIC;

        return new JavaClass("/java/lang/Object", new AccessFlags(access), null, new JavaTypeSet(),
                new JarFile("rt.jar"));
    }
}
