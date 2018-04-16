package org.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.ClassReader;

/**
 * Represents a JAR-file.
 */
public class JarFile {

	private File jarFile;
	
	public JarFile(String jarFile) {
		this.jarFile = new File(jarFile);
	}
	
	public JarFile(File jarFile) throws FileNotFoundException {
		if(!jarFile.exists()) throw new FileNotFoundException("File " + jarFile.getAbsolutePath() + " not found.");
		
		this.jarFile = jarFile;
	}
	
	public String getAbsolutePath() {
		return this.jarFile.getAbsolutePath();
	}
	
	public void accept(JarFileVisitor visitor) throws IOException {
		
        FileInputStream fis = new FileInputStream(this.jarFile);
        JarInputStream jarStream = new JarInputStream(fis);
        JarEntry entry = jarStream.getNextJarEntry();

        while (entry != null) {
            if(entry.getName().endsWith(".class")) {

            	ClassReader cr = getClassReader(jarStream);
        		JarClassVisitor cp = new JarClassVisitor(visitor, cr);
            	
    			cr.accept(cp, 0);
            }
            entry = jarStream.getNextJarEntry();
        }

        jarStream.close();
        fis.close();		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof JarFile)) { return false; }
		
		return this.getAbsolutePath().equals(((JarFile)obj).getAbsolutePath());
	}
	
    /**
     * Returns an ASM ClassReader based on an input stream. 
     */
    private ClassReader getClassReader(InputStream classStream) {
        try {
            return new ClassReader(classStream);
        }
        catch(Exception e) {
            return null;
        }
    }
}
