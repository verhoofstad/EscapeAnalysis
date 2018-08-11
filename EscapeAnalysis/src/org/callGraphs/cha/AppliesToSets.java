package org.callGraphs.cha;

import java.util.HashMap;
import java.util.Map;

import org.asm.jvm.MethodSignature;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;
import org.classHierarchy.JavaTypeSet;

/**
 * Represents an optimized look-up storage set of all the applies-to sets in a class hierarchy.
 */
class AppliesToSets {

    // Contains all the method's applies-to sets grouped by their signature.
    Map<MethodSignature, JavaMethodSet> appliesToSet;

    public AppliesToSets(ClassHierarchy classHierarchy) {
        if (classHierarchy == null) { throw new IllegalArgumentException("Parameter 'classHierarchy' should not be null."); }

        this.appliesToSet = new HashMap<MethodSignature, JavaMethodSet>();

        for (JavaType javaClass : classHierarchy.getClasses()) {

            for (JavaMethod declaredMethod : javaClass.declaredMethods()) {

                // Ignore static methods: Since the applies-to sets are used to resolve virtual instance calls, 
                // they can not apply to static methods by definition.
                // Ignore abstract methods: Since abstract methods are never called at run-time, they can be omitted
                // from the applies-to set as well. This can however, introduce empty call-sites if the method has no 
                // concrete implementation anywhere in the code.
                if(!declaredMethod.isStatic() && !declaredMethod.isAbstract()) {
                    if (!this.appliesToSet.containsKey(declaredMethod.signature())) {
                        this.appliesToSet.put(declaredMethod.signature(), new JavaMethodSet());
                    }
                    this.appliesToSet.get(declaredMethod.signature()).add(declaredMethod);
                }
            }
        }

        for (JavaType javaInterface : classHierarchy.getInterfaces()) {

            for (JavaMethod declaredMethod : javaInterface.declaredMethods()) {

                // Ignore static methods: Since the applies-to sets are used to resolve virtual instance calls, 
                // they can not apply to static methods by definition.
                // Ignore abstract methods: Since abstract methods are never called at run-time, they can be omitted
                // from the applies-to set as well. This can however, introduce empty call-sites if the method has no 
                // concrete implementation anywhere in the code.
                // Note that since Java 8, interfaces still can have concrete methods (default methods) that need to be added here.
                if (!declaredMethod.isStatic() && !declaredMethod.isAbstract()) {
                    if (!this.appliesToSet.containsKey(declaredMethod.signature())) {

                        this.appliesToSet.put(declaredMethod.signature(), new JavaMethodSet());
                    }
                    this.appliesToSet.get(declaredMethod.signature()).add(declaredMethod);
                }
            }
        }
    }

    public JavaMethodSet appliesTo(JavaTypeSet coneSet, String name, String desc) {
        if (coneSet == null) { throw new IllegalArgumentException("Parameter 'coneSet' should not be null."); }
        if (name == null) { throw new IllegalArgumentException("Parameter 'name' should not be null."); }
        if (desc == null) { throw new IllegalArgumentException("Parameter 'desc' should not be null."); }

        MethodSignature signature = new MethodSignature(name, desc);

        JavaMethodSet methods = new JavaMethodSet();

        if (this.appliesToSet.containsKey(signature)) {

            for (JavaMethod method : this.appliesToSet.get(signature)) {

                if (method.appliesTo().overlapsWith(coneSet)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }
}
