package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaMethod;

public class OldEntryPointCollector extends ReifEntryPointCollector {

    public OldEntryPointCollector(JarFile projectFile) {
        super(projectFile);
    }
    
    @Override
    public void visitProjectMethod(JavaMethod javaMethod) {
        
        if(javaMethod.isAbstract()) {
            return;
        }
        
        boolean classIsInstantiable = typeIsInstantiable(javaMethod.containedIn());
        boolean isImplicitlyUsed = false;
        
        if ((classIsInstantiable || javaMethod.containedIn().isInterface() || javaMethod.isStatic()) &&
                (!javaMethod.isPrivate() || this.isPotentiallySerializationRelated(javaMethod))) {
            this.addEntryPoint(javaMethod);
        } else if (isImplicitlyUsed) {
            this.addEntryPoint(javaMethod);
        }
    }
    
    @Override
    protected boolean classIsInstantiable(JavaClass javaClass) {
        boolean hasFactoryMethod = false;
        
        for(JavaMethod declaredMethod : javaClass.declaredMethods()) {
            if(declaredMethod.isFactoryMethod()) {
                hasFactoryMethod = true;
            }
            if(declaredMethod.isStatic() && !declaredMethod.isStaticInitializer() && declaredMethod.isNative()) {
                hasFactoryMethod = true;
            }
        }
        
        return (javaClass.hasNonPrivateConstructor() || hasFactoryMethod
                || javaClass.isSubTypeOf("java/io/Serializable"));        
    }
}
