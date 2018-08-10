package org.classHierarchy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;
import org.junit.jupiter.api.Test;

class JavaTypeSetTest {

    @Test
    void JavaTypeSet_add() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        assertTrue(classes.isEmpty());
        
        // Act
        classes.add(TestData.createPublicClass("org/ClassA"));

        // Assert
        assertEquals(1, classes.size());
        assertFalse(classes.isEmpty());
    }
    
    @Test
    void JavaTypeSet_add_addingSameClass() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        
        // Act
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassA"));

        // Assert
        assertEquals(1, classes.size());
    }

    @Test
    void JavaTypeSet_add_addingDifferentClasses() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        
        // Act
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassB"));

        // Assert
        assertEquals(2, classes.size());
    }
    
    @Test
    void JavaTypeSet_addAll() {
        // Arrange
        List<JavaType> classList = new ArrayList<JavaType>();
        classList.add(TestData.createPublicClass("org/ClassA"));
        classList.add(TestData.createPublicClass("org/ClassA"));
        classList.add(TestData.createPublicClass("org/ClassB"));
        classList.add(TestData.createPublicClass("org/ClassB"));
        classList.add(TestData.createPublicClass("org/ClassC"));
        
        // Act 
        JavaTypeSet classes = new JavaTypeSet();
        classes.addAll(classList);
        
        // Assert
        assertEquals(3, classes.size());
    }

    @Test
    void JavaTypeSet_contains() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        classes.add(TestData.createPublicClass("org/ClassA"));

        // Act & Assert
        assertTrue(classes.contains("org/ClassA"));
        assertTrue(classes.contains(TestData.createPublicClass("org/ClassA")));
        assertFalse(classes.contains("org/ClassB"));
        assertFalse(classes.contains((JavaType)null));
    }
    
    @Test
    void JavaTypeSet_getMultiple() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassB"));
        classes.add(TestData.createPublicClass("org/ClassC"));

        // Act
        JavaTypeSet foundClasses = classes.get(new String[] {"org/ClassA", "org/ClassB"});
        
        // Assert
        assertNotNull(foundClasses);
        assertEquals(2, foundClasses.size());
    }

    @Test
    void JavaTypeSet_find() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassB"));
        classes.add(TestData.createPublicClass("org/ClassC"));

        // Act & Assert
        assertNotNull(classes.find("org/ClassA"));
        assertNotNull(classes.find("org/ClassC"));
        assertNull(classes.find("org/ClassD"));
        assertNull(classes.find("/org/ClassA"));
    }

    @Test
    void JavaTypeSet_isSubSetOfOrEqualTo() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassB"));
        classes.add(TestData.createPublicClass("org/ClassC"));
        
        JavaTypeSet strictSubSet = new JavaTypeSet();
        strictSubSet.add(TestData.createPublicClass("org/ClassA"));
        strictSubSet.add(TestData.createPublicClass("org/ClassC"));
        
        JavaTypeSet equalTo = new JavaTypeSet();
        equalTo.add(TestData.createPublicClass("org/ClassA"));
        equalTo.add(TestData.createPublicClass("org/ClassB"));
        equalTo.add(TestData.createPublicClass("org/ClassC"));

        JavaTypeSet superSet = new JavaTypeSet();
        superSet.add(TestData.createPublicClass("org/ClassA"));
        superSet.add(TestData.createPublicClass("org/ClassB"));
        superSet.add(TestData.createPublicClass("org/ClassC"));
        superSet.add(TestData.createPublicClass("org/ClassD"));

        JavaTypeSet disjoint = new JavaTypeSet();
        disjoint.add(TestData.createPublicClass("org/ClassX"));
        disjoint.add(TestData.createPublicClass("org/ClassY"));
        disjoint.add(TestData.createPublicClass("org/ClassZ"));

        JavaTypeSet emptySet = new JavaTypeSet();

        // Act & Assert
        assertTrue(strictSubSet.isSubSetOfOrEqualTo(classes));
        assertTrue(equalTo.isSubSetOfOrEqualTo(classes));
        assertFalse(superSet.isSubSetOfOrEqualTo(classes));
        assertFalse(disjoint.isSubSetOfOrEqualTo(classes));
        assertTrue(emptySet.isSubSetOfOrEqualTo(classes));

        assertFalse(classes.isSubSetOfOrEqualTo(strictSubSet));
        assertTrue(classes.isSubSetOfOrEqualTo(equalTo));
        assertTrue(classes.isSubSetOfOrEqualTo(superSet));
        assertFalse(classes.isSubSetOfOrEqualTo(disjoint));
        assertFalse(classes.isSubSetOfOrEqualTo(emptySet));
    }
    
    @Test
    void JavaTypeSet_isDisjointOf() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassB"));
        classes.add(TestData.createPublicClass("org/ClassC"));
        
        JavaTypeSet overlap = new JavaTypeSet();
        overlap.add(TestData.createPublicClass("org/ClassX"));
        overlap.add(TestData.createPublicClass("org/ClassY"));
        overlap.add(TestData.createPublicClass("org/ClassA"));

        JavaTypeSet disjoint = new JavaTypeSet();
        disjoint.add(TestData.createPublicClass("org/ClassX"));
        disjoint.add(TestData.createPublicClass("org/ClassY"));
        disjoint.add(TestData.createPublicClass("org/ClassZ"));
        
        JavaTypeSet emptySet = new JavaTypeSet();
        
        // Act & Assert
        assertTrue(disjoint.isDisjointOf(classes));
        assertTrue(emptySet.isDisjointOf(classes));
        assertFalse(overlap.isDisjointOf(classes));

        assertTrue(classes.isDisjointOf(disjoint));
        assertTrue(classes.isDisjointOf(emptySet));
        assertFalse(classes.isDisjointOf(overlap));
    }
    
    @Test
    void JavaTypeSet_overlapsWith() {
        
        // Arrange
        JavaTypeSet classes = new JavaTypeSet();
        classes.add(TestData.createPublicClass("org/ClassA"));
        classes.add(TestData.createPublicClass("org/ClassB"));
        classes.add(TestData.createPublicClass("org/ClassC"));
        
        JavaTypeSet overlap = new JavaTypeSet();
        overlap.add(TestData.createPublicClass("org/ClassX"));
        overlap.add(TestData.createPublicClass("org/ClassY"));
        overlap.add(TestData.createPublicClass("org/ClassA"));

        JavaTypeSet disjoint = new JavaTypeSet();
        disjoint.add(TestData.createPublicClass("org/ClassX"));
        disjoint.add(TestData.createPublicClass("org/ClassY"));
        disjoint.add(TestData.createPublicClass("org/ClassZ"));
        
        // Act & Assert
        assertFalse(disjoint.overlapsWith(classes));
        assertTrue(overlap.overlapsWith(classes));

        assertFalse(classes.overlapsWith(disjoint));
        assertTrue(classes.overlapsWith(overlap));
    }
    
}
