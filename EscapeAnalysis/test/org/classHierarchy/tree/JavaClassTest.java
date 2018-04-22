package org.classHierarchy.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

/**
 * Provides test methods for the JavaClass class.
 */
class JavaClassTest {

    @Test
    void JavaClass_javaObject_id() {
        assertEquals("/java/lang/Object", TestData.javaObject().id());
    }

    @Test
    void JavaClass_javaObject_name() {
        assertEquals("/java/lang/Object", TestData.javaObject().name());
    }

    @Test
    void JavaClass_javaObject_packagePath() {
        assertEquals("/java/lang", TestData.javaObject().packagePath());
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
        JavaClass classA = new JavaClass("org/classA", new AccessFlags(0), javaObject, new JavaTypeSet(),
                new JarFile(""));

        // Act & Assert
        assertTrue(classA.hasSuperClass());
        assertEquals(javaObject, classA.superClass());
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
    void JavaClass_javaObject_subClasses_noSubClasses() {
        // Act
        JavaTypeSet subClasses = TestData.javaObject().subClasses();

        // Assert
        assertNotNull(subClasses, "The method JavaType.subClasses should never return null.");
        assertEquals(0, subClasses.size());
    }

    @Test
    void JavaClass_javaObject_subClasses_twoSubClasses() {

        // Arrange
        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = new JavaClass("/org/A", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));
        JavaClass classB = new JavaClass("/org/B", new AccessFlags(0), classA, new JavaTypeSet(), new JarFile(""));
        JavaClass classC = new JavaClass("/org/C", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));

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
    void JavaClass_javaObject_coneSet_noSubClasses() {
        // Act
        JavaTypeSet coneSet = TestData.javaObject().coneSet();

        // Assert
        assertNotNull(coneSet, "The method JavaType.coneSet should never return null.");
        assertEquals(1, coneSet.size());
    }

    @Test
    void JavaClass_javaObject_coneSet_twoSubClasses() {

        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = new JavaClass("/org/A", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));
        JavaClass classB = new JavaClass("/org/B", new AccessFlags(0), classA, new JavaTypeSet(), new JarFile(""));
        JavaClass classC = new JavaClass("/org/C", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));

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
    void JavaClass_javaObject_sootName() {
        assertEquals("java.lang.Object", TestData.javaObject().sootName());
    }

    @Test
    void JavaClass_javaObject_hasPublicSubClass() {

        // Arrange
        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = new JavaClass("/org/A", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));
        JavaClass classB = new JavaClass("/org/B", new AccessFlags(0), classA, new JavaTypeSet(), new JarFile(""));
        JavaClass classC = new JavaClass("/org/C", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));

        classA.addSubClass(classB);
        javaObject.addSubClass(classA);
        javaObject.addSubClass(classC);

        // Act & Assert
        assertFalse(javaObject.hasPublicSubClass());
    }

    @Test
    void JavaClass_javaObject_hasPublicSubClass_true() {

        // Arrange
        JavaClass javaObject = TestData.javaObject();
        JavaClass classA = new JavaClass("/org/A", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));
        JavaClass classB = new JavaClass("/org/B", new AccessFlags(Opcodes.ACC_PUBLIC), classA, new JavaTypeSet(),
                new JarFile(""));
        JavaClass classC = new JavaClass("/org/C", new AccessFlags(0), javaObject, new JavaTypeSet(), new JarFile(""));

        classA.addSubClass(classB);
        javaObject.addSubClass(classA);
        javaObject.addSubClass(classC);

        // Act & Assert
        assertTrue(javaObject.hasPublicSubClass());
    }
}