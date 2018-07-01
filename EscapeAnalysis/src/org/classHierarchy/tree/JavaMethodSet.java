package org.classHierarchy.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.asm.JarFile;
import org.asm.JarFileSet;

/**
 * Represents a set of methods.
 */
public class JavaMethodSet implements Iterable<JavaMethod> {

    private Map<String, JavaMethod> methods = new HashMap<String, JavaMethod>();

    public JavaMethodSet() {
    }

    public JavaMethodSet(Iterable<JavaMethod> items) {
        for (JavaMethod item : items) {
            add(item);
        }
    }

    public void add(JavaMethod item) {
        if (!this.methods.containsKey(item.id())) {
            this.methods.put(item.id(), item);
        } else {
            System.out.println("Already added: " + item.id());
        }
    }

    public void addAll(Iterable<JavaMethod> items) {
        for (JavaMethod item : items) {
            add(item);
        }
    }
    
    public JavaMethodSet difference(JavaMethodSet methodSet) {
        if (methodSet == null) { throw new IllegalArgumentException("Parameter 'methodSet' should not be null."); }

        JavaMethodSet difference = new JavaMethodSet();
        for (JavaMethod javaMethod : this.methods.values()) {
            if (!methodSet.contains(javaMethod)) {
                difference.add(javaMethod);
            }
        }
        return difference;
    }

    public boolean contains(String id) {
        return this.methods.containsKey(id);
    }

    public boolean contains(JavaMethod method) {
        return this.methods.containsKey(method.id());
    }

    /**
     * Gets a value indicating whether this set contains a constructor method.
     * 
     * @return True, if this set contains a constructor; otherwise, false.
     */
    public boolean containsConstructor() {
        for (JavaMethod method : this.methods.values()) {
            if (method.isConstructor()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a value indicating whether this set contains a static method.
     * 
     * @return True, if this set contains a static method; otherwise, false.
     */
    public boolean containsStaticMethod() {
        for (JavaMethod method : this.methods.values()) {
            if (method.isStatic()) {
                return true;
            }
        }
        return false;
    }

    public JavaMethod getRandom() {
        return this.methods.values().iterator().next();
    }

    public JavaTypeSet getClasses() {
        Set<JavaType> classes = new HashSet<JavaType>();

        for (JavaMethod method : this.methods.values()) {
            if (method.containedIn() instanceof JavaClass) {
                classes.add((JavaClass) method.containedIn());
            }
        }
        return new JavaTypeSet(classes);
    }

    public JavaMethodSet getMethodsOfClass(JavaType currentClass) {
        JavaMethodSet methods = new JavaMethodSet();

        for (JavaMethod method : this.methods.values()) {
            if (method.containedIn().equals(currentClass)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public void remove(String id) {
        this.methods.remove(id);
    }

    public JarFileSet jarFiles() {
        Set<JarFile> jarFiles = new HashSet<JarFile>();

        for (JavaMethod method : this.methods.values()) {
            jarFiles.add(method.jarFile());
        }
        return new JarFileSet(jarFiles);
    }

    public boolean isEmpty() {
        return this.methods.isEmpty();
    }

    public int size() {
        return this.methods.size();
    }

    @Override
    public Iterator<JavaMethod> iterator() {
        return this.methods.values().iterator();
    }
}
