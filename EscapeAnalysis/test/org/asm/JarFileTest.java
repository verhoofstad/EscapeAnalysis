package org.asm;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JarFileTest {

    private static String currentFolder; 
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        currentFolder = System.getProperty("user.dir");
    }

    @Test
    public void JarFile_equals_withAbsolutePath() {
        
        JarFile jarFile = new JarFile("C:\\Programs\\Java\\rt.jar");
        
        assertTrue(jarFile.equals(jarFile));
        assertTrue(jarFile.equals(new JarFile("C:\\Programs\\Java\\rt.jar")));
        assertFalse(jarFile.equals(new JarFile("D:\\Programs\\Java\\rt.jar")));
        assertFalse(jarFile.equals(null));
    }

    @Test
    public void JarFile_equals_withRelativePath() {
        
        JarFile jarFile = new JarFile("rt.jar");
        
        assertTrue(jarFile.equals(jarFile));
        assertTrue(jarFile.equals(new JarFile("rt.jar")));
        assertTrue(jarFile.equals(new JarFile(Paths.get(currentFolder, "rt.jar").toString())));
        assertFalse(jarFile.equals(new JarFile("C:\\Programs\\Java\\rt.jar")));
        assertFalse(jarFile.equals(null));
    }

    @Test
    public void JarFile_getAbsolutePath() {
        
        JarFile jarFile = new JarFile("rt.jar");

        assertEquals(Paths.get(currentFolder, "rt.jar").toString(), jarFile.getAbsolutePath());
    }
    
    @Test
    public void JarFile_toString() {
        
        JarFile jarFile = new JarFile("C:\\Programs\\Java\\rt.jar");

        assertEquals("C:\\Programs\\Java\\rt.jar", jarFile.toString());
    }
}
