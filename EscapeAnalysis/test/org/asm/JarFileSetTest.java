package org.asm;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JarFileSetTest {

    private static String currentFolder; 
    private static Set<JarFile> threeJarFiles;
    
    @BeforeAll
    static void setUpBeforeClass() {
        
        currentFolder = System.getProperty("user.dir");

        threeJarFiles = new HashSet<JarFile>();
        threeJarFiles.add(new JarFile("rt.jar"));
        threeJarFiles.add(new JarFile("jce.jar"));
        threeJarFiles.add(new JarFile("jsse.jar"));
    }
      
    @Test
    void JarFileSet_constructorListOfString() {
        
        List<String> listOfJarFiles = new ArrayList<String>();
        for(JarFile jarFile : threeJarFiles) {
            // Add each JAR-file twice
            listOfJarFiles.add(jarFile.getAbsolutePath());
            listOfJarFiles.add(jarFile.getAbsolutePath());
        }
        
        // Act
        JarFileSet jarFiles = new JarFileSet(listOfJarFiles);
        
        // Assert
        assertEquals(3, jarFiles.size());
    }

    @Test
    void JarFileSet_add() {
        
        // Arrange
        JarFileSet jarFiles = new JarFileSet(threeJarFiles);
        
        // Add a new JAR-file.
        jarFiles.add(new JarFile("new.jar"));
        assertEquals(4, jarFiles.size());
        
        // Add a new JAR-file.
        jarFiles.add(new JarFile("rt.jar"));
        assertEquals(4, jarFiles.size());
    }

    @Test
    void testSize() {
        JarFileSet jarFiles = new JarFileSet(threeJarFiles);
        
        assertEquals(3, jarFiles.size());
    }

    @Test
    void testGetSootClassPath() {
        JarFileSet jarFiles = new JarFileSet(threeJarFiles);
        
        // Act
        String sootClassPath = jarFiles.getSootClassPath();
        
        // Assert
        assertNotNull(sootClassPath);
        assertTrue(sootClassPath.contains(Paths.get(currentFolder, "rt.jar").toString()));
        assertTrue(sootClassPath.contains(Paths.get(currentFolder, "jce.jar").toString()));
        assertTrue(sootClassPath.contains(Paths.get(currentFolder, "jsse.jar").toString()));
        assertTrue(sootClassPath.contains(";"));
    }

    @Test
    void testToSootStringList() {
        JarFileSet jarFiles = new JarFileSet(threeJarFiles);
        
        // Act
        List<String> sootJarFiles = jarFiles.toSootStringList();
        
        // Assert
        assertNotNull(sootJarFiles);
        assertEquals(3, sootJarFiles.size());
    }

}
