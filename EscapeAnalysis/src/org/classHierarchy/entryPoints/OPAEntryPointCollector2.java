package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;
import org.classHierarchy.JavaType;

public class OPAEntryPointCollector2 extends ReifEntryPointCollector{

    public OPAEntryPointCollector2(JarFile projectFile, JavaMethodSet factoryMethods) {
        super(projectFile, factoryMethods);
    }

    @Override
    protected void visitProjectMethod(JavaMethod javaMethod) {
        if(this.isEntryPoint(javaMethod)) {
            this.addEntryPoint(javaMethod);
        }
    }

    private boolean isEntryPoint(JavaMethod javaMethod) {
        
        JavaType declType = javaMethod.containedIn();
        
        if(declType.isInterface()) {
            if(javaMethod.isPublic()) {
                return true;
            }
            else {
                return this.isClientCallable(javaMethod);
            }
        }
        
        if(this.isPotentiallySerializationRelated(javaMethod)) {
            return true;
        }
        
        if(javaMethod.isPrivate()) {
            return false;
        }
        
        boolean isInstantiable = this.typeIsInstantiable(declType);
        
        if(isInstantiable && javaMethod.isStaticInitializer()) {
            return true;
        }
        else {
            boolean isAbstractOrInterface = declType.isAbstract() || declType.isInterface();
            boolean isClassLocal = javaMethod.isPrivate();
            boolean isGlobal = javaMethod.isPublic();
            boolean isStatic = javaMethod.isStatic() && !javaMethod.isStaticInitializer();
            boolean isPackageLocal = false;
            
            if(isClassLocal) {
                return false;
            } else if(isGlobal && (isInstantiable || isStatic)) {
                return true;
            } else if(isGlobal && !isInstantiable && isAbstractOrInterface || isPackageLocal) {
                return this.isClientCallable(javaMethod);
            } else {
                return false;
            }
        }
        
        
    }
    
    private boolean isClientCallable(JavaMethod javaMethod) {
        JavaType declType = javaMethod.containedIn();
        
        return (javaMethod.isPublic() || javaMethod.isProtected()) &&
            (declType.isPublic() || 
                javaMethod.appliesTo().containsPublicType());
    }
}
