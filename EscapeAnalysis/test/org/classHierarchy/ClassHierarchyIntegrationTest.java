package org.classHierarchy;

import static org.junit.jupiter.api.Assertions.*;

import org.asm.JarFileSet;
import org.classHierarchy.ClassHierachyBuilder;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaClass;
import org.classHierarchy.tree.JavaInterface;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClassHierarchyIntegrationTest {

    private static String javaObject = "java/lang/Object";
    private static String jdkFolder = "C:\\CallGraphData\\JavaJDK\\java-8-openjdk-amd64\\jre\\lib";
    private static ClassHierarchy jdkHierarchy;

    @BeforeAll
    public static void beforeClass() {
        JarFileSet jdkFiles = new JarFileSet(jdkFolder);
        ClassHierachyBuilder builder = new ClassHierachyBuilder();
        jdkFiles.accept(builder);
        jdkHierarchy = builder.classHierarchy();
    }

    @Test
    void ClassHierarchy_getClasses() {

        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        assertNotNull(allClasses, "The method ClassHierarchy.getClasses() should never return null.");
        for (JavaType javaType : allClasses) {
            assertTrue(javaType instanceof JavaClass);
        }
    }

    @Test
    void ClassHierarchy_getInterfaces() {

        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        assertNotNull(allInterfaces, "The method ClassHierarchy.getInterfaces() should never return null.");
        for (JavaType javaType : allInterfaces) {
            assertTrue(javaType instanceof JavaInterface);
        }
    }

    @Test
    void ClassHierarchy_getPublicClasses() {

        JavaTypeSet publicClasses = jdkHierarchy.getPublicClasses();
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        assertNotNull(publicClasses, "The method ClassHierarchy.getPublicClasses() should never return null.");
        assertTrue(publicClasses.isSubSetOfOrEqualTo(allClasses));
        assertTrue(publicClasses.isDisjointOf(jdkHierarchy.getFinalPackagePrivateClasses()));

        for (JavaType javaType : publicClasses) {
            assertTrue(javaType instanceof JavaClass);
            assertTrue(javaType.isPublic());
        }
    }

    @Test
    void ClassHierarchy_getFinalPackagePrivateClasses() {

        JavaTypeSet finalPackagePrivateClasses = jdkHierarchy.getFinalPackagePrivateClasses();
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        assertNotNull(finalPackagePrivateClasses,
                "The method ClassHierarchy.getFinalPackagePrivateClasses() should never return null.");
        assertTrue(finalPackagePrivateClasses.isSubSetOfOrEqualTo(allClasses));

        for (JavaType javaType : finalPackagePrivateClasses) {
            assertTrue(javaType instanceof JavaClass);
            assertTrue(javaType.isFinalPackagePrivate());
        }
    }

    @Test
    void ClassHierarchy_getExportedMethods() {
        JavaMethodSet exportedMethods = jdkHierarchy.getExportedMethods();

        assertNotNull(exportedMethods, "The method ClassHierarchy.getExportedMethods() should never return null.");
    }

    @Test
    void JavaClass_id() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes have an id.
        for (JavaType javaClass : allClasses) {
            assertNotNull(javaClass.id(), "The method JavaType.id() should never return null.");
            assertNotEquals("", javaClass.id(), "The method JavaType.id() should never return an empty string.");
        }
    }

    @Test
    void JavaClass_name() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes have a name.
        for (JavaType javaClass : allClasses) {
            assertNotNull(javaClass.name(), "The method JavaType.name() should never return null.");
            assertNotEquals("", javaClass.name(), "The method JavaType.name() should never return an empty string.");
        }
    }

    @Test
    void JavaClass_sootName() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes have a name.
        for (JavaType javaClass : allClasses) {
            assertNotNull(javaClass.sootName(), "The method JavaType.sootName() should never return null.");
            assertNotEquals("", javaClass.sootName(),
                    "The method JavaType.sootName() should never return an empty string.");
        }
    }

    @Test
    void JavaClass_publicOrPackagePrivate() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes are either public or package-private (not both nor
        // neither).
        for (JavaType javaClass : allClasses) {
            assertTrue(javaClass.isPublic() ^ javaClass.isPackagePrivate());

            if (javaClass.isFinalPackagePrivate()) {
                assertTrue(javaClass.isPackagePrivate());
            }
            if (!javaClass.isPackagePrivate()) {
                assertFalse(javaClass.isFinalPackagePrivate());
            }
        }
    }

    @Test
    void JavaClass_isFinal() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all final classes don't have any subclasses.
        for (JavaType javaClass : allClasses) {
            if (javaClass.isFinal()) {
                assertEquals(0, javaClass.subClasses().size());
                assertEquals(1, javaClass.coneSet().size());
                assertFalse(javaClass.isAbstract());
                // Assert methods of final classes are never overridden.
                for (JavaMethod method : javaClass.declaredMethods()) {
                    assertTrue(method.overridenBy().isEmpty());
                }
            }
        }
    }

    @Test
    void JavaClass_isAbstract() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert no abstract classes are also final.
        for (JavaType javaClass : allClasses) {
            if (javaClass.isAbstract()) {
                assertFalse(javaClass.isFinal());
            }
        }
    }

    @Test
    void JavaClass_packagePath() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes have a package path.
        for (JavaType javaClass : allClasses) {
            assertNotNull(javaClass.packagePath(), "The method JavaType.packagePath() should never return null.");
            assertNotEquals("", javaClass.packagePath(),
                    "The method JavaType.packagePath() should never return an empty string.");
        }
    }

    @Test
    void JavaClass_subClasses() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaClass : allClasses) {
            JavaTypeSet subClasses = javaClass.subClasses();

            assertNotNull(subClasses, "The method JavaType.subClasses() should never return null.");

            // Assert all sub types are in fact classes (i.e. not interfaces).
            for (JavaType subType : subClasses) {
                assertTrue(subType instanceof JavaClass);
            }

            // The set of sub classes is always a sub-set of the cone set.
            assertTrue(javaClass.subClasses().isSubSetOfOrEqualTo(javaClass.coneSet()));
        }
    }

    @Test
    void JavaClass_coneSet() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes have a non-empty cone set.
        for (JavaType javaClass : allClasses) {
            assertNotNull(javaClass.coneSet(), "The method JavaType.coneSet() should never return null.");
            assertNotEquals(0, javaClass.coneSet().size(), "The method JavaType.coneSet() should never be empty.");

            // Assert that the cone set of a class only contains classes (i.e. not
            // interfaces).
            for (JavaType coneClass : javaClass.coneSet()) {
                assertTrue(coneClass instanceof JavaClass);
            }
        }
    }

    @Test
    void JavaClass_declaredMethods() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaType : allClasses) {

            assertNotNull(javaType.declaredMethods());
        }
    }

    @Test
    void JavaClass_superClass() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all classes a superclass (except java.lang.Object).
        for (JavaType javaType : allClasses) {
            JavaClass javaClass = (JavaClass) javaType;

            if (javaClass.id().equals(javaObject)) {
                assertFalse(javaClass.hasSuperClass(), "The class java.lang.Object should not have a superclass.");
                assertNull(javaClass.superClass(), "The class java.lang.Object should not have a superclass.");
            } else {
                assertTrue(javaClass.hasSuperClass(),
                        "All classes other than java.lang.Object must have a superclass.");
                assertNotNull(javaClass.superClass(),
                        "All classes other than java.lang.Object must have a superclass.");
                assertFalse(javaClass.coneSet().contains(javaClass.superClass()),
                        "A superclass of a class can never be a subclass of that same class.");
            }
        }
    }

    @Test
    void JavaInterface_id() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces have an id.
        for (JavaType javaInterface : allInterfaces) {
            assertNotNull(javaInterface.id(), "The method JavaType.id() should never return null.");
            assertNotEquals("", javaInterface.id(), "The method JavaType.id() should never return an empty string.");
        }
    }

    @Test
    void JavaInterface_name() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces have a name.
        for (JavaType javaInterface : allInterfaces) {
            assertNotNull(javaInterface.name(), "The method JavaType.name() should never return null.");
            assertNotEquals("", javaInterface.name(),
                    "The method JavaType.name() should never return an empty string.");
        }
    }

    @Test
    void JavaInterface_sootName() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces have a name.
        for (JavaType javaInterface : allInterfaces) {
            assertNotNull(javaInterface.sootName(), "The method JavaType.sootName() should never return null.");
            assertNotEquals("", javaInterface.sootName(),
                    "The method JavaType.sootName() should never return an empty string.");
        }
    }

    @Test
    void JavaInterface_publicOrPackagePrivate() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces are either public or package-private (not both nor
        // neither).
        for (JavaType javaInterface : allInterfaces) {
            assertTrue(javaInterface.isPublic() ^ javaInterface.isPackagePrivate());

            if (javaInterface.isFinalPackagePrivate()) {
                assertTrue(javaInterface.isPackagePrivate());
            }
            if (!javaInterface.isPackagePrivate()) {
                assertFalse(javaInterface.isFinalPackagePrivate());
            }
        }
    }

    @Test
    void JavaInterface_isFinal() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert no interface is final.
        for (JavaType javaInterface : allInterfaces) {
            assertFalse(javaInterface.isFinal());
        }
    }

    @Test
    void JavaInterface_isAbstract() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces are abstract.
        for (JavaType javaInterface : allInterfaces) {
            assertTrue(javaInterface.isAbstract());
        }
    }

    @Test
    void JavaInterface_packagePath() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces have a package path.
        for (JavaType javaInterface : allInterfaces) {
            assertNotNull(javaInterface.packagePath(), "The method JavaType.packagePath() should never return null.");
            assertNotEquals("", javaInterface.packagePath(),
                    "The method JavaType.packagePath() should never return an empty string.");
        }
    }

    @Test
    void JavaInterface_subClasses() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        for (JavaType javaInterface : allInterfaces) {
            JavaTypeSet subClasses = javaInterface.subClasses();

            assertNotNull(subClasses, "The method JavaType.subClasses() should never return null.");

            // Assert all sub types are in fact classes (i.e. not interfaces).
            for (JavaType subType : subClasses) {
                assertTrue(subType instanceof JavaClass);
            }

            // The set of sub classes is always a sub-set of the cone set.
            assertTrue(javaInterface.subClasses().isSubSetOfOrEqualTo(javaInterface.coneSet()));
        }
    }

    @Test
    void JavaInterface_coneSet() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        // Assert all interfaces have a non-empty cone set.
        for (JavaType javaInterface : allInterfaces) {
            assertNotNull(javaInterface.coneSet(), "The method JavaType.coneSet() should never return null.");
            assertNotEquals(0, javaInterface.coneSet().size(), "The method JavaType.coneSet() should never be empty.");
        }
    }

    @Test
    void JavaInterface_declaredMethods() {
        JavaTypeSet allInterfaces = jdkHierarchy.getInterfaces();

        for (JavaType javaInterface : allInterfaces) {
            JavaMethodSet interfaceMethods = javaInterface.declaredMethods();

            assertNotNull(interfaceMethods);
        }
    }

    @Test
    void JavaClassMethod_id() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all class methods have a non-empty id.
        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {
                assertNotNull(javaMethod.id(), "The method JavaMethod.id() should never return null.");
                assertNotEquals("", javaMethod.id(), "The method JavaMethod.id() should never return an empty string.");
            }
        }
    }

    @Test
    void JavaClassMethod_name() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        // Assert all class methods have a non-empty name.
        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {
                assertNotNull(javaMethod.name(), "The method JavaMethod.name() should never return null.");
                assertNotEquals("", javaMethod.name(),
                        "The method JavaMethod.name() should never return an empty string.");
            }
        }
    }

    @Test
    void JavaClassMethod_accessFlags() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {

                if (javaMethod.isPublic()) {
                    assertFalse(javaMethod.isProtected());
                    assertFalse(javaMethod.isPrivate());
                }
                if (javaMethod.isProtected()) {
                    assertFalse(javaMethod.isPublic());
                    assertFalse(javaMethod.isPrivate());
                }
                if (javaMethod.isPrivate()) {
                    assertFalse(javaMethod.isProtected());
                    assertFalse(javaMethod.isPublic());
                }
                if (javaMethod.isAbstract()) {
                    assertFalse(javaMethod.isStaticInitializer());
                    assertFalse(javaMethod.isConstructor());
                    assertFalse(javaMethod.isStatic());
                }
                if (javaMethod.isStaticInitializer()) {
                    assertTrue(javaMethod.isStatic());
                    assertFalse(javaMethod.isConstructor());
                    assertFalse(javaMethod.isAbstract());
                }
            }
        }
    }

    @Test
    void JavaClassMethod_containedIn() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {
                assertSame(javaClass, javaMethod.containedIn());
            }
        }
    }

    @Test
    void JavaClassMethod_appliesTo() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {

                JavaTypeSet appliesToSet = javaMethod.appliesTo();

                assertNotNull(appliesToSet, "The method JavaMethod.appliesTo() should never return null.");
                assertTrue(appliesToSet.isSubSetOfOrEqualTo(javaClass.coneSet()));
                assertTrue(appliesToSet.contains(javaClass));
            }
        }
    }

    @Test
    void JavaClassMethod_overridenBy() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {

                JavaMethodSet overridenBy = javaMethod.overridenBy();

                assertNotNull(overridenBy, "The method JavaMethod.overridenBy() should never return null.");

                // Assert that overriding methods...
                for (JavaMethod overridingMethod : overridenBy) {
                    // have the same signature,
                    assertTrue(overridingMethod.signatureEquals(javaMethod));
                    // are contained in a different class,
                    assertTrue(overridingMethod.containedIn() != javaClass);
                    // are contained in one of the sub classes
                    assertTrue(javaClass.coneSet().contains(overridingMethod.containedIn()));
                    // and apply to a different set of types than the overridden method.
                    assertTrue(overridingMethod.appliesTo().isDisjointOf(javaMethod.appliesTo()));
                }
            }
        }
    }

    @Test
    void JavaClassMethod_jarFile() {
        JavaTypeSet allClasses = jdkHierarchy.getClasses();

        for (JavaType javaClass : allClasses) {
            for (JavaMethod javaMethod : javaClass.declaredMethods()) {
                assertSame(javaClass.jarFile(), javaMethod.jarFile());
            }
        }
    }
}
