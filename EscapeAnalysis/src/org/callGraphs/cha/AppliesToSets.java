package org.callGraphs.cha;

import java.util.HashMap;
import java.util.Map;

import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.tree.JavaType;
import org.classHierarchy.tree.JavaTypeSet;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

/**
 * Represents an optimized look-up storage set of all the applies-to sets in a class hierarchy.
 */
class AppliesToSets {

    // Contains all the methods grouped by their signature.
    Map<String, JavaMethodSet> appliesToSet;

    public AppliesToSets(ClassHierarchy classHierarchy) {

        this.appliesToSet = new HashMap<String, JavaMethodSet>();

        for (JavaType javaClass : classHierarchy.getClasses()) {

            for (JavaMethod declaredMethod : javaClass.declaredMethods()) {

                // Ignore static methods that happen to have the same signature as existing instance methods.
                // Since the applies-to sets are used to resolve virtual instance calls, they can not apply to static methods by definition.
                // E.g. library 'scala-library-2.10.4.jar' has a static variant named hashCode() in the 'scala.collection.concurrent.TrieMapSerializationEnd' class.
                if(!declaredMethod.isStatic()) {
                    if (!this.appliesToSet.containsKey(declaredMethod.signature())) {
    
                        this.appliesToSet.put(declaredMethod.signature(), new JavaMethodSet());
                    }
                    this.appliesToSet.get(declaredMethod.signature()).add(declaredMethod);
                }
            }
        }

        for (JavaType javaInterface : classHierarchy.getInterfaces()) {

            for (JavaMethod declaredMethod : javaInterface.declaredMethods()) {

                // Ignore static methods that happen to have the same signature as another existing instance method.
                // Since the applies-to sets are used to resolve virtual instance calls, they can not apply to static methods by definition.
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

        String signature = JavaMethod.toSignature(name, desc);

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
