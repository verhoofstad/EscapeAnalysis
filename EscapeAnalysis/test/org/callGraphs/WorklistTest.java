package org.callGraphs;

import static org.junit.jupiter.api.Assertions.*;

import org.Environment;
import org.asm.JarFile;
import org.asm.JarFileSet;
import org.asm.classHierarchyBuilding.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.entryPoints.ExportedMethodCollector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WorklistTest {

    private static JarFile projectFile = new JarFile(Environment.rootFolder + "\\Libraries\\commons-io\\commons-io\\jars\\commons-io-2.4.jar");
    private static JavaMethodSet entryPoints;

    @BeforeAll
    public static void beforeClass() {
        JarFileSet jarFiles = new JarFileSet(Environment.jdkFolder);
        jarFiles.add(projectFile);
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jarFiles.accept(builder);
        ClassHierarchy classHierarchy = builder.classHierarchy();
        ExportedMethodCollector collector = new ExportedMethodCollector(projectFile);
        entryPoints = collector.collectEntryPointsFrom(classHierarchy);
    }

    @Test
    void Worklist_size() {
        // Arrange
        Worklist worklist = new Worklist(entryPoints);

        // Act & Assert
        assertEquals(entryPoints.size(), worklist.size());
    }
    
    @Test
    void Worklist_itterate() {
        // Arrange
        Worklist worklist = new Worklist(entryPoints);
        JavaMethodSet itterated = new JavaMethodSet();

        // Act
        while(!worklist.isEmpty()) {
            itterated.add(worklist.getItem());
        }
        
        // Assert
        assertEquals(entryPoints.size(), itterated.size());
        assertEquals(0, worklist.size());
    }
    
    @Test
    void Worklist_duplicates() {
        // Arrange
        Worklist worklist = new Worklist(entryPoints);

        // Act
        for(JavaMethod entryPoint : entryPoints) {
            worklist.add(entryPoint);
        }
        
        // Assert
        assertEquals(entryPoints.size(), worklist.size());
    }
}
