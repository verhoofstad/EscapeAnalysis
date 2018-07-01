package org.classHierarchy.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.junit.jupiter.api.Test;

/**
 * Provides test methods for the JavaClass class.
 */
class JavaClassTest {

    @Test
    void JavaClass_javaObject_id() {
        assertEquals("java/lang/Object", TestData.javaObject().id());
    }

    @Test
    void JavaClass_javaObject_packagePath() {
        assertEquals("java/lang", TestData.javaObject().packagePath());
    }

    @Test
    void JavaClass_isPublic() {
        // Arrange
        JavaClass publicClass = TestData.createPublicClass("org/PublicClass");
        JavaClass packagePrivateClass = new JavaClass("org/PackagePrivateClass", new AccessFlags(0), null, new JavaTypeSet(), new JarFile(""));
        
        // Act & Assert
        assertTrue(publicClass.isPublic());
        assertFalse(packagePrivateClass.isPublic());
    }
    
    @Test
    void JavaClass_isPackagePrivate() {
        // Arrange
        JavaClass publicClass = TestData.createPublicClass("org/PublicClass");
        JavaClass packagePrivateClass = TestData.createPackagePrivateClass("org/PackagePrivateClass");
        
        // Act & Assert
        assertFalse(publicClass.isPackagePrivate());
        assertTrue(packagePrivateClass.isPackagePrivate());
    }

    @Test
    void JavaClass_isFinalPackagePrivate() {
        // Arrange
        JavaClass classA = TestData.createPackagePrivateClass("org/ClassA");
        JavaClass classB = TestData.createPackagePrivateClass("org/ClassB", classA);
        JavaClass classC = TestData.createPackagePrivateClass("org/ClassC", classB);
        classA.addSubClass(classB);
        classB.addSubClass(classC);
        
        // Act & Assert
        assertTrue(classA.isFinalPackagePrivate());
    }
    
    @Test
    void JavaClass_isFinalPackagePrivate_withPublicSubClass() {
        // Arrange
        JavaClass classA = TestData.createPackagePrivateClass("org/ClassA");
        JavaClass classB = TestData.createPackagePrivateClass("org/ClassB", classA);
        JavaClass classC = TestData.createPublicClass("org/ClassC", classB);
        classA.addSubClass(classB);
        classB.addSubClass(classC);
        
        // Act & Assert
        assertFalse(classA.isFinalPackagePrivate());
    }
    
    @Test
    void JavaClass_javaObject_accessFlags() {
        assertTrue(TestData.javaObject().isPublic());
        assertFalse(TestData.javaObject().isPackagePrivate());
        assertFalse(TestData.javaObject().isFinal());
        assertFalse(TestData.javaObject().isFinalPackagePrivate());
        assertFalse(TestData.javaObject().isAbstract());
    }
    
    @Test
    void JavaClass_jarFile() {
        assertEquals(new JarFile("rt.jar"), TestData.javaObject().jarFile());
    }
    
    @Test
    void JavaClass_subClasses_noSubClasses() {
        // Act
        JavaTypeSet subClasses = TestData.javaObject().subClasses();

        // Assert
        assertNotNull(subClasses, "The method JavaType.subClasses should never return null.");
        assertEquals(0, subClasses.size());
    }

