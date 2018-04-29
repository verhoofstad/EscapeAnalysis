package org.classHierarchy.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a set of Java types (classes and/or interfaces).
 *
 */
public class JavaTypeSet implements Iterable<JavaType> {
    private Map<String, JavaType> types = new HashMap<String, JavaType>();

    public JavaTypeSet() { }

    public JavaTypeSet(JavaType item) {
        add(item);
    }

    public JavaTypeSet(Iterable<JavaType> items) {
        for (JavaType item : items) {
            add(item);
        }
    }

    public void add(JavaType item) {
        if (!this.types.containsKey(item.id())) {
            this.types.put(item.id(), item);
        } else {
            System.out.println("Type already added: " + item.id());
        }
    }

    public void addAll(Iterable<JavaType> items) {
        for (JavaType item : items) {
            add(item);
        }
    }

    public boolean contains(String id) {
        return this.types.containsKey(id);
    }

    public boolean contains(JavaType item) {
        if(item == null) { return false; }
        
        return this.types.containsKey(item.id());
    }

    /**
     * Finds the Java type with the given id. Returns null if the type was not found.
     */
    public JavaType find(String id) {
        if (this.contains(id)) {
            return this.types.get(id);
        }
        return null;
    }

    /**
     * Returns the Java type with the given id. Throws an error if the type was not found.
     */
    public JavaType get(String id) {
        if (this.contains(id)) {
            return this.types.get(id);
        }
        throw new Error("Could not find type " + id + ".");
    }
    
    /**
     * Returns the Java types with the given identifiers. Throws an error if one or more types could not be found.
     */
    public JavaTypeSet get(String[] identifiers) {
        
        JavaTypeSet types = new JavaTypeSet();

        for (String id : identifiers) {
            if (this.contains(id)) {
                types.add(this.types.get(id));
            } else {
                throw new Error("Could not find type " + id + ".");
            }
        }
        return types;
    }

    public void difference(JavaTypeSet typeSet) {

        for (JavaType javaType : typeSet) {
            if (this.contains(javaType)) {
                this.types.remove(javaType.id());
            }
        }
    }

    /**
     * Determines whether this set is a subset of a given set.
     */
    public boolean isSubSetOfOrEqualTo(JavaTypeSet other) {

        for (JavaType javaType : this.types.values()) {
            if (!other.contains(javaType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines whether this set is a disjoint of a given set (i.e. have no elements in common).
     */
    public boolean isDisjointOf(JavaTypeSet other) {
        for (JavaType javaType : this.types.values()) {
            if (other.contains(javaType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines whether this set has at least one element in common with a given set.
     */
    public boolean overlapsWith(JavaTypeSet typeSet) {

        for (String id : this.types.keySet()) {
            if (typeSet.contains(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of elements in this set.
     * @return The number of elements in this set.
     */
    public int size() {
        return this.types.size();
    }
 
    /**
     * Return a value indicating whether this set contains no elements.
     * @return True, if this set is empty; otherwise, false.
     */
    public boolean isEmpty() {
        return this.types.isEmpty();
    }

    public Iterator<JavaType> iterator() {
        return this.types.values().iterator();
    }
}
