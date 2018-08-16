package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.JavaClass;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaMethodSet;

public class OldEntryPointCollector extends ReifEntryPointCollector {
    
    public OldEntryPointCollector(JarFile projectFile, JavaMethodSet factoryMethods) {
        super(projectFile, factoryMethods);
    }
    
    @Override
    public boolean isEntryPoint(JavaMethod javaMethod) {
        
        boolean classIsInstantiable = typeIsInstantiable(javaMethod.containedIn());
        boolean isImplicitlyUsed = false;
        
        if ((classIsInstantiable || javaMethod.containedIn().isInterface() || javaMethod.isStatic()) &&
                (!javaMethod.isPrivate() || this.isPotentiallySerializationRelated(javaMethod))) {
            return true;
        } else if (isImplicitlyUsed) {
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    protected boolean classIsInstantiable(JavaClass javaClass) {
        
        boolean hasFactoryMethod = this.factoryMethods.overlapsWith(javaClass.declaredMethods());
        
        return (javaClass.hasNonPrivateConstructor() || hasFactoryMethod
                || javaClass.isSubTypeOf("java/io/Serializable"));        
    }
}