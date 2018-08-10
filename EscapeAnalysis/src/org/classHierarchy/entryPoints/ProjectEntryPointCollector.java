package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.ClassHierarchy;
import org.classHierarchy.ClassHierarchyVisitor;
import org.classHierarchy.tree.JavaMethod;
import org.classHierarchy.tree.JavaMethodSet;

abstract class ProjectEntryPointCollector extends ClassHierarchyVisitor {

    private JarFile projectFile;
    private JavaMethodSet entryPoints;
    
    protected ProjectEntryPointCollector(JarFile projectFile) {
        if(projectFile == null) { throw new IllegalArgumentException("Parameter 'projectFile' cannot be null."); }
        
        this.projectFile = projectFile;
    }

    public JavaMethodSet collectEntryPointsFrom(ClassHierarchy classHierarchy) {
        this.entryPoints = new JavaMethodSet();
        classHierarchy.accept(this);
        return this.entryPoints;
    }

    protected void addEntryPoint(JavaMethod entryPoint) {
        this.entryPoints.add(entryPoint);
    }
    
    protected abstract void visitProjectMethod(JavaMethod javaMethod);

    @Override
    public void visitPublicMethod(JavaMethod javaMethod) { 
        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.visitProjectMethod(javaMethod);
        }
    }

    @Override
    public void visitProtectedMethod(JavaMethod javaMethod) { 
        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.visitProjectMethod(javaMethod);
        }
    }

    @Override
    public void visitPackagePrivateMethod(JavaMethod javaMethod) {
        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.visitProjectMethod(javaMethod);
        }
    }

    @Override
    public void visitPrivateMethod(JavaMethod javaMethod) { 
        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.visitProjectMethod(javaMethod);
        }
    }
}
