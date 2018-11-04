package org.classHierarchy.counting;

import org.asm.JarFile;
import org.classHierarchy.ClassHierarchyVisitor;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;

/**
 * Counts the classes, interfaces and methods in a class hierarchy.
 */
public class ClassAndMethodCounter extends ClassHierarchyVisitor {

    private CountResults countResults = new CountResults();
    private JarFile projectFile;
    
    public ClassAndMethodCounter(JarFile projectFile) {
        if(projectFile == null ) { throw new IllegalArgumentException("Parameter 'projectFile' cannot be null."); }
        
        this.projectFile = projectFile;
    }
    
    public CountResults countResults() {
        return this.countResults;
    }
    
    @Override
    public void visitPublicClass(JavaType javaClass) { 
        
        this.countResults.all_classCount++;
        this.countResults.all_publicClassCount++;

        if(javaClass.isLoadedFrom(this.projectFile)) {
            this.countResults.project_classCount++;
            this.countResults.project_publicClassCount++;
        } else {
            this.countResults.libraries_classCount++;
            this.countResults.libraries_publicClassCount++;
        }
    }
    
    @Override
    public void visitPackagePrivateClass(JavaType javaClass) { 
        
        this.countResults.all_classCount++;
        this.countResults.all_packageVisibleClassCount++;

        if(javaClass.isLoadedFrom(this.projectFile)) {
            this.countResults.project_classCount++;
            this.countResults.project_packageVisibleClassCount++;

            this.countPackagePrivateSubClasses(javaClass);
            
            this.countOverridenMethodsByPackagePrivateClasses((JavaClass)javaClass);
        } else {
            this.countResults.libraries_classCount++;
            this.countResults.libraries_packageVisibleClassCount++;
        }
    }
    
    @Override
    public void visitPublicInterface(JavaType javaInterface) { 
        
        this.countResults.all_interfaceCount++;
        this.countResults.all_publicInterfaceCount++;

        if(javaInterface.isLoadedFrom(this.projectFile)) {
            this.countResults.project_interfaceCount++;
            this.countResults.project_publicInterfaceCount++;
        } else {
            this.countResults.libraries_interfaceCount++;
            this.countResults.libraries_publicInterfaceCount++;
        }
    }
    
    @Override
    public void visitPackagePrivateInterface(JavaType javaInterface) { 
        
        this.countResults.all_interfaceCount++;
        this.countResults.all_packageVisibleInterfaceCount++;

        if(javaInterface.isLoadedFrom(this.projectFile)) {
            this.countResults.project_interfaceCount++;
            this.countResults.project_packageVisibleInterfaceCount++;
        } else {
            this.countResults.libraries_interfaceCount++;
            this.countResults.libraries_packageVisibleInterfaceCount++;
        }
    }
    
    @Override
    public void visitPublicMethod(JavaMethod javaMethod) { 
        
        this.countResults.all_methodCount++;
        this.countResults.all_publicMethods++;

        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.countResults.project_methodCount++;
            this.countResults.project_publicMethods++;
        } else {
            this.countResults.libraries_methodCount++;
            this.countResults.libraries_publicMethods++;
        }
    }

    @Override
    public void visitProtectedMethod(JavaMethod javaMethod) { 
        
        this.countResults.all_methodCount++;
        this.countResults.all_protectedMethods++;

        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.countResults.project_methodCount++;
            this.countResults.project_protectedMethods++;
        } else {
            this.countResults.libraries_methodCount++;
            this.countResults.libraries_protectedMethods++;
        }
    }

    @Override
    public void visitPackagePrivateMethod(JavaMethod javaMethod) { 
        
        this.countResults.all_methodCount++;
        this.countResults.all_packagePrivateMethods++;

        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.countResults.project_methodCount++;
            this.countResults.project_packagePrivateMethods++;
        } else {
            this.countResults.libraries_methodCount++;
            this.countResults.libraries_packagePrivateMethods++;
        }
    }

    @Override
    public void visitPrivateMethod(JavaMethod javaMethod) { 
        
        this.countResults.all_methodCount++;
        this.countResults.all_privateMethods++;

        if(javaMethod.isLoadedFrom(this.projectFile)) {
            this.countResults.project_methodCount++;
            this.countResults.project_privateMethods++;
        } else {
            this.countResults.libraries_methodCount++;
            this.countResults.libraries_privateMethods++;
        }
    }
    
    private void countPackagePrivateSubClasses(JavaType javaClass) {
        
        boolean hasPublicSubClass = false;
        boolean hasPackagePrivateSubClass = false;
        
        for(JavaType subClass : javaClass.coneSet()) {
            if(!subClass.equals(javaClass)) {
                hasPublicSubClass = hasPublicSubClass || subClass.isPublic();
                hasPackagePrivateSubClass = hasPackagePrivateSubClass || subClass.isPackagePrivate();
            }
        }
        
        if(hasPackagePrivateSubClass) {
            if(hasPublicSubClass) {
                this.countResults.project_packageVisibleClassWithPackageVisibleAndPublicSubClassCount++;
            } else {
                this.countResults.project_packageVisibleClassWithPackageVisibleSubClassCount++;
            }
        }
    }
    
    private void countOverridenMethodsByPackagePrivateClasses(JavaClass javaClass) {
        
        if(javaClass.isFinalPackagePrivate()) {

            for(JavaMethod declaredMethod : javaClass.declaredMethods()) {
                if(declaredMethod.overrides() != null ) {
                    
                    if(declaredMethod.overrides().containedIn().id().equalsIgnoreCase("java/lang/Object")) {
                        this.countResults.project_packageVisibleClassOverrideObjectMethods += 1;
                    } else {
                        this.countResults.project_packageVisibleClassOverrideNonObjectMethods += 1;
                    }
                }
            }

            if(javaClass.superClass().id().equals("java/lang/Object")) {
                this.countResults.project_packageVisibleClassInheritFromJavaLangObject += 1;
            } else {
                this.countResults.project_packageVisibleClassInheritFromOther += 1;
            }
        }
    }
}
