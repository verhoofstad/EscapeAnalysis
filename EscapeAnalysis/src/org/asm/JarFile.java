package org.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.ClassReader;

/**
 * Represents a JAR-file.
 */
public class JarFile {

    private File jarFile;

    public JarFile(String jarFile) {
        this(new File(jarFile));
    }

    public JarFile(File jarFile) {
        if (jarFile == null) { throw new IllegalArgumentException("Parameter 'jarFile' should not be null."); }
        
        this.jarFile = jarFile;
    }

    public String getAbsolutePath() {
        return this.jarFile.getAbsolutePath();
    }
    
    public void accept(JarFileVisitor visitor) {

        FileInputStream fis;
        try {
            fis = new FileInputStream(this.jarFile);
            JarInputStream jarStream = new JarInputStream(fis);
            JarEntry entry = jarStream.getNextJarEntry();
    
            while (entry != null) {
                if (entry.getName().endsWith(".class")) {
    
                    ClassReader cr = new ClassReader(jarStream);
                    JarClassVisitor cp = new JarClassVisitor(visitor, cr, this);
    
                    cr.accept(cp, 0);
                }
                entry = jarStream.getNextJarEntry();
            }
    
            jarStream.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return this.getAbsolutePath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JarFile)) {
            return false;
        }

        return this.getAbsolutePath().equals(((JarFile) obj).getAbsolutePath());
    }
    
    @Override
    public String toString() {
        return this.getAbsolutePath();
    }
}