    @Test
    void JavaClass_subClasses_twoSubClasses() {

        // Arrange
        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = new JavaClass("org/A", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));
        JavaClass classB = new JavaClass("org/B", new AccessFlags(0), classA, new JavaTypeSet(), new JarFile(""));
        JavaClass classC = new JavaClass("org/C", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));

        classA.addSubClass(classB);
        javaObject.addSubClass(classA);
        javaObject.addSubClass(classC);

        // Act
        JavaTypeSet subClasses = javaObject.subClasses();

        // Assert: Only class A and C are returned as sub classes of java.lang.Object.
        assertNotNull(subClasses, "The method JavaType.subClasses should never return null.");
        assertEquals(2, subClasses.size());
    }
    
    @Test
    void JavaClass_subClasses_withChaSet() {
        
        // Act
        JavaClass rootNode = TestData.createClassHierarchyAnalysisTestSet();
        
        // Assert
        assertNotNull(rootNode.subClasses());
        assertEquals(2, rootNode.subClasses().size());
        assertTrue(rootNode.subClasses().contains("org/B"));
        assertTrue(rootNode.subClasses().contains("org/C"));
        assertFalse(rootNode.hasSuperClass());

        JavaClass classB = (JavaClass)rootNode.subClasses().get("org/B");
        assertNotNull(classB.subClasses());
        assertEquals(1, classB.subClasses().size());
        assertTrue(classB.subClasses().contains("org/D"));
        assertSame(rootNode, classB.superClass());

        JavaClass classD = (JavaClass)classB.subClasses().get("org/D");
        assertNotNull(classD.subClasses());
        assertTrue(classD.subClasses().isEmpty());
        assertSame(classB, classD.superClass());
    
        JavaClass classC = (JavaClass)rootNode.subClasses().get("org/C");
        assertNotNull(classC.subClasses());
        assertEquals(2, classC.subClasses().size());
        assertTrue(classC.subClasses().contains("org/E"));
        assertTrue(classC.subClasses().contains("org/F"));
        assertSame(rootNode, classC.superClass());

        JavaClass classE = (JavaClass)classC.subClasses().get("org/E");
        assertNotNull(classE.subClasses());
        assertTrue(classE.subClasses().isEmpty());
        assertSame(classC, classE.superClass());
        
        JavaClass classF = (JavaClass)classC.subClasses().get("org/F");
        assertNotNull(classF.subClasses());
        assertEquals(2, classF.subClasses().size());
        assertTrue(classF.subClasses().contains("org/G"));
        assertTrue(classF.subClasses().contains("org/H"));
        assertSame(classC, classF.superClass());
        
        JavaClass classG = (JavaClass)classF.subClasses().get("org/G");
        assertNotNull(classG.subClasses());
        assertTrue(classG.subClasses().isEmpty());
        assertSame(classF, classG.superClass());
        
        JavaClass classH = (JavaClass)classF.subClasses().get("org/H");
        assertNotNull(classH.subClasses());
        assertTrue(classH.subClasses().isEmpty());
        assertSame(classF, classH.superClass());
    }
    
    @Test
    void JavaClass_coneSet_noSubClasses() {
        // Act
        JavaTypeSet coneSet = TestData.javaObject().coneSet();

        // Assert
        assertNotNull(coneSet, "The method JavaType.coneSet should never return null.");
        assertEquals(1, coneSet.size());
    }

    @Test
    void JavaClass_coneSet_twoSubClasses() {

        // Arrange
        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = TestData.createPublicClass("org/A", javaObject);
        JavaClass classB = TestData.createPublicClass("org/B", classA);
        JavaClass classC = TestData.createPublicClass("org/C", javaObject);
        classA.addSubClass(classB);
        javaObject.addSubClass(classA);
        javaObject.addSubClass(classC);

        // Act
        JavaTypeSet coneSetJavaObject = javaObject.coneSet();
        JavaTypeSet coneSetClassA = classA.coneSet();
        JavaTypeSet coneSetClassC = classC.coneSet();

        // Assert
        assertNotNull(coneSetJavaObject, "The method JavaType.coneSet should never return null.");
        assertEquals(4, coneSetJavaObject.size());
        assertNotNull(coneSetClassA, "The method JavaType.coneSet should never return null.");
        assertEquals(2, coneSetClassA.size());
        assertNotNull(coneSetClassC, "The method JavaType.coneSet should never return null.");
        assertEquals(1, coneSetClassC.size());
    }
    
    @Test
    void JavaClass_coneSet_withChaSet() {
        
        // Act
        JavaClass rootNode = TestData.createClassHierarchyAnalysisTestSet();
        
        // Assert
        assertNotNull(rootNode.coneSet());
        assertEquals(8, rootNode.coneSet().size());
        
        JavaType classB = rootNode.subClasses().get("org/B");
        assertNotNull(classB.coneSet());
        assertEquals(2, classB.coneSet().size());

        JavaType classD = classB.subClasses().get("org/D");
        assertNotNull(classD.coneSet());
        assertEquals(1, classD.coneSet().size());

        JavaType classC = rootNode.subClasses().get("org/C");
        assertNotNull(classC.coneSet());
        assertEquals(5, classC.coneSet().size());

        JavaType classE = classC.subClasses().get("org/E");
        assertNotNull(classE.coneSet());
        assertEquals(1, classE.coneSet().size());
        
        JavaType classF = classC.subClasses().get("org/F");
        assertNotNull(classF.coneSet());
        assertEquals(3, classF.coneSet().size());
        
        JavaType classG = classF.subClasses().get("org/G");
        assertNotNull(classG.coneSet());
        assertEquals(1, classG.coneSet().size());
        
        JavaType classH = classF.subClasses().get("org/H");
        assertNotNull(classH.coneSet());
        assertEquals(1, classH.coneSet().size());
    }
    
    @Test
    void JavaClass_declaredMethods_noMethods() {
        // Act
        JavaMethodSet declaredMethods = TestData.javaObject().declaredMethods();
        
        // Assert
        assertNotNull(declaredMethods);
        assertTrue(declaredMethods.isEmpty());
    }
    
    @Test
    void JavaClass_declaredMethods_oneDeclaredAndOneInherited() {
        
        // Arrange
        JavaClass classA = TestData.createPublicClass("org/A");
        JavaClass classB = TestData.createPublicClass("org/B", classA);
        classA.addSubClass(classB);
        
        classA.addMethod(TestData.createMethod("methodOfClassA", classA));
        classB.addMethod(TestData.createMethod("methodOfClassB", classB));
        
        // Act
        JavaMethodSet declaredMethods = classB.declaredMethods();
        
        // Assert: JavaType.declaredMethods should not return inherited methods.
        assertNotNull(declaredMethods);
        assertEquals(1, declaredMethods.size());
    }
    
    @Test
    void JavaClass_sootName() {
        assertEquals("java.lang.Object", TestData.javaObject().sootName());
    }
    
    @Test
    void JavaClass_addMethod() {
        // Arrange
        JavaClass classA = TestData.createPublicClass("org/A");
        JavaClass classB = TestData.createPublicClass("org/B", classA);
        classA.addSubClass(classB);
        
        JavaMethod methodClassA = TestData.createMethod("method", classA);
        JavaMethod methodClassB = TestData.createMethod("method", classB);
        
        // Act
        classA.addMethod(methodClassA);
        classB.addMethod(methodClassB);
        
        // Assert
        assertEquals(1, classA.declaredMethods().size());
        assertEquals(1, classB.declaredMethods().size());
        
        JavaMethodSet overriddesOfMethodA = methodClassA.overridenBy();
        assertNotNull(overriddesOfMethodA);
        assertEquals(1, overriddesOfMethodA.size());
    }
    
    @Test
    void JavaClass_resolveAppliesToSets() {
        
        JavaClass rootNode = TestData.createClassHierarchyAnalysisTestSet();
        
        // Act
        resolveAppliesToSets(rootNode);
        
        // Assert
        for(JavaMethod method : rootNode.declaredMethods()) {
            if(method.name().equals("m")) {
                assertEquals(2, method.overridenBy().size());
                assertEquals(1, method.appliesTo().size());
                assertTrue(method.appliesTo().contains("org/A"));
            }
            if(method.name().equals("p")) {
                assertEquals(1, method.overridenBy().size());
                assertEquals(5, method.appliesTo().size());
                assertTrue(method.appliesTo().contains("org/A"));
                assertTrue(method.appliesTo().contains("org/B"));
                assertTrue(method.appliesTo().contains("org/C"));
                assertTrue(method.appliesTo().contains("org/D"));
                assertTrue(method.appliesTo().contains("org/E"));
                assertFalse(method.appliesTo().contains("org/F"));
                assertFalse(method.appliesTo().contains("org/G"));
                assertFalse(method.appliesTo().contains("org/H"));
            }
        }
    }
    
    @Test
    void JavaClass_javaObject_hasSuperClass() {
        assertFalse(TestData.javaObject().hasSuperClass());
        assertNull(TestData.javaObject().superClass());
    }

    @Test
    void JavaClass_classA_hasSuperClass() {
        // Arrange
        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = TestData.createPublicClass("org/ClassA", javaObject);

        // Act & Assert
        assertTrue(classA.hasSuperClass());
        assertEquals(javaObject, classA.superClass());
    }


    private void resolveAppliesToSets(JavaType javaClass) {
        
        javaClass.resolveAppliesToSets();
        for(JavaType subClass : javaClass.subClasses()) {
            resolveAppliesToSets(subClass);
        }
    }

}