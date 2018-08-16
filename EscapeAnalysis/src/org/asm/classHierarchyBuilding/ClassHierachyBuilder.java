package org.asm.classHierarchyBuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.asm.JarClass;
import org.asm.JarFile;
import org.asm.JarFileSetVisitor;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaInterface;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;

/**
 * Builds a class hierarchy.
 */
public class ClassHierachyBuilder extends JarFileSetVisitor {

    private boolean verbose = false;

    private JarFile currentJarFile;

    private Map<String, JavaTempType> tempClasses = new HashMap<String, JavaTempType>();
    private Map<String, JavaTempType> tempInterfaces = new HashMap<String, JavaTempType>();

    private JavaType rootNode;
    private JavaTypeSet classes = new JavaTypeSet();
    private JavaTypeSet interfaces = new JavaTypeSet();

    /**
     * Gets the constructed class hierarchy.
     */
    public ClassHierarchy classHierarchy() {
        return new ClassHierarchy(this.rootNode, this.classes, this.interfaces);
    }

    @Override
    public void visitJarFile(JarFile jarFile) {
        this.currentJarFile = jarFile;
    }

    @Override
    public void visitPublicClass(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPublicEnum(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPackagePrivateClass(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPackagePrivateEnum(JarClass jarClass) {
        processJarClass(jarClass);
    }

    @Override
    public void visitPublicInterface(JarClass jarClass) {
        processJarInterface(jarClass);
    }

    @Override
    public void visitPackagePrivateInterface(JarClass jarClass) {
        processJarInterface(jarClass);
    }

    @Override
    public void visitEnd() {

        println("Building interface hierarchy...");
        buildInterfaceHierarchy();

        if (this.tempInterfaces.size() != this.interfaces.size()) {
            throw new Error("Interface count mismatch. Expected: " + this.tempInterfaces.size() + ", Actual: "
                    + this.interfaces.size());
        }

        println("Building class hierarchy...");
        this.rootNode = buildClassHierarchy();

        this.tempClasses = null;
        this.tempInterfaces = null;
    }

    private void processJarClass(JarClass jarClass) {
        JavaTempType tempClass = new JavaTempType(jarClass, this.currentJarFile);
        processMethods(jarClass, tempClass);
        this.tempClasses.put(tempClass.name(), tempClass);
    }

    private void processJarInterface(JarClass jarClass) {
        JavaTempType tempInterface = new JavaTempType(jarClass, this.currentJarFile);
        processMethods(jarClass, tempInterface);
        this.tempInterfaces.put(tempInterface.name(), tempInterface);
    }

    private void processMethods(JarClass jarClass, JavaTempType javaTempType) {
        MethodLoader methodLoader = new MethodLoader(javaTempType);
        jarClass.accept(methodLoader);
    }

    private JavaType buildClassHierarchy() {

        List<JavaTempType> rootClasses = findRootClasses();

        if (rootClasses.size() == 1) {
            return resolveTempClass(rootClasses.get(0), null);
        } else {
            throw new Error("Expected exactly 1 root class, " + rootClasses.size() + " found.");
        }
    }

    private JavaClass resolveTempClass(JavaTempType tempClass, JavaClass superClass) {

        JavaTypeSet implementedInterfaces = this.interfaces.get(tempClass.superInterfaces());
        JavaClass javaClass = tempClass.resolveToJavaClass(superClass, implementedInterfaces);

        // Find sub-classes
        for (JavaTempType tempSubClass : this.tempClasses.values()) {

            if (tempSubClass.isSubClassOf(tempClass)) {
                javaClass.addSubClass(resolveTempClass(tempSubClass, javaClass));
            }
        }
        // Add the current class as sub-class of each implemented interface.
        for (JavaType implementedInterface : implementedInterfaces) {
            implementedInterface.addSubClass(javaClass);
        }
        // Add the class to the class set.
        this.classes.add(javaClass);
        return javaClass;
    }


    private List<JavaTempType> findRootClasses() {

        List<JavaTempType> rootClasses = new ArrayList<JavaTempType>();

        for (JavaTempType tempClass : this.tempClasses.values()) {
            if (tempClass.superClass() == null) {
                rootClasses.add(tempClass);
            }
        }
        return rootClasses;
    }

    private void buildInterfaceHierarchy() {

        List<JavaTempType> rootInterfaces = findRootInterfaces();

        for (JavaTempType tempInterface : rootInterfaces) {

            // It's possible the interface has already been resolved.
            // (e.g. if it was also a super interface of a previous resolved class)
            if (!this.interfaces.contains(tempInterface.name())) {

                JavaInterface javaInterface = tempInterface.resolveToJavaInterface(new JavaTypeSet());

                this.interfaces.add(javaInterface);

                // Find sub-interfaces
                for (JavaTempType tempSubInterface : this.tempInterfaces.values()) {

                    if (tempSubInterface.isSubInterfaceOf(tempInterface)) {
                        javaInterface.addSubInterface(resolveTempInterface(tempSubInterface));
                    }
                }
            }
        }
    }

    private JavaInterface resolveTempInterface(JavaTempType tempInterface) {

        // Resolve super-interfaces
        JavaTypeSet superInterfaces = new JavaTypeSet();

        for (JavaTempType tempSuperInterface : this.tempInterfaces.values()) {

            if (tempInterface.isSubInterfaceOf(tempSuperInterface)) {

                JavaType superInterface = this.interfaces.find(tempSuperInterface.name());

                if (superInterface != null) {
                    superInterfaces.add(superInterface);
                } else {
                    superInterfaces.add(resolveTempInterface(tempSuperInterface));
                }
            }
        }

        if (superInterfaces.size() != tempInterface.superInterfaces().length) {

            String error = String.format("Interfaces missing (2). Expected: %s, actual: %s\n",
                    tempInterface.superInterfaces().length, superInterfaces.size());

            throw new Error(error);
        }

        // Resolve current interface
        JavaInterface javaInterface = (JavaInterface) this.interfaces.find(tempInterface.name());

        if (javaInterface == null) {

            javaInterface = tempInterface.resolveToJavaInterface(superInterfaces);
            this.interfaces.add(javaInterface);

            // Find sub-interfaces
            for (JavaTempType tempSubInterface : this.tempInterfaces.values()) {

                if (tempSubInterface.isSubInterfaceOf(tempInterface)) {
                    javaInterface.addSubInterface(resolveTempInterface(tempSubInterface));
                }
            }
        }
        return javaInterface;
    }

    private List<JavaTempType> findRootInterfaces() {

        List<JavaTempType> rootInterfaces = new ArrayList<JavaTempType>();

        for (JavaTempType tempInterface : this.tempInterfaces.values()) {
            if (!tempInterface.hasSuperInterfaces()) {
                rootInterfaces.add(tempInterface);
            }
        }
        return rootInterfaces;
    }

    private void println(String format, Object... args) {
        if (this.verbose) {
            System.out.format(format + "\n", args);
        }
    }
}
