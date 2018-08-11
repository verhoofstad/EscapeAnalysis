package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;

public class CPAEntryPointCollector extends ReifEntryPointCollector {

    public CPAEntryPointCollector(JarFile projectFile) {
        super(projectFile);
    }

    @Override
    public void visitProjectMethod(JavaMethod javaMethod) {
        
        if(this.isEntryPoint(javaMethod)) {
            this.addEntryPoint(javaMethod);
        }
    }
    
    private boolean isEntryPoint(JavaMethod javaMethod) {
        JavaType declType = javaMethod.containedIn();
        
        return this.maybeCalledByTheJVM(javaMethod) ||
            (javaMethod.isStaticInitializer() && (javaMethod.isPublic()) && declType.isAccessible()) ||
            (this.isClientCallable(javaMethod) &&
                ((javaMethod.isStatic() && !javaMethod.isStaticInitializer()) || this.typeIsInstantiable(declType)));
    }

    private boolean isClientCallable(JavaMethod javaMethod) {
        JavaType declType = javaMethod.containedIn();
        
        return (javaMethod.isPublic() || javaMethod.isProtected()) &&
            (declType.isPublic() || 
                javaMethod.appliesTo().containsPublicType());
    }
    
    @Override
    protected boolean classIsInstantiable(JavaClass javaClass) {
        return super.classIsInstantiable(javaClass) && javaClass.isAccessible();
    }
}