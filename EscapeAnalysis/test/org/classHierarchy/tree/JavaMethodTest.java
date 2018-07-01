package org.classHierarchy.tree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JavaMethodTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @Test
    void testId() {
        // Act & Assert
        assertEquals("java/lang/Object/wait():(V)", TestData.waitMethod().id());
        assertEquals("java/lang/Object/wait(long):(V)", TestData.waitMethodWithTimeoutParameter().id());
        assertEquals("java/lang/Object/wait(long,int):(V)", TestData.waitMethodWithTimeoutAndNanosParameters().id());
    }

    @Test
    void testName() {
        // Act & Assert
        assertEquals("wait", TestData.waitMethod().name());
        assertEquals("wait", TestData.waitMethodWithTimeoutParameter().name());
        assertEquals("wait", TestData.waitMethodWithTimeoutAndNanosParameters().name());
    }

    @Test
    void testSignature() {
        // Act & Assert
        assertEquals("wait::()V", TestData.waitMethod().signature());
        assertEquals("wait::(J)V", TestData.waitMethodWithTimeoutParameter().signature());
        assertEquals("wait::(JI)V", TestData.waitMethodWithTimeoutAndNanosParameters().signature());
    }

    @Test
    void testContainedIn() {
        // Act & Assert
        assertEquals(TestData.javaObject(), TestData.waitMethod().containedIn());
        assertEquals(TestData.javaObject(), TestData.waitMethodWithTimeoutParameter().containedIn());
        assertEquals(TestData.javaObject(), TestData.waitMethodWithTimeoutAndNanosParameters().containedIn());
    }

    @Test
    void testOverridenBy() {
        fail("Not yet implemented");
    }

    @Test
    void testIsPublic() {
        fail("Not yet implemented");
    }

    @Test
    void testIsProtected() {
        fail("Not yet implemented");
    }

    @Test
    void testIsPrivate() {
        fail("Not yet implemented");
    }

    @Test
    void testIsAbstract() {
        fail("Not yet implemented");
    }

    @Test
    void testIsStatic() {
        fail("Not yet implemented");
    }

    @Test
    void testIsConstructor() {
        fail("Not yet implemented");
    }

    @Test
    void testIsStaticInitializer() {
        fail("Not yet implemented");
    }

    @Test
    void testAppliesTo() {
        fail("Not yet implemented");
    }

    @Test
    void testOverridenByJavaMethod() {
        fail("Not yet implemented");
    }

    @Test
    void testEqualsObject() {
        fail("Not yet implemented");
    }

    @Test
    void testSignatureEqualsJavaMethod() {
        fail("Not yet implemented");
    }

    @Test
    void testSignatureEqualsStringString() {
        fail("Not yet implemented");
    }

    @Test
    void testSootName() {
        fail("Not yet implemented");
    }

    @Test
    void testSootParameters() {
        fail("Not yet implemented");
    }

    @Test
    void testSootReturnType() {
        fail("Not yet implemented");
    }

    @Test
    void testToString() {
        fail("Not yet implemented");
    }

    @Test
    void testToSignature() {
        fail("Not yet implemented");
    }
}
