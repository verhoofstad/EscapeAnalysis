package org.asm;

import org.asm.jvm.AccessFlags;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

/**
 * Represents a class, enum or interface from a JAR-file.
 */
public class JarClass {

    private String name;
    private String superName;
    private String[] interfaces;
    private AccessFlags accessFlags;
    private ClassReader reader;
    private JarFile jarFile;

    JarClass(String name, String superName, String[] interfaces, AccessFlags accessFlags, ClassReader reader, JarFile jarFile) {

        this.name = name;
        this.superName = superName;
        this.interfaces = interfaces;
        this.accessFlags = accessFlags;
        this.reader = reader;
        this.jarFile = jarFile;
    }

    /**
     * Gets the internal name of the class.
     */
    public String name() {
        return this.name;
    }

    /**
     * Gets the internal of name of the super class. For interfaces, the super class
     * is Object. May be null, but only for the Object class.
     */
    public String superName() {
        return this.superName;
    }

    /**
     * Gets the internal names of the class's interfaces. May be null.
     */
    public String[] interfaces() {
        return this.interfaces;
    }

    /**
     * Gets the access flags for this type.
     */
    public AccessFlags accessFlags() {
        return this.accessFlags;
    }

    /**
     * Gets the JAR-file this class was loaded from.
     */
    public JarFile jarFile() {
        return this.jarFile;
    }
    
    public void accept(ClassVisitor visitor) {
        this.reader.accept(visitor, 0);
    }
}