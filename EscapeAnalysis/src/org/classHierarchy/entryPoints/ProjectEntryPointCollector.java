package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.ConcreteMethodVisitor;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

abstract class ProjectEntryPointCollector extends ConcreteMethodVisitor {

    private JarFile projectFile;
    private JavaMethodSet entryPoints;
    
    protected ProjectEntryPointCollector(JarFile projectFile) {
        if(projectFile == null) { throw new IllegalArgumentException("Parameter 'projectFile' cannot be null."); }
        
        this.projectFile = projectFile;
    }

    protected abstract boolean isEntryPoint(JavaMethod javaMethod);

    public JavaMethodSet collectEntryPointsFrom(ClassHierarchy classHierarchy) {
        this.entryPoints = new JavaMethodSet();
        classHierarchy.accept(this);
        return this.entryPoints;
    }
    
    @Override
    public void visitConcreteMethod(JavaMethod javaMethod) { 
        if(javaMethod.isLoadedFrom(this.projectFile) && this.isEntryPoint(javaMethod)) {
            this.entryPoints.add(javaMethod);
        }
    }
}
