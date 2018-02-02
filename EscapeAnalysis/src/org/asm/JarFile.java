package org.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.ClassReader;

public class JarFile {

	private File _jarFile;
	
	public JarFile(File jarFile) throws FileNotFoundException {
		if(!jarFile.exists()) throw new FileNotFoundException();
		
		_jarFile = jarFile;
	}
	
	
	public void accept(JarVisitor visitor) throws IOException {
		
        FileInputStream fis = new FileInputStream(_jarFile);
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
