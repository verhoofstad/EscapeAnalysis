package org.asm.classHierarchyBuilding;

import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaType;

class JavaTempMethod {

    private int access;
    private String name;
    private String desc;

    JavaTempMethod(int access, String name, String desc) {
        this.access = access;
        this.name = name;
        this.desc = desc;
    }

    public JavaMethod resolveToJavaMethod(JavaType containedIn) {
        return new JavaMethod(containedIn, this.access, this.name, this.desc);
    }
}
