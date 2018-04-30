package org.escapeAnalysis;

import static org.junit.jupiter.api.Assertions.*;

import org.asm.JarFile;
import org.asm.JarFileSet;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EscapeAnalysisIntegrationTest {

    private static String jdkFolder = "C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64\\jre\\lib";
    private static String testProject = "C:\\CallGraphData\\TestProject.jar";
    
    private static JarFileSet jarFiles;
    private static ClassHierarchy classHierarchy;
    private static JavaType someClass;

    @BeforeAll
    public static void beforeClass() {
        
        jarFiles = new JarFileSet(jdkFolder);
        jarFiles.add(new JarFile(testProject));
        
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jarFiles.accept(builder);
        
        classHierarchy = builder.classHierarchy();
        someClass = classHierarchy.getClass("org/SomeClass");
    }
    
    @Test
    void test_noEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "noEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertTrue(escapeAnalysis.escapingClasses().isEmpty());
    }
    
    @Test
    void test_returnEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "returnEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    @Test
    void test_indirectReturnEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "indirectReturnEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    @Test
    void test_noParameterEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "noParameterEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertTrue(escapeAnalysis.escapingClasses().isEmpty());
    }
    
    @Test
    void test_parameterEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "parameterEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    @Test
    void test_indirectParameterEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "indirectParameterEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    @Test
    void test_thisEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "thisEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
                
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    
    @Test
    void test_methodEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "methodEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    @Test
    void test_indirectMethodEscape() {
        // Arrange
        JavaMethodSet methodsToAnalyse = getMethodByName(someClass, "indirectMethodEscape");
        EscapeAnalysis escapeAnalysis = new EscapeAnalysis(classHierarchy.getClasses());
        
        // Act
        escapeAnalysis.analyse(methodsToAnalyse, jarFiles);
        
        // Assert
        assertNotNull(escapeAnalysis.escapingClasses());
        assertEquals(1, escapeAnalysis.escapingClasses().size());
        assertTrue(escapeAnalysis.escapingClasses().contains("org/T"));
    }
    
    private JavaMethodSet getMethodByName(JavaType javaClass, String name) {
        
        JavaMethodSet methods = new JavaMethodSet();
        for(JavaMethod method : javaClass.declaredMethods()) {
            if(method.name().equals(name)) {
                methods.add(method);
            }
        }
        return methods;
    }
}
