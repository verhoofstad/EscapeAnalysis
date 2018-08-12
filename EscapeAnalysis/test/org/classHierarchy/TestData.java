package org.classHierarchy;

import org.asm.JarFile;
import org.asm.jvm.AccessFlags;
import org.asm.jvm.MethodSignature;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;
import org.objectweb.asm.Opcodes;

public class TestData {

    /**
     * Returns an instance of JavaClass that represents the java.lang.Object type.
     */
    public static JavaClass javaObject() {

        int access = Opcodes.ACC_PUBLIC;

        return new JavaClass("java/lang/Object", new AccessFlags(access), null, new JavaTypeSet(), new JarFile("rt.jar"));
    }
    
    public static JavaClass createPublicClass(String name) {
        return createPublicClass(name, null);
    }

    public static JavaClass createPublicClass(String name, JavaClass superClass) {
        int access = Opcodes.ACC_PUBLIC;

        return new JavaClass(name, new AccessFlags(access), superClass, new JavaTypeSet(), new JarFile(""));
    }
    public static JavaClass createPackagePrivateClass(String name) {
        return createPackagePrivateClass(name, null);
    }
    
    public static JavaClass createPackagePrivateClass(String name, JavaClass superClass) {
        return new JavaClass(name, new AccessFlags(0), superClass, new JavaTypeSet(), new JarFile(""));
    }
    
    public static JavaMethod createMethod(String name, JavaType containedIn) {
        return new JavaMethod(containedIn, Opcodes.ACC_PUBLIC, new MethodSignature(name, "()V"));
    }
    
    /**
     * Returns the example class hierarchy from the paper "Optimization of Object-Oriented Programs Using Static Class Hierarchy Analysis"
     * @return
     */
    public static JavaClass createClassHierarchyAnalysisTestSet() {
     
        JavaClass classA = createPublicClass("org/A");
        JavaClass classB = createPublicClass("org/B", classA);
        JavaClass classC = createPublicClass("org/C", classA);
        JavaClass classD = createPublicClass("org/D", classB);
        JavaClass classE = createPublicClass("org/E", classC);
        JavaClass classF = createPublicClass("org/F", classC);
        JavaClass classG = createPublicClass("org/G", classF);
        JavaClass classH = createPublicClass("org/H", classF);
        
        classA.addSubClass(classB);
        classA.addSubClass(classC);
        classB.addSubClass(classD);
        classC.addSubClass(classE);
        classC.addSubClass(classF);
        classF.addSubClass(classG);
        classF.addSubClass(classH);
        
        JavaMethod classAm = createMethod("m", classA);
        JavaMethod classBm = createMethod("m", classB);
        JavaMethod classCm = createMethod("m", classC);
        JavaMethod classEm = createMethod("m", classE);
        JavaMethod classAp = createMethod("p", classA);
        JavaMethod classFp = createMethod("p", classF);
        
        classA.addMethod(classAm);
        classB.addMethod(classBm);
        classC.addMethod(classCm);
        classE.addMethod(classEm);
        classA.addMethod(classAp);
        classF.addMethod(classFp);
        return classA;
    }
    
    public static JavaMethod waitMethod() {
        
        int access = Opcodes.ACC_PUBLIC;
        
        return new JavaMethod(javaObject(), access, new MethodSignature("wait", "()V"));
    }
    
    public static JavaMethod waitMethodWithTimeoutParameter() {
        
        int access = Opcodes.ACC_PUBLIC;
        
        return new JavaMethod(javaObject(), access, new MethodSignature("wait", "(J)V"));
    }
    
    public static JavaMethod waitMethodWithTimeoutAndNanosParameters() {
        
        int access = Opcodes.ACC_PUBLIC;
        
        return new JavaMethod(javaObject(), access, new MethodSignature("wait", "(JI)V"));
    }

}
