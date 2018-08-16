package org.classHierarchy.entryPoints;

import org.asm.JarFile;
import org.classHierarchy.JavaMethod;
import org.classHierarchy.JavaType;

/**
 * Collects the exported methods as defined in Rapid Type Analysis.
 */
public class ExportedMethodCollector extends ProjectEntryPointCollector {
    
    public ExportedMethodCollector(JarFile projectFile) {
        super(projectFile);
    }

    @Override
    public boolean isEntryPoint(JavaMethod javaMethod) {
        
        JavaType declType = javaMethod.containedIn();
        return !javaMethod.isAbstract() && !javaMethod.isSynthetic() && declType.isPublic()
            && ((javaMethod.isPublic() || (javaMethod.isProtected() && !declType.isFinal())) || declType.isInterface());
    }
}
